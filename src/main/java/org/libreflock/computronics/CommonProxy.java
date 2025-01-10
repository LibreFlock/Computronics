package org.libreflock.computronics;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.item.entity.EntityItemIndestructable;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.reference.Sounds;
import org.libreflock.asielib.network.Packet;

import java.io.IOException;

public class CommonProxy {

	public boolean isClient() {
		return false;
	}

	public void registerAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(AudioPacketDFPWM.class);
		AudioPacketRegistry.INSTANCE.registerType(SoundCardPacket.class);
	}

	public void registerEntities() {
		EntityRegistry.registerModEntity(new ResourceLocation(Mods.Computronics, "tape_item"), EntityItemIndestructable.class, "tape_item", 1, Computronics.instance, 64, 20, true);
	}

	public void registerItemModel(Item item, int meta, String name) {

	}

	public void registerItemModel(Block block, int meta, String name) {
		registerItemModel(Item.getItemFromBlock(block), meta, name);
	}

	public void preInit() {
		registerAudioHandlers();
	}

	public void init() {
		Sounds.registerSounds();
	}

	public void goBoom(Packet p) throws IOException {
		//NO-OP
	}

	public void spawnSwarmParticle(World world, double xPos, double yPos, double zPos, int color) {
		//NO-OP
	}

	public void onServerStop() {

	}
}
