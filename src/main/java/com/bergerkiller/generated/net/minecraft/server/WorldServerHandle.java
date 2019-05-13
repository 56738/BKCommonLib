package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.WorldServer</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public abstract class WorldServerHandle extends WorldHandle {
    /** @See {@link WorldServerClass} */
    public static final WorldServerClass T = new WorldServerClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(WorldServerHandle.class, "net.minecraft.server.WorldServer", com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);

    /* ============================================================================== */

    public static WorldServerHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract List<TileEntityHandle> getTileEntityList();
    public abstract List<EntityPlayerHandle> getPlayers();
    public abstract EntityTracker getEntityTracker();
    public abstract PlayerChunkMapHandle getPlayerChunkMap();
    public abstract void setEntityTracker(EntityTracker entityTracker);
    public abstract ChunkProviderServerHandle getChunkProviderServer();
    public abstract WorldNBTStorageHandle getDataManager();
    public abstract List<EntityHandle> getEntityList();
    public abstract void removeEntity(EntityHandle entity);
    public abstract boolean addEntity(EntityHandle entity);
    public abstract MinecraftServerHandle getMinecraftServer();
    public abstract void saveLevel();

    public static WorldServerHandle fromBukkit(org.bukkit.World world) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.Conversion.toWorldHandle.convert(world));
    }

    public void setChunkProviderServer(ChunkProviderServerHandle chunkProviderServerHandle) {
        if (T.field_chunkProviderServer.isAvailable()) {
            T.field_chunkProviderServer.set(getRaw(), chunkProviderServerHandle);
        }
        if (WorldHandle.T.field_chunkProvider.isAvailable()) {
            WorldHandle.T.field_chunkProvider.set(getRaw(), chunkProviderServerHandle.getRaw());
        }
    }
    public abstract IntHashMap getEntitiesById();
    public abstract void setEntitiesById(IntHashMap value);
    public abstract Map<UUID, EntityHandle> getEntitiesByUUID();
    public abstract void setEntitiesByUUID(Map<UUID, EntityHandle> value);
    public abstract Collection<EntityHandle> getEntityRemoveQueue();
    public abstract void setEntityRemoveQueue(Collection<EntityHandle> value);
    /**
     * Stores class members for <b>net.minecraft.server.WorldServer</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class WorldServerClass extends Template.Class<WorldServerHandle> {
        public final Template.Field.Converted<IntHashMap> entitiesById = new Template.Field.Converted<IntHashMap>();
        public final Template.Field.Converted<Map<UUID, EntityHandle>> entitiesByUUID = new Template.Field.Converted<Map<UUID, EntityHandle>>();
        @Template.Optional
        public final Template.Field.Converted<ChunkProviderServerHandle> field_chunkProviderServer = new Template.Field.Converted<ChunkProviderServerHandle>();
        public final Template.Field.Converted<Collection<EntityHandle>> entityRemoveQueue = new Template.Field.Converted<Collection<EntityHandle>>();

        public final Template.Method.Converted<List<TileEntityHandle>> getTileEntityList = new Template.Method.Converted<List<TileEntityHandle>>();
        public final Template.Method.Converted<List<EntityPlayerHandle>> getPlayers = new Template.Method.Converted<List<EntityPlayerHandle>>();
        public final Template.Method.Converted<EntityTracker> getEntityTracker = new Template.Method.Converted<EntityTracker>();
        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        public final Template.Method.Converted<Void> setEntityTracker = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ChunkProviderServerHandle> getChunkProviderServer = new Template.Method.Converted<ChunkProviderServerHandle>();
        public final Template.Method.Converted<WorldNBTStorageHandle> getDataManager = new Template.Method.Converted<WorldNBTStorageHandle>();
        public final Template.Method.Converted<List<EntityHandle>> getEntityList = new Template.Method.Converted<List<EntityHandle>>();
        public final Template.Method.Converted<Void> removeEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<Boolean> addEntity = new Template.Method.Converted<Boolean>();
        public final Template.Method.Converted<MinecraftServerHandle> getMinecraftServer = new Template.Method.Converted<MinecraftServerHandle>();
        public final Template.Method<Void> saveLevel = new Template.Method<Void>();

    }

}

