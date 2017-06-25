package com.bergerkiller.bukkit.common.protocol;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.collections.ClassMap;
import com.bergerkiller.bukkit.common.conversion.DuplexConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.mountiplex.reflection.ClassTemplate;
import com.bergerkiller.mountiplex.reflection.FieldAccessor;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.reflection.net.minecraft.server.NMSDataWatcher;
import com.bergerkiller.reflection.net.minecraft.server.NMSEnumProtocol;
import com.bergerkiller.reflection.net.minecraft.server.NMSPacketClasses.*;

import java.util.logging.Level;

public class PacketType extends ClassTemplate<Object> {

    private static final ClassMap<PacketType> typesByPacketClass = new ClassMap<PacketType>();

    /*
     * ========================
     * === Outgoing packets ===
     * ========================
     */
    /* Misc. packets */
    public static final NMSPacket DEFAULT = new NMSPacket();
    public static final NMSPacketPlayOutAbilities OUT_ABILITIES = new NMSPacketPlayOutAbilities();
    public static final NMSPacketPlayOutBed OUT_BED = new NMSPacketPlayOutBed();
    public static final NMSPacketPlayOutBlockAction OUT_BLOCK_ACTION = new NMSPacketPlayOutBlockAction();
    public static final NMSPacketPlayOutChat OUT_CHAT = new NMSPacketPlayOutChat();
    public static final NMSPacketPlayOutCollect OUT_COLLECT = new NMSPacketPlayOutCollect();
    public static final NMSPacketPlayOutCustomPayload OUT_CUSTOM_PAYLOAD = new NMSPacketPlayOutCustomPayload();
    public static final NMSPacketPlayOutExperience OUT_EXPERIENCE = new NMSPacketPlayOutExperience();
    public static final NMSPacketPlayOutExplosion OUT_EXPLOSION = new NMSPacketPlayOutExplosion();
    public static final NMSPacketPlayOutGameStateChange OUT_GAME_STATE_CHANGE = new NMSPacketPlayOutGameStateChange();
    public static final NMSPacketPlayOutHeldItemSlot OUT_HELD_ITEM_SLOT = new NMSPacketPlayOutHeldItemSlot();
    public static final NMSPacketPlayOutKeepAlive OUT_KEEP_ALIVE = new NMSPacketPlayOutKeepAlive();
    public static final NMSPacketPlayOutKickDisconnect OUT_KICK_DISCONNECT = new NMSPacketPlayOutKickDisconnect();
    public static final NMSPacketPlayOutLogin OUT_LOGIN = new NMSPacketPlayOutLogin();
    public static final NMSPacketPlayOutMap OUT_MAP = new NMSPacketPlayOutMap();
    public static final NMSPacketPlayOutMapChunk OUT_MAP_CHUNK = new NMSPacketPlayOutMapChunk();
    public static final NMSPacketPlayOutPlayerListHeaderFooter OUT_PLAYER_LIST_HEADER_FOOTER = new NMSPacketPlayOutPlayerListHeaderFooter();
    public static final NMSPacketPlayOutMultiBlockChange OUT_MULTI_BLOCK_CHANGE = new NMSPacketPlayOutMultiBlockChange();
    public static final NMSPacketPlayOutNamedSoundEffect OUT_NAMED_SOUND_EFFECT = new NMSPacketPlayOutNamedSoundEffect();
    public static final NMSPacketPlayOutOpenSignEditor OUT_OPEN_SIGN_EDITOR = new NMSPacketPlayOutOpenSignEditor();
    public static final NMSPacketPlayOutPlayerInfo OUT_PLAYER_INFO = new NMSPacketPlayOutPlayerInfo();
    public static final NMSPacketPlayOutPosition OUT_POSITION = new NMSPacketPlayOutPosition();
    public static final NMSPacketPlayOutRespawn OUT_RESPAWN = new NMSPacketPlayOutRespawn();
    public static final NMSPacketPlayOutSpawnPosition OUT_SPAWN_POSITION = new NMSPacketPlayOutSpawnPosition();
    public static final NMSPacketPlayOutStatistic OUT_STATISTIC = new NMSPacketPlayOutStatistic();
    public static final NMSPacketPlayOutTabComplete OUT_TAB_COMPLETE = new NMSPacketPlayOutTabComplete();
    public static final NMSPacketPlayOutTileEntityData OUT_TILE_ENTITY_DATA = new NMSPacketPlayOutTileEntityData();
    public static final NMSPacketPlayOutTitle OUT_TITLE = new NMSPacketPlayOutTitle();
    public static final NMSPacketPlayOutUpdateHealth OUT_UPDATE_HEALTH = new NMSPacketPlayOutUpdateHealth();
    public static final NMSPacketPlayOutUpdateTime OUT_UPDATE_TIME = new NMSPacketPlayOutUpdateTime();
    public static final NMSPacketPlayOutWorldBorder OUT_WORLD_BORDER = new NMSPacketPlayOutWorldBorder();
    public static final NMSPacketPlayOutWorldEvent OUT_WORLD_EVENT = new NMSPacketPlayOutWorldEvent();
    public static final NMSPacketPlayOutWorldParticles OUT_WORLD_PARTICLES = new NMSPacketPlayOutWorldParticles();
    public static final NMSPacketPlayOutBlockBreakAnimation OUT_BLOCK_BREAK_ANIMATION = new NMSPacketPlayOutBlockBreakAnimation();
    public static final NMSPacketPlayOutBlockChange OUT_BLOCK_CHANGE = new NMSPacketPlayOutBlockChange();
    public static final NMSPacketPlayOutBoss OUT_BOSS = new NMSPacketPlayOutBoss();
    public static final NMSPacketPlayOutCamera OUT_CAMERA = new NMSPacketPlayOutCamera();
    public static final NMSPacketPlayOutCombatEvent OUT_COMBAT_EVENT = new NMSPacketPlayOutCombatEvent();
    public static final NMSPacketPlayOutCustomSoundEffect OUT_CUSTOM_SOUND_EFFECT = new NMSPacketPlayOutCustomSoundEffect();
    public static final NMSPacketPlayOutResourcePackSend OUT_RESOURCE_PACK_SEND = new NMSPacketPlayOutResourcePackSend();
    public static final NMSPacketPlayOutServerDifficulty OUT_SERVER_DIFFICULTY = new NMSPacketPlayOutServerDifficulty();
    public static final NMSPacketPlayOutSetCooldown OUT_SET_COOLDOWN = new NMSPacketPlayOutSetCooldown();
    public static final NMSPacketPlayOutUnloadChunk OUT_UNLOAD_CHUNK = new NMSPacketPlayOutUnloadChunk();
    /* Scoreboard-related packets */
    public static final NMSPacketPlayOutScoreboardDisplayObjective OUT_SCOREBOARD_DISPLAY_OBJECTIVE = new NMSPacketPlayOutScoreboardDisplayObjective();
    public static final NMSPacketPlayOutScoreboardObjective OUT_SCOREBOARD_OBJECTIVE = new NMSPacketPlayOutScoreboardObjective();
    public static final NMSPacketPlayOutScoreboardScore OUT_SCOREBOARD_SCORE = new NMSPacketPlayOutScoreboardScore();
    public static final NMSPacketPlayOutScoreboardTeam OUT_SCOREBOARD_TEAM = new NMSPacketPlayOutScoreboardTeam();
    /* Window-related packets */
    public static final NMSPacketPlayOutCloseWindow OUT_WINDOW_CLOSE = new NMSPacketPlayOutCloseWindow();
    public static final NMSPacketPlayOutWindowData OUT_WINDOW_DATA = new NMSPacketPlayOutWindowData();
    public static final NMSPacketPlayOutOpenWindow OUT_WINDOW_OPEN = new NMSPacketPlayOutOpenWindow();
    public static final NMSPacketPlayOutSetSlot OUT_WINDOW_SET_SLOT = new NMSPacketPlayOutSetSlot();
    public static final NMSPacketPlayOutTransaction OUT_WINDOW_TRANSACTION = new NMSPacketPlayOutTransaction();
    public static final NMSPacketPlayOutWindowItems OUT_WINDOW_ITEMS = new NMSPacketPlayOutWindowItems();
    /* Entity-related packets */
    public static final NMSPacketPlayOutEntity OUT_ENTITY = new NMSPacketPlayOutEntity();
    public static final NMSPacketPlayOutSpawnEntity OUT_ENTITY_SPAWN = new NMSPacketPlayOutSpawnEntity();
    public static final NMSPacketPlayOutNamedEntitySpawn OUT_ENTITY_SPAWN_NAMED = new NMSPacketPlayOutNamedEntitySpawn();
    public static final NMSPacketPlayOutSpawnEntityExperienceOrb OUT_ENTITY_SPAWN_EXPORB = new NMSPacketPlayOutSpawnEntityExperienceOrb();
    public static final NMSPacketPlayOutSpawnEntityLiving OUT_ENTITY_SPAWN_LIVING = new NMSPacketPlayOutSpawnEntityLiving();
    public static final NMSPacketPlayOutSpawnEntityPainting OUT_ENTITY_SPAWN_PAINTING = new NMSPacketPlayOutSpawnEntityPainting();
    public static final NMSPacketPlayOutSpawnEntityWeather OUT_ENTITY_SPAWN_WITHER = new NMSPacketPlayOutSpawnEntityWeather();
    public static final NMSPacketPlayOutEntityDestroy OUT_ENTITY_DESTROY = new NMSPacketPlayOutEntityDestroy();
    public static final NMSPacketPlayOutAttachEntity OUT_ENTITY_ATTACH = new NMSPacketPlayOutAttachEntity();
    public static final NMSPacketPlayOutEntityEffect OUT_ENTITY_EFFECT_ADD = new NMSPacketPlayOutEntityEffect();
    public static final NMSPacketPlayOutRemoveEntityEffect OUT_ENTITY_EFFECT_REMOVE = new NMSPacketPlayOutRemoveEntityEffect();
    public static final NMSPacketPlayOutEntityEquipment OUT_ENTITY_EQUIPMENT = new NMSPacketPlayOutEntityEquipment();
    public static final NMSPacketPlayOutEntityHeadRotation OUT_ENTITY_HEAD_ROTATION = new NMSPacketPlayOutEntityHeadRotation();
    public static final NMSPacketPlayOutEntityLook OUT_ENTITY_LOOK = new NMSPacketPlayOutEntityLook();
    public static final NMSPacketPlayOutAnimation OUT_ENTITY_ANIMATION = new NMSPacketPlayOutAnimation();
    public static final NMSPacketPlayOutEntityMetadata OUT_ENTITY_METADATA = new NMSPacketPlayOutEntityMetadata();
    public static final NMSPacketPlayOutEntityStatus OUT_ENTITY_STATUS = new NMSPacketPlayOutEntityStatus();
    public static final NMSPacketPlayOutEntityTeleport OUT_ENTITY_TELEPORT = new NMSPacketPlayOutEntityTeleport();
    public static final NMSPacketPlayOutEntityVelocity OUT_ENTITY_VELOCITY = new NMSPacketPlayOutEntityVelocity();
    public static final NMSPacketPlayOutRelEntityMove OUT_ENTITY_MOVE = new NMSPacketPlayOutRelEntityMove();
    public static final NMSPacketPlayOutRelEntityMoveLook OUT_ENTITY_MOVE_LOOK = new NMSPacketPlayOutRelEntityMoveLook();
    public static final NMSPacketPlayOutUpdateAttributes OUT_ENTITY_UPDATE_ATTRIBUTES = new NMSPacketPlayOutUpdateAttributes();
    public static final NMSPacketPlayOutMount OUT_MOUNT = new NMSPacketPlayOutMount();
    public static final NMSPacketPlayOutVehicleMove OUT_VEHICLE_MOVE = new NMSPacketPlayOutVehicleMove();

