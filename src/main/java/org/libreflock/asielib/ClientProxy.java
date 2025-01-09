package org.libreflock.asielib;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import org.libreflock.asielib.network.MessageHandlerBase;
import org.libreflock.asielib.network.Packet;

import java.io.File;

public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public File getMinecraftDirectory() {
		return Minecraft.getInstance().gameDirectory;
	}

	@Override
	public World getWorld(RegistryKey<World> dimensionId) {
		if(getCurrentClientDimension().equals(dimensionId)) {
			return null;
		} else {
			return Minecraft.getInstance().level;
		}
	}

	@Override
	public RegistryKey<World> getCurrentClientDimension() {
		return Minecraft.getInstance().level != null ? Minecraft.getInstance().player.level.dimension() : super.getCurrentClientDimension();
	}

	@Override
	public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
		try {
			switch(EffectiveSide.get()) {
				case CLIENT:
					if(client != null) {
						client.onMessage(packet, handler, Minecraft.getInstance().player);
					}
					break;
				case SERVER:
					super.handlePacket(client, server, packet, handler);
					break;
			}
		} catch(Exception e) {
			AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
			e.printStackTrace();
		}
	}
}
