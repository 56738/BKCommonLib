package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import org.bukkit.entity.Entity;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayOutEntityTeleport</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayOutEntityTeleportHandle extends PacketHandle {
    /** @See {@link PacketPlayOutEntityTeleportClass} */
    public static final PacketPlayOutEntityTeleportClass T = new PacketPlayOutEntityTeleportClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayOutEntityTeleportHandle.class, "net.minecraft.server.PacketPlayOutEntityTeleport");

    /* ============================================================================== */

    public static PacketPlayOutEntityTeleportHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayOutEntityTeleportHandle handle = new PacketPlayOutEntityTeleportHandle();
        handle.instance = handleInstance;
        return handle;
    }

    public static final PacketPlayOutEntityTeleportHandle createNew() {
        return T.constr.newInstance();
    }

    public static final PacketPlayOutEntityTeleportHandle createNew(Entity entity) {
        return T.constr_entity.newInstance(entity);
    }

    /* ============================================================================== */


    public static PacketPlayOutEntityTeleportHandle createNew(int entityId, double posX, double posY, double posZ, float yaw, float pitch, boolean onGround) {
        PacketPlayOutEntityTeleportHandle handle = createNew();
        handle.setEntityId(entityId);
        handle.setPosX(posX);
        handle.setPosY(posY);
        handle.setPosZ(posZ);
        handle.setYaw(yaw);
        handle.setPitch(pitch);
        handle.setOnGround(onGround);
        return handle;
    }

    public double getPosX() {
        return getProtocolPosition(T.posX_1_8_8, T.posX_1_10_2);
    }

    public double getPosY() {
        return getProtocolPosition(T.posY_1_8_8, T.posY_1_10_2);
    }

    public double getPosZ() {
        return getProtocolPosition(T.posZ_1_8_8, T.posZ_1_10_2);
    }

    public void setPosX(double posX) {
        setProtocolPosition(T.posX_1_8_8, T.posX_1_10_2, posX);
    }

    public void setPosY(double posY) {
        setProtocolPosition(T.posY_1_8_8, T.posY_1_10_2, posY);
    }

    public void setPosZ(double posZ) {
        setProtocolPosition(T.posZ_1_8_8, T.posZ_1_10_2, posZ);
    }

    public float getYaw() {
        return getProtocolRotation(T.yaw_raw);
    }

    public float getPitch() {
        return getProtocolRotation(T.pitch_raw);
    }

    public void setYaw(float yaw) {
        setProtocolRotation(T.yaw_raw, yaw);
    }

    public void setPitch(float pitch) {
        setProtocolRotation(T.pitch_raw, pitch);
    }
    public int getEntityId() {
        return T.entityId.getInteger(instance);
    }

    public void setEntityId(int value) {
        T.entityId.setInteger(instance, value);
    }

    public boolean isOnGround() {
        return T.onGround.getBoolean(instance);
    }

    public void setOnGround(boolean value) {
        T.onGround.setBoolean(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayOutEntityTeleport</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutEntityTeleportClass extends Template.Class<PacketPlayOutEntityTeleportHandle> {
        public final Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle> constr = new Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle>();
        public final Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle> constr_entity = new Template.Constructor.Converted<PacketPlayOutEntityTeleportHandle>();

        public final Template.Field.Integer entityId = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posX_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posY_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Integer posZ_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Double posX_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posY_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Double posZ_1_10_2 = new Template.Field.Double();
        @Template.Optional
        public final Template.Field.Byte yaw_raw = new Template.Field.Byte();
        @Template.Optional
        public final Template.Field.Byte pitch_raw = new Template.Field.Byte();
        public final Template.Field.Boolean onGround = new Template.Field.Boolean();

    }

}