    /*
     * ========================
     * === Incoming packets ===
     * ========================
     */
    public static final NMSPacketPlayInAbilities IN_ABILITIES = new NMSPacketPlayInAbilities();
    public static final NMSPacketPlayInArmAnimation IN_ENTITY_ANIMATION = new NMSPacketPlayInArmAnimation();
    public static final NMSPacketPlayInBlockDig IN_BLOCK_DIG = new NMSPacketPlayInBlockDig();
    public static final NMSPacketPlayInBlockPlace IN_BLOCK_PLACE = new NMSPacketPlayInBlockPlace();
    public static final NMSPacketPlayInBoatMove IN_BOAT_MOVE = new NMSPacketPlayInBoatMove();
    public static final NMSPacketPlayInChat IN_CHAT = new NMSPacketPlayInChat();
    public static final NMSPacketPlayInClientCommand IN_CLIENT_COMMAND = new NMSPacketPlayInClientCommand();
    public static final NMSPacketPlayInCustomPayload IN_CUSTOM_PAYLOAD = new NMSPacketPlayInCustomPayload();
    public static final NMSPacketPlayInEntityAction IN_ENTITY_ACTION = new NMSPacketPlayInEntityAction();
    public static final NMSPacketPlayInFlying IN_FLYING = new NMSPacketPlayInFlying();
    public static final NMSPacketPlayInHeldItemSlot IN_HELD_ITEM_SLOT = new NMSPacketPlayInHeldItemSlot();
    public static final NMSPacketPlayInKeepAlive IN_KEEP_ALIVE = new NMSPacketPlayInKeepAlive();
    public static final NMSPacketPlayInSpectate IN_SPECTATE = new NMSPacketPlayInSpectate();
    public static final NMSPacketPlayInSetCreativeSlot IN_SET_CREATIVE_SLOT = new NMSPacketPlayInSetCreativeSlot();
    public static final NMSPacketPlayInSettings IN_SETTINGS = new NMSPacketPlayInSettings();
    public static final NMSPacketPlayInSteerVehicle IN_STEER_VEHICLE = new NMSPacketPlayInSteerVehicle();
    public static final NMSPacketPlayInTabComplete IN_TAB_COMPLETE = new NMSPacketPlayInTabComplete();
    public static final NMSPacketPlayInTeleportAccept IN_TELEPORT_ACCEPT = new NMSPacketPlayInTeleportAccept();
    public static final NMSPacketPlayInUpdateSign IN_UPDATE_SIGN = new NMSPacketPlayInUpdateSign();
    public static final NMSPacketPlayInUseEntity IN_USE_ENTITY = new NMSPacketPlayInUseEntity();
    public static final NMSPacketPlayInUseItem IN_USE_ITEM = new NMSPacketPlayInUseItem();
    public static final NMSPacketPlayInVehicleMove IN_VEHICLE_MOVE = new NMSPacketPlayInVehicleMove();

