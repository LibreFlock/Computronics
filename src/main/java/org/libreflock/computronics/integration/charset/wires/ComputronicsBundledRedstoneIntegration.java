package org.libreflock.computronics.integration.charset.wires;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.libreflock.charset.api.wires.IBundledEmitter;
import org.libreflock.charset.api.wires.IBundledReceiver;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.internal.IComputronicsPeripheral;
import org.libreflock.asielib.api.tile.IBundledRedstoneProvider;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class ComputronicsBundledRedstoneIntegration {

	private static Capability<IBundledEmitter> CHARSET_EMITTER = null;
	private static Capability<IBundledReceiver> CHARSET_RECEIVER = null;

	@CapabilityInject(IBundledEmitter.class)
	private static void onBundledEmitterrInject(Capability<IBundledEmitter> c) {
		CHARSET_EMITTER = c;
		IntegrationCharsetWires.bundledRedstone.register();
	}

	@CapabilityInject(IBundledReceiver.class)
	private static void onBundledReceiverInject(Capability<IBundledReceiver> c) {
		CHARSET_RECEIVER = c;
		IntegrationCharsetWires.bundledRedstone.register();
	}

	public static boolean isEmitter(ICapabilityProvider tile, Direction side) {
		return tile.hasCapability(CHARSET_EMITTER, side);
	}

	public static boolean isReceiver(ICapabilityProvider tile, Direction side) {
		return tile.hasCapability(CHARSET_RECEIVER, side);
	}

	@Optional.Method(modid = Mods.API.CharsetWires)
	public static IBundledEmitter getEmitter(ICapabilityProvider tile, Direction side) {
		return tile.getCapability(CHARSET_EMITTER, side);
	}

	@Optional.Method(modid = Mods.API.CharsetWires)
	public static IBundledReceiver getReceiver(ICapabilityProvider tile, Direction side) {
		return tile.getCapability(CHARSET_RECEIVER, side);
	}

	private boolean registered = false;

	private void register() {
		if(!registered) {
			MinecraftForge.EVENT_BUS.register(this);
			registered = true;
		}
	}

	private static final ResourceLocation charsetBundledRedstoneID = new ResourceLocation(Mods.Computronics, "charset_bundled_rs");

	@SubscribeEvent
	public void onCapabilityAttach(AttachCapabilitiesEvent<TileEntity> e) {
		if(e.getObject() instanceof IComputronicsPeripheral && e.getObject() instanceof IBundledRedstoneProvider) {
			e.addCapability(charsetBundledRedstoneID, new CharsetCapabilityProvider(e.getObject()));
		}
	}

	public static class TileCache {

		protected final Direction side;
		protected final TileEntity tile;
		protected final IBundledRedstoneProvider br;

		protected TileCache(TileEntity tile, @Nullable Direction side) {
			this.tile = tile;
			this.br = (IBundledRedstoneProvider) tile;
			this.side = side;
		}
	}

	public static class ComputronicsBundledEmitter extends TileCache implements IBundledEmitter {

		protected ComputronicsBundledEmitter(TileEntity tile, @Nullable Direction side) {
			super(tile, side);
		}

		@Override
		public byte[] getBundledSignal() {
			return br.getBundledOutput(side);
		}
	}

	public static class ComputronicsBundledReceiver extends TileCache implements IBundledReceiver {

		protected ComputronicsBundledReceiver(TileEntity tile, Direction side) {
			super(tile, side);
		}

		@Override
		public void onBundledInputChange() {
			TileEntity tile = this.tile.getWorld().getTileEntity(this.tile.getBlockPos().offset(side));
			if(tile != null && isEmitter(tile, side.getOpposite())) {
				br.onBundledInputChange(side, getEmitter(tile, side.getOpposite()).getBundledSignal());
			}
		}
	}

	private static class CharsetCapabilityProvider implements ICapabilityProvider {

		private final TileEntity tile;
		private final IBundledRedstoneProvider br;
		private final ComputronicsBundledEmitter[] EMITTERS = new ComputronicsBundledEmitter[7];
		private final ComputronicsBundledReceiver[] RECEIVERS = new ComputronicsBundledReceiver[7];

		public CharsetCapabilityProvider(TileEntity tile) {
			this.tile = tile;
			this.br = ((IBundledRedstoneProvider) tile);
		}

		@Override
		public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
			return capability == CHARSET_EMITTER && br.canBundledConnectToOutput(facing)
				|| capability == CHARSET_RECEIVER && facing != null && br.canBundledConnectToInput(facing);
		}

		@Nullable
		@Override
		public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
			if(capability == CHARSET_EMITTER && br.canBundledConnectToOutput(facing)) {
				return CHARSET_EMITTER.cast(getEmitter(facing));
			} else if(capability == CHARSET_RECEIVER && facing != null && br.canBundledConnectToInput(facing)) {
				return CHARSET_RECEIVER.cast(getReceiver(facing));
			}
			return null;
		}

		private ComputronicsBundledEmitter getEmitter(@Nullable Direction facing) {
			if(facing == null) {
				if(EMITTERS[6] == null) {
					EMITTERS[6] = new ComputronicsBundledEmitter(tile, null);
				}
				return EMITTERS[6];
			}
			if(EMITTERS[facing.ordinal()] == null) {
				EMITTERS[facing.ordinal()] = new ComputronicsBundledEmitter(tile, facing);
			}
			return EMITTERS[facing.ordinal()];
		}

		private ComputronicsBundledReceiver getReceiver(Direction facing) {
			if(RECEIVERS[facing.ordinal()] == null) {
				RECEIVERS[facing.ordinal()] = new ComputronicsBundledReceiver(tile, facing);
			}
			return RECEIVERS[facing.ordinal()];
		}
	}
}
