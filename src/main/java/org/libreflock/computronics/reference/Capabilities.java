package org.libreflock.computronics.reference;

import net.minecraft.nbt.INBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.audio.AudioUtils;

import java.util.concurrent.Callable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class Capabilities {

	public static final Capabilities INSTANCE = new Capabilities();

	@CapabilityInject(IAudioSource.class)
	public static Capability<IAudioSource> AUDIO_SOURCE_CAPABILITY;
	@CapabilityInject(IAudioReceiver.class)
	public static Capability<IAudioReceiver> AUDIO_RECEIVER_CAPABILITY;

	private static final ResourceLocation AUDIO_SOURCE_KEY = new ResourceLocation("computronics:audio_source");
	private static final ResourceLocation AUDIO_RECEIVER_KEY = new ResourceLocation("computronics:audio_receiver");

	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		CapabilityManager.INSTANCE.register(IAudioSource.class, new NullCapabilityStorage<IAudioSource>(), new AudioSourceFactory());
		CapabilityManager.INSTANCE.register(IAudioReceiver.class, new NullCapabilityStorage<IAudioReceiver>(), new AudioReceiverFactory());
	}

	public static boolean hasAny(@Nullable ICapabilityProvider provider, Direction dir, Capability... caps) {
		if(provider == null) {
			return false;
		}
		for(Capability cap : caps) {
			if(provider.getCapability(cap, dir) != null) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasAll(@Nullable ICapabilityProvider provider, Direction dir, Capability... caps) {
		if(provider == null) {
			return false;
		}
		for(Capability cap : caps) {
			if(provider.getCapability(cap, dir) == null) {
				return false;
			}
		}
		return true;
	}

	@Nullable
	public static <T> T getFirst(@Nullable ICapabilityProvider provider, Direction dir, Iterable<Capability<? extends T>> caps) {
		if(provider == null) {
			return null;
		}
		for(Capability<? extends T> cap : caps) {
			if(provider.getCapability(cap, dir) != null) {
				return (T) provider.getCapability(cap, dir);
			}
		}
		return null;
	}

	@Nullable
	public static <T> T getFirst(@Nullable ICapabilityProvider provider, Direction dir, Capability<? extends T> first, Capability<? extends T> second) {
		if(provider == null) {
			return null;
		}
		if(provider.getCapability(first, dir) != null) {
			return (T) provider.getCapability(first, dir);
		}
		if(provider.getCapability(second, dir) != null) {
			return (T) provider.getCapability(second, dir);
		}
		return null;
	}

	@SubscribeEvent
	public void onAttachCapabilities(AttachCapabilitiesEvent<TileEntity> e) {
		final TileEntity tile = e.getObject();
		if(tile instanceof IAudioSource) {
			e.addCapability(AUDIO_SOURCE_KEY, new ICapabilityProvider() {
				// // @Override
				// public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
				// 	return capability == AUDIO_SOURCE_CAPABILITY;
				// }

				// @Nullable
				// @Override
				// public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
				// 	return hasCapability(capability, facing) ? AUDIO_SOURCE_CAPABILITY.<T>cast((IAudioSource) tile) : null;
				// }

				@Override
				@Nonnull
				public <T>
				LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {
					return cap.orEmpty((Capability<T>) AUDIO_SOURCE_CAPABILITY, LazyOptional.of((NonNullSupplier<T>) tile));
				};

			});
		}
		if(tile instanceof IAudioReceiver) {
			e.addCapability(AUDIO_RECEIVER_KEY, new ICapabilityProvider() {
				@Override
				@Nonnull
				public <T>
				LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, final @Nullable Direction side) {
					return cap.orEmpty((Capability<T>) AUDIO_RECEIVER_CAPABILITY, LazyOptional.of((NonNullSupplier<T>) tile));
				};

			});
		}
	}

	/**
	 * @author asie
	 */
	public static class NullCapabilityStorage<T> implements Capability.IStorage<T> {

		public NullCapabilityStorage() {
		}

		@Nullable
		@Override
		public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
		}
	}


	static class AudioSourceFactory implements Callable {
		public DefaultAudioSource call() {
			return new DefaultAudioSource();
		}
	}

	static class AudioReceiverFactory implements Callable{
		public DefaultAudioReceiver call() {
			return new DefaultAudioReceiver();
		}
	}

	private static class DefaultAudioSource implements IAudioSource {

		// DefaultAudioSource () {}

		@Override
		public int getSourceId() {
			return -1;
		}

		@Override
		public boolean connectsAudio(Direction side) {
			return false;
		}
	}

	private static class DefaultAudioReceiver implements IAudioReceiver {

		@Nullable
		@Override
		public World getSoundWorld() {
			return null;
		}

		@Override
		public Vector3d getSoundPos() {
			return new Vector3d(0, 0, 0);
		}

		@Override
		public int getSoundDistance() {
			return 0;
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable Direction side) {

		}

		@Override
		public String getID() {
			return AudioUtils.positionId(0, 0, 0);
		}

		@Override
		public boolean connectsAudio(Direction side) {
			return false;
		}
	}

	private Capabilities() {
	}
}
