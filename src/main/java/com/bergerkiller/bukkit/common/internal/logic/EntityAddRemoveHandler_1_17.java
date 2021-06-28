package com.bergerkiller.bukkit.common.internal.logic;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.logging.Level;
import java.util.stream.Stream;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.ExtendedClassWriter;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_17 extends EntityAddRemoveHandler {
    private final Class<?> levelCallbackType;
    private final FastField<Object> entityManagerField = new FastField<Object>();
    private final FastField<Object> callbacksField = new FastField<Object>();
    private final List<LevelCallbackHandler> hooks = new ArrayList<LevelCallbackHandler>();
    private final AddRemoveHandlerLogic removeHandler;
    private final Class<?> levelCallbackHookType;

    public EntityAddRemoveHandler_1_17() {
        // This is the interface we implement to receive events
        levelCallbackType = Resolver.loadClass("net.minecraft.world.level.entity.LevelCallback", false);
        if (levelCallbackType == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find LevelCallback class");
        }

        // Initialize accessor of PersistentEntitySectionManager WorldServer.entityManager
        Class<?> sectionManagerClass = Resolver.loadClass("net.minecraft.world.level.entity.PersistentEntitySectionManager", false);
        if (sectionManagerClass == null) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to find PersistentEntitySectionManager class");
        }
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entityManager");
            entityManagerField.init(WorldServerHandle.T.getType().getDeclaredField(fieldName));
            if (!sectionManagerClass.isAssignableFrom(entityManagerField.getType())) {
                throw new IllegalStateException("Field not assignable to PersistentEntitySectionManager");
            }
            entityManagerField.forceInitialization();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize WorldServer entityManager field: " + t.getMessage(), t);
            entityManagerField.initUnavailable("entityManager");
        }

        // Initialize the callbacks field of PersistentEntitySectionManager
        try {
            String fieldName = Resolver.resolveFieldName(sectionManagerClass, "callbacks");
            callbacksField.init(sectionManagerClass.getDeclaredField(fieldName));
            if (!levelCallbackType.equals(callbacksField.getType())) {
                throw new IllegalStateException("Field not assignable to LevelCallback");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize PersistentEntitySectionManager callbacks field: " + t.getMessage(), t);
            callbacksField.initUnavailable("callbacks");
        }

        this.removeHandler = Template.Class.create(AddRemoveHandlerLogic.class, Common.TEMPLATE_RESOLVER);

        Class<?> hookType = null;
        try {
            Class<?> levelCallbackType = callbacksField.getType();
            String levelCallbackDesc = MPLType.getDescriptor(levelCallbackType);
            String bkcCallbackDesc = MPLType.getDescriptor(LevelCallbackHandler.class);

            ExtendedClassWriter<Object> cw = ExtendedClassWriter.builder(levelCallbackType)
                    .setFlags(ClassWriter.COMPUTE_MAXS)
                    .setExactName(EntityAddRemoveHandler_1_17.class.getName() + "$Hook")
                    .build();

            FieldVisitor fv;
            MethodVisitor mv;

            // Add field with the base instance
            {
                fv = cw.visitField(ACC_PUBLIC | ACC_FINAL, "base", levelCallbackDesc, null, null);
                fv.visitEnd();
            }

            // Add field with the callback instance
            {
                fv = cw.visitField(ACC_PUBLIC | ACC_FINAL, "callback", bkcCallbackDesc, null, null);
                fv.visitEnd();
            }

            // Add constructor initializing the base instance and the callback handler instance
            {
                mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(" + levelCallbackDesc + bkcCallbackDesc + ")V", null, null);
                mv.visitCode();
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitFieldInsn(PUTFIELD, cw.getInternalName(), "callback", bkcCallbackDesc);
                mv.visitInsn(RETURN);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // Override all methods defined by the interface that must be implemented
            for (Method method : levelCallbackType.getMethods()) {
                String name = MPLType.getName(method);
                Class<?>[] params = method.getParameterTypes();
                final String callbackMethodName;
                if (name.equals("b") && params.length == 1 && params[0] == Object.class) {
                    callbackMethodName = "onEntityAdded";
                } else if (name.equals("a") && params.length == 1 && params[0] == Object.class) {
                    callbackMethodName = "onEntityRemoved";
                } else {
                    callbackMethodName = null;
                }

                mv = cw.visitMethod(ACC_PUBLIC, MPLType.getName(method), MPLType.getMethodDescriptor(method), null, null);
                mv.visitCode();
                
                if (callbackMethodName != null) {
                    // Locate the Method instance in the callback class we're going to be calling
                    // This shouldn't ever fail.
                    Method callbackMethod = Stream.of(LevelCallbackHandler.class.getMethods())
                            .filter(m -> m.getName().equals(callbackMethodName))
                            .findFirst().get();

                    // Call the base method, store any return value in a temporary value on the stack
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                    int reg = MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                    if (method.getReturnType() != void.class) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), ISTORE), reg);
                    }

                    // Then call our callback hook with the input arguments, discard return value
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "callback", bkcCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, LevelCallbackHandler.class, callbackMethod);

                    // Finally, return the original return value of the base method (if any)
                    if (method.getReturnType() != void.class) {
                        mv.visitVarInsn(MPLType.getOpcode(method.getReturnType(), ILOAD), reg);
                    }
                } else {
                    // Call base method directly
                    mv.visitVarInsn(ALOAD, 0);
                    mv.visitFieldInsn(GETFIELD, cw.getInternalName(), "base", levelCallbackDesc);
                    MPLType.visitVarILoad(mv, 1, method.getParameterTypes());
                    ExtendedClassWriter.visitInvoke(mv, levelCallbackType, method);
                }
                mv.visitInsn(MPLType.getOpcode(method.getReturnType(), IRETURN));
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }

            // Generate!
            hookType = cw.generate();
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to initialize level hook callback proxy class", t);
        }
        this.levelCallbackHookType = hookType;
    }

    @Override
    public void processEvents() {
        for (LevelCallbackHandler hook : hooks) {
            hook.processEvents();
        }
    }

    @Override
    protected void hook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() != this.levelCallbackHookType) {
            try {
                LevelCallbackHandler handler = new LevelCallbackHandler(this, world);
                Object hook = this.levelCallbackHookType.getConstructors()[0].newInstance(callbacks, handler);
                callbacksField.set(sectionManager, hook);
                hooks.add(handler);
            } catch (Throwable t) {
                Logging.LOGGER_REFLECTION.log(Level.SEVERE, "Failed to instantiate a level hook callback", t);
            }
        }
    }

    @Override
    protected void unhook(World world) {
        Object sectionManager = entityManagerField.get(HandleConversion.toWorldHandle(world));
        Object callbacks = callbacksField.get(sectionManager);
        if (callbacks != null && callbacks.getClass() == this.levelCallbackHookType) {
            // De-register the handler
            LevelCallbackHandler handler = SafeField.get(callbacks, "callback", LevelCallbackHandler.class);
            if (handler != null) {
                hooks.remove(handler);
            }

            // Retrieve the value of the 'base' field and restore the field value to that
            Object base = SafeField.get(callbacks, "base", callbacksField.getType());
            if (base != null) {
                callbacksField.set(sectionManager, base);
            }
        }
    }

    /**
     * WorldServer.entityManager -> PersistentEntitySectionManager.callbacks is hooked by a
     * runtime-generated hook that calls callbacks on this class type instance
     * to be notified of entities being added or removed
     */
    public static class LevelCallbackHandler {
        private final EntityAddRemoveHandler_1_17 handler;
        private final World world;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public LevelCallbackHandler(EntityAddRemoveHandler_1_17 handler, World world) {
            this.handler = handler;
            this.world = world;
        }

        public void onEntityRemoved(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            pendingAddEvents.remove(entity);
            handler.notifyRemoved(world, entity);
        }

        public void onEntityAdded(Object entityHandle) {
            Entity entity = WrapperConversion.toEntity(entityHandle);
            pendingAddEvents.add(entity);
            handler.notifyAddedEarly(world, entity);
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
            }
        }
    }

    @Override
    public void replace(World world, EntityHandle oldEntity, EntityHandle newEntity) {
        // *** EntityTrackerEntry ***
        replaceInEntityTracker(oldEntity, oldEntity, newEntity);
        if (oldEntity.getVehicle() != null) {
            replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
        }
        if (oldEntity.getPassengers() != null) {
            for (EntityHandle passenger : oldEntity.getPassengers()) {
                replaceInEntityTracker(passenger, oldEntity, newEntity);
            }
        }

        removeHandler.replaceInWorldStorage(HandleConversion.toWorldHandle(world), oldEntity.getRaw(), newEntity.getRaw());
        removeHandler.replaceInSectionStorage(oldEntity.getRaw(), newEntity.getRaw());
        removeHandler.replaceInChunkStorage(oldEntity.getRaw(), newEntity.getRaw());

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        // com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldEntity.getRaw());
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        // There appears to be nothing to do, since all of this is now done
        // inside setPositionRaw - so right away.
    }

    @SuppressWarnings("unchecked")
    private static void replaceInEntityTracker(EntityHandle entity, EntityHandle oldEntity, EntityHandle newEntity) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newEntity.getBukkitWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entity.getIdField());
        if (entry != null) {
            // PlayerChunkMap$EntityTracker entity
            EntityHandle entryEntity = entry.getEntity();
            if (entryEntity != null && entryEntity.getIdField() == oldEntity.getIdField()) {
                entry.setEntity(newEntity);
            }

            // EntityTrackerEntry 'tracker' entity
            EntityTrackerEntryStateHandle stateHandle = entry.getState();
            EntityHandle stateEntity = stateHandle.getEntity();
            if (stateEntity != null && stateEntity.getIdField() == oldEntity.getIdField() && stateEntity.getRaw() != newEntity.getRaw()) {
                stateHandle.setEntity(newEntity);
            }

            // EntityTrackerEntry List of passengers
            List<Object> statePassengers = (List<Object>) EntityTrackerEntryStateHandle.T.opt_passengers.raw.get(stateHandle.getRaw());
            replaceInList(statePassengers, oldEntity, newEntity);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, EntityHandle oldEntity, EntityHandle newEntity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle) obj;
                if (obj_e.getIdField() == oldEntity.getIdField()) {
                    iter.set(newEntity);
                }
            } else if (EntityHandle.T.isAssignableFrom(obj)) {
                int obj_id = EntityHandle.T.idField.getInteger(obj);
                if (obj_id == oldEntity.getIdField()) {
                    iter.set(newEntity.getRaw());
                }
            }
        }
        return false;
    }

    @Template.Optional
    @Template.Import("net.minecraft.server.level.WorldServer")
    @Template.Import("net.minecraft.world.level.chunk.Chunk")
    @Template.Import("net.minecraft.server.level.ChunkProviderServer")
    @Template.Import("net.minecraft.world.entity.Entity")
    @Template.Import("net.minecraft.util.EntitySlice")
    @Template.InstanceType("net.minecraft.world.level.entity.PersistentEntitySectionManager")
    public static abstract class AddRemoveHandlerLogic extends Template.Class<Template.Handle> {

        /*
         * <REPLACE_IN_WORLD_STORAGE>
         * public static void replaceInWorldStorage(WorldServer world, Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private int entityId:id;
         *     #require net.minecraft.world.entity.Entity protected UUID entityUUID:uuid;
         *     int entityId = oldEntity#entityId;
         *     UUID entityUUID = oldEntity#entityUUID;
         * 
         *     #require net.minecraft.server.level.WorldServer private final net.minecraft.world.level.entity.PersistentEntitySectionManager entityManager;
         *     #require net.minecraft.world.level.entity.PersistentEntitySectionManager private final EntityLookup visibleEntityStorage;
         *     PersistentEntitySectionManager entitySectionManager = world#entityManager;
         *     EntityLookup entityLookup = entitySectionManager#visibleEntityStorage;
         * 
         *     #require net.minecraft.world.level.entity.EntityLookup private final it.unimi.dsi.fastutil.ints.Int2ObjectMap byId;
         *     #require net.minecraft.world.level.entity.EntityLookup private final Map byUUID:byUuid;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap byIdMap = entityLookup#byId;
         *     Map byUUIDMap = entityLookup#byUUID;
         * 
         *     if (byIdMap.get(entityId) == oldEntity) {
         *         byIdMap.put(entityId, newEntity);
         *     }
         *     if (byUUIDMap.get(entityUUID) == oldEntity) {
         *         byUUIDMap.put(entityUUID, newEntity);
         *     }
         * 
         *     #require net.minecraft.server.level.WorldServer final net.minecraft.world.level.entity.EntityTickList entityTickList;
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> active;
         *     #require net.minecraft.world.level.entity.EntityTickList private it.unimi.dsi.fastutil.ints.Int2ObjectMap<Entity> passive;
         *     EntityTickList tickList = world#entityTickList;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListActive = tickList#active;
         *     it.unimi.dsi.fastutil.ints.Int2ObjectMap tickListPassive = tickList#passive;
         *     if (tickListActive.get(entityId) == oldEntity) {
         *         tickListActive.put(entityId, newEntity);
         *     }
         *     if (tickListPassive.get(entityId) == oldEntity) {
         *         tickListPassive.put(entityId, newEntity);
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_WORLD_STORAGE%")
        public abstract void replaceInWorldStorage(Object worldHandle, Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_CHUNK_STORAGE>
         * public static void replaceInChunkStorage(Entity oldEntity, Entity newEntity) {
         *     // Paper: added an entities field to Chunk
         * #if exists net.minecraft.world.level.chunk.Chunk public final com.destroystokyo.paper.util.maplist.EntityList entities;
         *     net.minecraft.core.BlockPosition pos = oldEntity.getChunkCoordinates();
         *     net.minecraft.world.level.World world = oldEntity.getWorld();
         *     if (world == null || pos == null) {
         *         return;
         *     }
         *     Chunk chunk = ((WorldServer)world).getChunkIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);
         *     if (chunk == null) {
         *         return;
         *     }
         *     if (chunk.entities.remove(oldEntity)) {
         *         if (newEntity != null) {
         *             chunk.entities.add(newEntity);
         *         }
         *     }
         * #endif
         * }
         */
        @Template.Generated("%REPLACE_IN_CHUNK_STORAGE%")
        public abstract void replaceInChunkStorage(Object oldEntity, Object newEntity);

        /*
         * <REPLACE_IN_SECTION_STORAGE>
         * public static void replaceInSectionStorage(Entity oldEntity, Entity newEntity) {
         *     #require net.minecraft.world.entity.Entity private net.minecraft.world.level.entity.EntityInLevelCallback levelCallback;
         *     EntityInLevelCallback callback = oldEntity#levelCallback;
         *     if (callback != EntityInLevelCallback.NULL) {
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private final EntityAccess entity;
         *         #require net.minecraft.world.level.entity.PersistentEntitySectionManager.a private EntitySection currentSection;
         *         if ( callback#entity == oldEntity ) {
         *             callback#entity = newEntity;
         *         }
         *         EntitySection section = callback#currentSection;
         * 
         *         #require net.minecraft.world.level.entity.EntitySection private final net.minecraft.util.EntitySlice storage;
         *         EntitySlice slice = section#storage;
         *         List sliceList = com.bergerkiller.bukkit.common.conversion.type.HandleConversion.cbEntitySliceToList(slice);
         *         java.util.ListIterator iter = sliceList.listIterator();
         *         while (iter.hasNext()) {
         *             if (iter.next() == oldEntity) {
         *                 iter.set(newEntity);
         *             }
         *         }
         *     }
         * }
         */
        @Template.Generated("%REPLACE_IN_SECTION_STORAGE%")
        public abstract void replaceInSectionStorage(Object oldEntity, Object newEntity);
    }
}