    /* Window-related packets */
    public static final NMSPacketPlayInCloseWindow IN_WINDOW_CLOSE = new NMSPacketPlayInCloseWindow();
    public static final NMSPacketPlayInEnchantItem IN_WINDOW_ENCHANT_ITEM = new NMSPacketPlayInEnchantItem();
    public static final NMSPacketPlayInResourcePackStatus IN_WINDOW_RESOURCEPACK_STATUS = new NMSPacketPlayInResourcePackStatus();
    public static final NMSPacketPlayInTransaction IN_WINDOW_TRANSACTION = new NMSPacketPlayInTransaction();
    public static final NMSPacketPlayInWindowClick IN_WINDOW_CLICK = new NMSPacketPlayInWindowClick();

    private final int id;
    private final boolean outgoing;
    private final FieldAccessor<DataWatcher> dataWatcherField;

    static {
        //TODO: Check that all NMS packets are actually registered here
    }

    /**
     * Constructor used in PacketTypeClasses to construct by name
     */
    public PacketType() {
        this(null);
    }

    public void init() {

    }

    @SuppressWarnings("unchecked")
    protected PacketType(Class<?> packetClass) {
        // If not specified, resort to using the PacketType class name to obtain the Class
        if (packetClass == null) {
            packetClass = CommonUtil.getNMSClass(getClass().getSimpleName().substring(3));
        }

        if (packetClass == null) {
            //Logging.LOGGER_REFLECTION.warning("Failed to find NMS Packet class type for " + getClass().getSimpleName());
            this.outgoing = false;
            this.id = -1;
            this.dataWatcherField = null;
            return;
        }

        typesByPacketClass.put(packetClass, this);

        // Apply the packet class
        this.setClass((Class<Object>) packetClass);

        // Obtain ID and determine in/outgoing
        Integer tmpId;
        if ((tmpId = NMSEnumProtocol.getPacketIdIn(getType())) != null) {
            this.outgoing = false;
            this.id = tmpId.intValue();
        } else if ((tmpId = NMSEnumProtocol.getPacketIdOut(getType())) != null) {
            this.outgoing = true;
            this.id = tmpId.intValue();
        } else {
            this.outgoing = false;
            this.id = -1;
            if (packetClass == null || !packetClass.equals(CommonUtil.getNMSClass("Packet"))) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Packet '" + getClass().getSimpleName() + " is not registered!");
            }
        }

