package com.bergerkiller.bukkit.common.internal.network;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.Conversion;
import com.bergerkiller.bukkit.common.events.PacketReceiveEvent;
import com.bergerkiller.bukkit.common.events.PacketSendEvent;
import com.bergerkiller.bukkit.common.internal.PacketHandler;
import com.bergerkiller.bukkit.common.protocol.CommonPacket;
import com.bergerkiller.bukkit.common.protocol.PacketListener;
import com.bergerkiller.bukkit.common.protocol.PacketMonitor;
import com.bergerkiller.bukkit.common.protocol.PacketType;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.PlayerUtil;
import com.bergerkiller.generated.net.minecraft.server.EntityPlayerHandle;
import com.bergerkiller.mountiplex.reflection.SafeMethod;
import com.bergerkiller.reflection.net.minecraft.server.NMSPlayerConnection;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;

/**
 * Basic packet handler implementation for handling packets using a send/receive
 * hook. The
 * {@link #handlePacketSend(Player, Object, boolean) handlePacketSend(player, packet, wasCancelled)}
 * and
 * {@link #handlePacketReceive(Player, Object, boolean) handlePacketReceive(player, packet, wasCancelled)}
 * methods should be called by an additional listener hook.
 */
public abstract class PacketHandlerHooked implements PacketHandler {

    private final Map<PacketType, List<PacketListener>> listeners = new HashMap<PacketType, List<PacketListener>>();
    private final Map<PacketType, List<PacketMonitor>> monitors = new HashMap<PacketType, List<PacketMonitor>>();
    private final Map<Plugin, List<PacketListener>> listenerPlugins = new HashMap<Plugin, List<PacketListener>>();
    private final Map<Plugin, List<PacketMonitor>> monitorPlugins = new HashMap<Plugin, List<PacketMonitor>>();
    private final ClassMap<SafeMethod<?>> receiverMethods = new ClassMap<SafeMethod<?>>();
    private final LinkedList<SilentPacket> silentQueue = new LinkedList<SilentPacket>();

    @Override
    public boolean onEnable() {
        // Initialize all receiver methods
        Class<?> packetType = PacketType.DEFAULT.getType();
        for (Method method : NMSPlayerConnection.T.getType().getDeclaredMethods()) {
            if (method.getReturnType() != void.class || method.getParameterTypes().length != 1
                    || !Modifier.isPublic(method.getModifiers())) {
                continue;
            }
            Class<?> arg = method.getParameterTypes()[0];
            if (!packetType.isAssignableFrom(arg) || arg == packetType) {
                continue;
            }
            receiverMethods.put(arg, new SafeMethod<Void>(method));
        }
        return true;
    }

    @Override
    public void removePacketListeners(Plugin plugin) {
        // Listeners
        List<PacketListener> listeners = listenerPlugins.get(plugin);
        if (listeners != null) {
            for (PacketListener listener : listeners) {
                removePacketListener(listener, false);
            }
        }
        // Monitors
        List<PacketMonitor> monitors = monitorPlugins.get(plugin);
        if (monitors != null) {
            for (PacketMonitor monitor : monitors) {
                removePacketMonitor(monitor, false);
            }
        }
    }

    @Override
    public void removePacketMonitor(PacketMonitor monitor) {
        removePacketMonitor(monitor, true);
    }

    private void removePacketMonitor(PacketMonitor monitor, boolean fromPlugins) {
        if (monitor == null) {
            return;
        }
        for (List<PacketMonitor> monitorList : monitors.values()) {
            monitorList.remove(monitor);
        }
        if (fromPlugins) {
            // Remove from plugin list
            for (Plugin plugin : monitorPlugins.keySet().toArray(new Plugin[0])) {
                List<PacketMonitor> list = monitorPlugins.get(plugin);
                // If not null, remove the monitor, if empty afterwards remove the entire entry
                if (list != null && list.remove(monitor) && list.isEmpty()) {
                    monitorPlugins.remove(plugin);
                }
            }
        }
    }

    @Override
    public void removePacketListener(PacketListener listener) {
        removePacketListener(listener, true);
    }

