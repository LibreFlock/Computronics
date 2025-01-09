package org.libreflock.asielib;

import net.minecraft.network.INetHandler;
import net.minecraft.network.play.ServerPlayNetHandler;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import org.libreflock.asielib.network.MessageHandlerBase;
import org.libreflock.asielib.network.Packet;

import javax.annotation.Nullable;
import java.io.File;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public File getMinecraftDirectory() {
		return new File(".");
	}

	@Nullable
	public ServerWorld getWorld(RegistryKey<World> dimensionId) {
		return ServerLifecycleHooks.getCurrentServer().getLevel(dimensionId);
	}

	public int getCurrentClientDimension() {
		return -9001;
	}

	public void handlePacket(MessageHandlerBase client, MessageHandlerBase server, Packet packet, INetHandler handler) {
		try {
			if(server != null) {
				server.onMessage(packet, handler, ((ServerPlayNetHandler) handler).player);
			}
		} catch(Exception e) {
			AsieLibMod.log.warn("Caught a network exception! Is someone sending malformed packets?");
			e.printStackTrace();
		}
	}
}
