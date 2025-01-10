package org.libreflock.computronics.integration.charset.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.libreflock.charset.api.audio.AudioAPI;
import org.libreflock.charset.api.audio.AudioData;
import org.libreflock.charset.api.audio.AudioSink;
import org.libreflock.charset.api.audio.IAudioReceiver;
import org.libreflock.charset.api.audio.IAudioSource;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.tile.TileAudioCable;
import org.libreflock.computronics.tile.TileSpeaker;
import org.libreflock.computronics.tile.TileSpeechBox;
import org.libreflock.computronics.tile.TileTapeDrive;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class IntegrationCharsetAudio {

	@CapabilityInject(IAudioSource.class)
	public static Capability<IAudioSource> SOURCE_CAPABILITY;
	@CapabilityInject(IAudioReceiver.class)
	public static Capability<IAudioReceiver> RECEIVER_CAPABILITY;

	private static final ResourceLocation CABLE_SINK_KEY = new ResourceLocation("computronics:cableSink");
	private static final ResourceLocation SPEAKER_SINK_KEY = new ResourceLocation("computronics:speakerSink");
	private static final ResourceLocation TAPE_SOURCE_KEY = new ResourceLocation("computronics:tapeDriveSource");
	private static final ResourceLocation SPEECH_BOX_SOURCE_KEY = new ResourceLocation("computronics:speechBoxSource");

	public void postInit() {
		AudioAPI.SINK_REGISTRY.register(AudioSinkSpeaker.class, AudioSinkSpeaker::new);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onAttach(final AttachCapabilitiesEvent<TileEntity> event) {
		if(event.getObject() instanceof TileSpeaker
			&& RECEIVER_CAPABILITY != null) {
			event.addCapability(SPEAKER_SINK_KEY, new ICapabilityProvider() {
				private final AudioSink sink = new AudioSinkSpeaker((TileSpeaker) event.getObject());

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
					return capability == RECEIVER_CAPABILITY && facing != null;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
					return capability == RECEIVER_CAPABILITY ? RECEIVER_CAPABILITY.<T>cast(sink) : null;
				}
			});
		} else if(event.getObject() instanceof TileAudioCable
			&& RECEIVER_CAPABILITY != null) {
			event.addCapability(CABLE_SINK_KEY, new ICapabilityProvider() {
				private final TileAudioCable cable = (TileAudioCable) event.getObject();
				private final AudioReceiverCable[] RECEIVERS = new AudioReceiverCable[6];

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
					return capability == RECEIVER_CAPABILITY && facing != null;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
					if(capability == RECEIVER_CAPABILITY && facing != null) {
						if(RECEIVERS[facing.ordinal()] == null) {
							RECEIVERS[facing.ordinal()] = new AudioReceiverCable(cable, facing);
						}

						return RECEIVER_CAPABILITY.cast(RECEIVERS[facing.ordinal()]);
					} else {
						return null;
					}
				}
			});
		} else if(event.getObject() instanceof TileTapeDrive
			&& SOURCE_CAPABILITY != null) {
			event.addCapability(TAPE_SOURCE_KEY, new ICapabilityProvider() {
				private final AudioSourceDummy source = new AudioSourceDummy();

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
					return capability == SOURCE_CAPABILITY && facing != null;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
					return capability == SOURCE_CAPABILITY ? SOURCE_CAPABILITY.<T>cast(source) : null;
				}
			});

		} else if(event.getObject() instanceof TileSpeechBox
			&& SOURCE_CAPABILITY != null) {
			event.addCapability(SPEECH_BOX_SOURCE_KEY, new ICapabilityProvider() {
				private final AudioSourceDummy source = new AudioSourceDummy();

				@Override
				public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
					return capability == SOURCE_CAPABILITY && facing != null;
				}

				@Nullable
				@Override
				public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
					return capability == SOURCE_CAPABILITY ? SOURCE_CAPABILITY.<T>cast(source) : null;
				}
			});

		}
	}

	public static int send(IBlockAccess world, BlockPos pos, AudioPacket packet, float volume, boolean ignoreComputronicsAPICheck) {
		AudioData dataNew;
		org.libreflock.charset.api.audio.AudioPacket packetNew;

		if(packet instanceof AudioPacketDFPWM) {
			int time = ((AudioPacketDFPWM) packet).data.length * 8000 / ((AudioPacketDFPWM) packet).frequency;
			dataNew = new AudioDataDFPWM(packet, ((AudioPacketDFPWM) packet).data, time);
		} else {
			dataNew = new AudioDataDummy(packet);
		}

		packetNew = new org.libreflock.charset.api.audio.AudioPacket(dataNew, volume);
		for(Direction facing : Direction.VALUES) {
			BlockPos posO = pos.offset(facing);
			TileEntity tile = world.getTileEntity(posO);
			if(tile != null && tile.hasCapability(RECEIVER_CAPABILITY, facing.getOpposite())) {
				if(!ignoreComputronicsAPICheck && tile instanceof org.libreflock.computronics.api.audio.IAudioReceiver) {
					continue;
				}
				tile.getCapability(RECEIVER_CAPABILITY, facing.getOpposite()).receive(packetNew);
			}
		}

		if(packetNew.getSinkCount() > 0) {
			packetNew.send();
			return packetNew.getSinkCount();
		} else {
			return 0;
		}
	}

	public static boolean connects(@Nullable TileEntity tile, Direction dir) {
		return tile != null && (tile.hasCapability(SOURCE_CAPABILITY, dir)
			|| tile.hasCapability(RECEIVER_CAPABILITY, dir));
	}
}