        // Obtain the datawatcher Field
        FieldAccessor<DataWatcher> dataWatcherField = null;
        for (SafeField<?> field : this.getFields()) {
            if (NMSDataWatcher.T.isType(field.getType())) {
                dataWatcherField = field.translate(DuplexConversion.dataWatcher);
                break;
            }
        }
        this.dataWatcherField = dataWatcherField;
    }

    public boolean isOutGoing() {
        return this.outgoing;
    }

    public int getId() {
        return this.id;
    }

    public Object createPacketHandle() {
        return super.newInstance();
    }

    @Override
    public String toString() {
        return super.getType().getSimpleName();
    }

    public FieldAccessor<DataWatcher> getMetaDataField() {
        if (dataWatcherField == null) {
            throw new IllegalArgumentException("MetaData field does not exist");
        }
        return dataWatcherField;
    }

    public int getPacketSize(Object packetHandle) {
        return 1;
    }

    protected static PacketType getType(Class<?> packetHandleClass) {
        if (packetHandleClass == null) {
            throw new IllegalArgumentException("Null packets can not be used");
        }
        PacketType type = typesByPacketClass.get(packetHandleClass);
        if (type == null) {
            type = new PacketType(packetHandleClass);
        }
        return type;
    }

    public static PacketType getType(Object packetHandle) {
        if (packetHandle == null) {
            throw new IllegalArgumentException("Null packets can not be used");
        }
        PacketType type = typesByPacketClass.get(packetHandle);
        if (type == null) {
            type = new PacketType(packetHandle.getClass());
        }
        return type;
    }

    public static PacketType getType(int packetId, boolean outGoing) {
        final Class<?> type;
        if (outGoing) {
            type = NMSEnumProtocol.getPacketClassOut(packetId);
        } else {
            type = NMSEnumProtocol.getPacketClassIn(packetId);
        }
        if (type == null) {
            return null;
        } else {
            return getType(type);
        }
    }
}