    private void removePacketListener(PacketListener listener, boolean fromPlugins) {
        if (listener == null) {
            return;
        }
        for (List<PacketListener> listenerList : listeners.values()) {
            listenerList.remove(listener);
        }
        if (fromPlugins) {
            // Remove from plugin list
            for (Plugin plugin : listenerPlugins.keySet().toArray(new Plugin[0])) {
                List<PacketListener> list = listenerPlugins.get(plugin);
                // If not null, remove the listener, if empty afterwards remove the entire entry
                if (list != null && list.remove(listener) && list.isEmpty()) {
                    listenerPlugins.remove(plugin);
                }
            }
        }
    }

    @Override
    public void addPacketMonitor(Plugin plugin, PacketMonitor monitor, PacketType[] types) {
        if (monitor == null) {
            throw new IllegalArgumentException("Monitor is not allowed to be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        // Register the listener
        for (PacketType type : types) {
            // Map to listener array
            List<PacketMonitor> monitorList = monitors.get(type);
            if (monitorList == null) {
                monitorList = new ArrayList<PacketMonitor>();
                monitors.put(type, monitorList);
            }
            monitorList.add(monitor);
            // Map to plugin list
            List<PacketMonitor> list = monitorPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketMonitor>(2);
                monitorPlugins.put(plugin, list);
            }
            list.add(monitor);
        }
    }

    @Override
    public void addPacketListener(Plugin plugin, PacketListener listener, PacketType[] types) {
        if (listener == null) {
            throw new IllegalArgumentException("Listener is not allowed to be null");
        } else if (plugin == null) {
            throw new IllegalArgumentException("Plugin is not allowed to be null");
        }
        // Register the listener
        for (PacketType type : types) {
            // Map to listener array
            List<PacketListener> listenerList = listeners.get(type);
            if (listenerList == null) {
                listenerList = new ArrayList<PacketListener>();
                listeners.put(type, listenerList);
            }
            listenerList.add(listener);
            // Map to plugin list
            List<PacketListener> list = listenerPlugins.get(plugin);
            if (list == null) {
                list = new ArrayList<PacketListener>(2);
                listenerPlugins.put(plugin, list);
            }
            list.add(listener);
        }
    }

    @Override
    public void receivePacket(Player player, Object packet) {
        SafeMethod<?> method = this.receiverMethods.get(packet);
        if (method == null) {
        	Logging.LOGGER_NETWORK.log(Level.WARNING, "Could not find suitable packet handler for " + packet.getClass().getSimpleName());
        } else {
            method.invoke(getPlayerConnection(player), packet);
        }
    }

    @Override
    public void sendPacket(Player player, Object packet, boolean throughListeners) {
        Object handle = Conversion.toEntityHandle.convert(player);
        if (!handle.getClass().equals(CommonUtil.getNMSClass("EntityPlayer"))) {
            return;
        }
        if (!PacketType.DEFAULT.isInstance(packet) || PlayerUtil.isDisconnected(player)) {
            return;
        }

        if (!throughListeners) {
            synchronized (silentQueue) {
                silentQueue.addLast(new SilentPacket(player, packet));
            }
        }

        final Object connection = EntityPlayerHandle.T.playerConnection.get(handle);
        NMSPlayerConnection.sendPacket(connection, packet);
    }

    @Override
    public Collection<Plugin> getListening(PacketType type) {
        List<PacketListener> listenerList = listeners.get(type);
        if (listenerList == null) {
            return Collections.emptySet();
        }
        List<Plugin> plugins = new ArrayList<Plugin>();
        for (Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
            for (PacketListener listener : listenerList) {
                if (entry.getValue().contains(listener)) {
                    plugins.add(entry.getKey());
                    break;
                }
            }
        }
        return plugins;
    }

    @Override
    public void transfer(PacketHandler to) {
        for (Entry<Plugin, List<PacketListener>> entry : listenerPlugins.entrySet()) {
            for (PacketListener listener : entry.getValue()) {
                to.addPacketListener(entry.getKey(), listener, getListenerTypes(listener));
            }
        }
        for (Entry<Plugin, List<PacketMonitor>> entry : monitorPlugins.entrySet()) {
            for (PacketMonitor listener : entry.getValue()) {
                to.addPacketMonitor(entry.getKey(), listener, getMonitorTypes(listener));
            }
        }
    }

    protected Object getPlayerConnection(Player player) {
        return EntityPlayerHandle.T.playerConnection.get(Conversion.toEntityHandle.convert(player));
    }

    private PacketType[] getListenerTypes(PacketListener listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketListener>> entry : listeners.entrySet()) {
            if (entry.getValue().contains(listener)) {
                list.add(entry.getKey());
            }
        }
        return list.toArray(new PacketType[list.size()]);
    }

