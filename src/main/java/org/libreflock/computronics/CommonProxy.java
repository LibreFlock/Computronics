package org.libreflock.computronics;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.item.entity.ItemEntityIndestructable;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.reference.Sounds;
import org.libreflock.asielib.network.Packet;

import java.io.IOException;

import javax.swing.text.html.parser.Entity;

public class CommonProxy {

	// public static final DeferredRegister<Item> ItemRegistry = DeferredRegister.create(ForgeRegistries.ITEMS, "computronics");
	public static final DeferredRegister EntityRegistry = DeferredRegister.create(ForgeRegistries.ENTITIES, "computronics");
	// public static final DeferredRegister<SoundEvent> SoundRegistry = DeferredRegister

	public boolean isClient() {
		return false;
	}

	public void registerAudioHandlers() {
		AudioPacketRegistry.INSTANCE.registerType(AudioPacketDFPWM.class);
		AudioPacketRegistry.INSTANCE.registerType(SoundCardPacket.class);
	}

	public void registerEntities() {
		// EntityRegistry.reig(new ResourceLocation(Mods.Computronics, "tape_item"), ItemEntityIndestructable.class, "tape_item", 1, Computronics.instance, 64, 20, true);
		ItemRegistry.register("tape_item", null);
	}

	public void registerItemModel(Item item, int meta, String name) {

	}

	public void registerItemModel(Block block, int meta, String name) {
		// registerItemModel(Item.getItemFromBlock(block), meta, name);
	}

	public void preInit() {
		registerAudioHandlers();
	}

	public void init() {
		Sounds.registerSounds();
	}

	public void goBoom(Packet p) throws IOException { // ????
		//NO-OP
	}

	public void spawnSwarmParticle(World world, double xPos, double yPos, double zPos, int color) {
		//NO-OP
	}

	public void onServerStop() {

	}
}
