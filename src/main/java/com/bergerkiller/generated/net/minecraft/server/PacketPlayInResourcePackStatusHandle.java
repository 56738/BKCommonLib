package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.PacketPlayInResourcePackStatus</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class PacketPlayInResourcePackStatusHandle extends PacketHandle {
    /** @See {@link PacketPlayInResourcePackStatusClass} */
    public static final PacketPlayInResourcePackStatusClass T = new PacketPlayInResourcePackStatusClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(PacketPlayInResourcePackStatusHandle.class, "net.minecraft.server.PacketPlayInResourcePackStatus");

    /* ============================================================================== */

    public static PacketPlayInResourcePackStatusHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        PacketPlayInResourcePackStatusHandle handle = new PacketPlayInResourcePackStatusHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public Object getStatus() {
        return T.status.get(instance);
    }

    public void setStatus(Object value) {
        T.status.set(instance, value);
    }

    /**
     * Stores class members for <b>net.minecraft.server.PacketPlayInResourcePackStatus</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayInResourcePackStatusClass extends Template.Class<PacketPlayInResourcePackStatusHandle> {
        @Template.Optional
        public final Template.Field<String> message = new Template.Field<String>();
        public final Template.Field.Converted<Object> status = new Template.Field.Converted<Object>();

    }

}

