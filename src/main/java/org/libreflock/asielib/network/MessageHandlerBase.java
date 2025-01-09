package org.libreflock.asielib.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;

import java.io.IOException;

public abstract class MessageHandlerBase {

	public abstract void onMessage(Packet packet, INetHandler handler, PlayerEntity player, int command) throws IOException;

	public Packet onMessage(Packet packet, INetHandler handler, PlayerEntity player) {
		try {
			onMessage(packet, handler, player, packet.readUnsignedShort());
		} catch(IOException e) {
			e.printStackTrace();
		}
		return packet;
	}

}
