package org.libreflock.computronics;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
// import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
// import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
// import net.minecraft.world.IBlockAccess;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
// import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.audio.AudioPacketClientHandlerDFPWM;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.audio.SoundCardPacketClientHandler;
import org.libreflock.computronics.item.ItemPortableTapeDrive;
import org.libreflock.computronics.oc.IntegrationOpenComputers;
import org.libreflock.computronics.oc.client.RackMountableRenderer;
import org.libreflock.computronics.oc.client.UpgradeRenderer;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tape.TapeScrollEventHandler;
import org.libreflock.computronics.util.boom.SelfDestruct;
import org.libreflock.computronics.util.internal.IBlockWithColor;
import org.libreflock.computronics.util.internal.IItemWithColor;
import org.libreflock.computronics.util.sound.Audio;
import org.libreflock.asielib.network.Packet;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {

	@Override
	public boolean isClient() {
		return true;
	}

	@Override
	public void registerAudioHandlers() {
		super.registerAudioHandlers();
		AudioPacketRegistry.INSTANCE.registerClientHandler(
			AudioPacketDFPWM.class, new AudioPacketClientHandlerDFPWM()
		);
		AudioPacketRegistry.INSTANCE.registerClientHandler(
			SoundCardPacket.class, new SoundCardPacketClientHandler()
		);
	}

	@Override
	public void registerEntities() {
		super.registerEntities();
	}

	@Override
	public void registerItemModel(Item item, int meta, String name) {
		if(item instanceof IItemWithColor) {
			coloredItems.add(item);
		}
		if(name.contains("#")) {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name.split("#")[0], name.split("#")[1]));
		} else {
			ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(name, "inventory"));
		}
	}

	@Override
	public void registerItemModel(Block block, int meta, String name) {
		if(block instanceof IBlockWithColor) {
			coloredBlocks.add(block);
		}
		super.registerItemModel(block, meta, name);
	}

	private final List<Item> coloredItems = new ArrayList<Item>();
	private final List<Block> coloredBlocks = new ArrayList<Block>();

	@Override
	public void preInit() {
		super.preInit();

		registerRenderers();
	}

	@Override
	public void init() {
		super.init();
		Audio.init();
		registerColors();
		MinecraftForge.EVENT_BUS.register(new TapeScrollEventHandler());
	}

	private void registerColors() {
		Minecraft.getInstance().getItemColors().register(new IItemColor() {
			@Override
			public int getColor(ItemStack stack, int tintIndex) {
				return stack.getItem() instanceof IItemWithColor ? ((IItemWithColor) stack.getItem()).getColorFromItemstack(stack, tintIndex) : 0xFFFFFFFF;
			}
		}, coloredItems.toArray(new Item[coloredItems.size()]));
		Minecraft.getInstance().getBlockColors().register(new IBlockColor() {
			@Override
			public int getColor(BlockState state, @Nullable IBlockDisplayReader worldIn, @Nullable BlockPos pos, int tintIndex) {
				return pos != null && state.getBlock() instanceof IBlockWithColor ? ((IBlockWithColor) state.getBlock()).colorMultiplier(state, worldIn, pos, tintIndex) : 0xFFFFFFFF;
			}
		}, coloredBlocks.toArray(new Block[coloredBlocks.size()]));
	}

	public void registerRenderers() {
		/*if(Computronics.colorfulLamp != null) {
			//RenderingRegistry.registerBlockHandler(new LampRender());
		}*/
		/*if(Computronics.audioCable != null) {
			RenderingRegistry.registerBlockHandler(new AudioCableRender());
		}*/
		if(Mods.isLoaded(Mods.OpenComputers)) {
			registerOpenComputersRenderers();
		}
		/*if(Mods.API.hasAPI(Mods.API.BuildCraftStatements)) {
			MinecraftForge.EVENT_BUS.register(new StatementTextureManager());
		}*/
		// if(Computronics.railcraft != null) {
		// 	Computronics.railcraft.registerRenderers();
		// }

		ItemPortableTapeDrive.MeshDefinition.registerRenderers();
	}

	@Override
	public void onServerStop() {
		Computronics.instance.audio.removeAll();
		Computronics.instance.soundCardAudio.removeAll();
	}

	@Override
	public void goBoom(Packet p) throws IOException {
		double
			x = p.readDouble(),
			y = p.readDouble(),
			z = p.readDouble();
		float force = p.readFloat();
		boolean destroyBlocks = p.readByte() != 0;
		Minecraft minecraft = Minecraft.getInstance();
		SelfDestruct explosion = new SelfDestruct(minecraft.level,
			null, x, y, z, force, destroyBlocks);
		int size = p.readInt();
		ArrayList<BlockPos> list = new ArrayList<BlockPos>(size);
		int i = (int) x;
		int j = (int) y;
		int k = (int) z;
		{
			int j1, k1, l1;
			for(int i1 = 0; i1 < size; ++i1) {
				j1 = p.readByte() + i;
				k1 = p.readByte() + j;
				l1 = p.readByte() + k;
				list.add(new BlockPos(j1, k1, l1));
			}
		}

		explosion.getToBlow().clear();
		explosion.getToBlow().addAll(list);
		explosion.finalizeExplosion(true);
		
		Vector3d vec = new Vector3d( (double) p.readFloat(), (double) p.readFloat(), (double) p.readFloat());
		minecraft.player.setDeltaMovement(vec);
		// minecraft.player.motionX += (double) p.readFloat();
		// minecraft.player.motionY += (double) p.readFloat();
		// minecraft.player.motionZ += (double) p.readFloat();
	}

	// @Override
	// @Optional.Method(modid = Mods.Forestry)
	// public void spawnSwarmParticle(World world, double xPos, double yPos, double zPos, int color) {
	// 	Computronics.forestry.spawnSwarmParticle(world, xPos, yPos, zPos, color);
	// }

	// @Optional.Method(modid = Mods.OpenComputers)
	private void registerOpenComputersRenderers() {
		// if(Computronics.forestry != null) {
		// 	Computronics.forestry.registerOCEntityRenderers();
		// }
		if(IntegrationOpenComputers.upgradeRenderer == null) {
			IntegrationOpenComputers.upgradeRenderer = new UpgradeRenderer();
		}
		MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.upgradeRenderer);

		if(IntegrationOpenComputers.mountableRenderer == null) {
			IntegrationOpenComputers.mountableRenderer = new RackMountableRenderer();
		}
		MinecraftForge.EVENT_BUS.register(IntegrationOpenComputers.mountableRenderer);
	}
}
