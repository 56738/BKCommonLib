package com.bergerkiller.bukkit.common.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.bergerkiller.bukkit.common.reflection.classes.PacketRef;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

/**
 * Wraps around another packet to create an undetectable packet type to send to clients undetected
 */
public class SilentPacket extends Packet {
	static {
		PacketRef.classToIds.put(SilentPacket.class, 0);
	}

	private final Packet packet;

	public SilentPacket(Packet packet) {
		this.packet = packet;
		PacketRef.packetID.transfer(packet, this);
	}

	@Override
	public int a() {
		return this.packet.a();
	}

	@Override
	public void a(DataInputStream in) throws IOException {
		throw new UnsupportedOperationException("Can not load a silent packet from a stream");
	}

	@Override
	public void a(DataOutputStream out) throws IOException {
		this.packet.a(out);
	}

	@Override
	public void handle(NetHandler arg0) {
		// Nothing happens here to avoid problems
	}
}