    private PacketType[] getMonitorTypes(PacketMonitor listener) {
        ArrayList<PacketType> list = new ArrayList<PacketType>();
        for (Map.Entry<PacketType, List<PacketMonitor>> entry : monitors.entrySet()) {
            if (entry.getValue().contains(listener)) {
                list.add(entry.getKey());
            }
        }
        return list.toArray(new PacketType[list.size()]);
    }

    /**
     * Handles a packet before it is being sent to a player
     *
     * @param player for which the packet was meant
     * @param packet that is handled
     * @param wasCancelled - True if it was originally cancelled, False if not
     * @return True if the packet is allowed to be sent, False if not
     */
    public boolean handlePacketSend(Player player, Object packet, boolean wasCancelled) {
        if (player == null || packet == null) {
            return true;
        }

        // Check if silent
        boolean is_silent = false;
        if (!silentQueue.isEmpty()) {
            long time = System.currentTimeMillis();
            synchronized (silentQueue) {
                ListIterator<SilentPacket> iter = silentQueue.listIterator();
                while (iter.hasNext()) {
                    SilentPacket sp = iter.next();
                    if (time >= sp.timeout) {
                        iter.remove();
                    } else if (sp.player == player && sp.packet == packet) {
                        is_silent = true;
                        iter.remove();
                    }
                }
            }
        }

        // Handle listeners
        PacketType type = PacketType.getType(packet);
        if (!is_silent) {
            List<PacketListener> listenerList = listeners.get(type);
            if (listenerList != null) {
                CommonPacket cp = new CommonPacket(packet, type);
                PacketSendEvent ev = new PacketSendEvent(player, cp);
                ev.setCancelled(wasCancelled);
                for (PacketListener listener : listenerList) {
                    listener.onPacketSend(ev);
                }
                if (ev.isCancelled()) {
                    return false;
                }
            }
        }

        // Handle monitors
        handlePacketSendMonitor(player, type, packet);
        return true;
    }

    private void handlePacketSendMonitor(Player player, PacketType packetType, Object packet) {
        List<PacketMonitor> monitorList = monitors.get(packetType);
        if (monitorList != null) {
            CommonPacket cp = new CommonPacket(packet, packetType);
            for (PacketMonitor monitor : monitorList) {
                monitor.onMonitorPacketSend(cp, player);
            }
        }
    }

    /**
     * Handles a packet before it is being handled by the server
     *
     * @param player from which the packet came
     * @param packet that is handled
     * @param wasCancelled - True if the packet is allowed to be received, False
     * if not
     * @return True if the packet is allowed to be received, False if not
     */
    public boolean handlePacketReceive(Player player, Object packet, boolean wasCancelled) {
        if (player == null || packet == null) {
            return true;
        }
        // Handle listeners
        PacketType type = PacketType.getType(packet);
        List<PacketListener> listenerList = listeners.get(type);
        if (listenerList != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            PacketReceiveEvent ev = new PacketReceiveEvent(player, cp);
            ev.setCancelled(wasCancelled);
            for (PacketListener listener : listenerList) {
                listener.onPacketReceive(ev);
            }
            if (ev.isCancelled()) {
                return false;
            }
        }
        // Handle monitors
        List<PacketMonitor> monitorList = monitors.get(type);
        if (monitorList != null) {
            CommonPacket cp = new CommonPacket(packet, type);
            for (PacketMonitor monitor : monitorList) {
                monitor.onMonitorPacketReceive(cp, player);
            }
        }
        return true;
    }

    private static class SilentPacket {
        public final Player player;
        public final Object packet;
        public final long timeout;

        public SilentPacket(Player player, Object packet) {
            this.player = player;
            this.packet = packet;
            this.timeout = System.currentTimeMillis() + 5000; // remove after 5s
        }
    }
}
