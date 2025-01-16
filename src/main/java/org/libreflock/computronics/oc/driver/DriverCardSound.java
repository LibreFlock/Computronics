package org.libreflock.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.internal.Rotatable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Visibility;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

// import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.integration.charset.audio.IntegrationCharsetAudio;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.ColorUtils;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.sound.Instruction.Close;
import org.libreflock.computronics.util.sound.Instruction.Open;
import org.libreflock.computronics.util.sound.Instruction.ResetAM;
import org.libreflock.computronics.util.sound.Instruction.ResetEnvelope;
import org.libreflock.computronics.util.sound.Instruction.ResetFM;
import org.libreflock.computronics.util.sound.Instruction.SetADSR;
import org.libreflock.computronics.util.sound.Instruction.SetAM;
import org.libreflock.computronics.util.sound.Instruction.SetFM;
import org.libreflock.computronics.util.sound.Instruction.SetFrequency;
import org.libreflock.computronics.util.sound.Instruction.SetLFSR;
import org.libreflock.computronics.util.sound.Instruction.SetVolume;
import org.libreflock.computronics.util.sound.SoundBoard;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;

import static org.libreflock.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;
import static org.libreflock.computronics.reference.Capabilities.AUDIO_SOURCE_CAPABILITY;

/**
 * @author Vexatos, gamax92
 */
public class DriverCardSound extends ManagedEnvironmentWithComponentConnector implements IAudioSource, ICapabilityProvider, SoundBoard.ISoundHost {

	protected final EnvironmentHost host;
	protected final SoundBoard board;

	public DriverCardSound(EnvironmentHost host) {
		this.host = host;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("sound").
			withConnector().
			create());
		this.board = new SoundBoard(this);
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(Direction side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return host.world();
		}

		@Override
		public Vector3d getSoundPos() {
			return new Vector3d(host.xPosition(), host.yPosition(), host.zPosition());
		}

		@Override
		public int getSoundDistance() {
			return Config.COMMON.SOUND_RADIUS.get();
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return host instanceof TileEntity ? AudioUtils.positionId(host.xPosition(), host.yPosition(), host.zPosition()) : "";
		}

	};

	@Override
	public boolean canUpdate() {
		return host.world() instanceof ClientWorld;
	}

	@Override
	public void update() {
		board.update();
	}

	@Override
	public void loadData(CompoundNBT nbt) {
		super.loadData(nbt);
		board.load(nbt);
	}

	@Override
	public void saveData(CompoundNBT nbt) {
		super.saveData(nbt);
		if(host.world() instanceof ServerWorld) {
			board.save(nbt);
		}
	}

	@Override
	public void onMessage(Message message) {
		if((message.name().equals("computer.stopped")
			|| message.name().equals("computer.started"))
			&& node().isNeighborOf(message.source())) {
			board.clearAndStop();
		}
	}

	protected int checkChannel(Arguments args, int index) {
		return board.checkChannel(args.checkInteger(index));
	}

	protected int checkChannel(Arguments args) {
		return checkChannel(args, 0);
	}

	@Callback(doc = "This is a bidirectional table of all valid modes.", direct = true, getter = true)
	public Object[] modes(Context context, Arguments args) {
		return new Object[] { SoundBoard.compileModes() };
	}

	@Callback(doc = "This is the number of channels this card provides.", direct = true, getter = true)
	public Object[] channel_count(Context context, Arguments args) {
		return new Object[] { board.process.states.size() };
	}

	@Callback(doc = "function(volume:number); Sets the general volume of the entire sound card to a value between 0 and 1. Not an instruction, this affects all channels directly.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setTotalVolume(Context context, Arguments args) {
		board.setTotalVolume(args.checkDouble(0));
		return new Object[] {};
	}

	@Callback(doc = "function(); Clears the instruction queue.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] clear(Context context, Arguments args) {
		board.clear();
		return new Object[] {};
	}

	@Callback(doc = "function(channel:number); Instruction; Opens the specified channel, allowing sound to be generated.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] open(Context context, Arguments args) {
		return board.tryAdd(new Open(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number); Instruction; Closes the specified channel, stopping sound from being generated.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] close(Context context, Arguments args) {
		return board.tryAdd(new Close(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, type:number); Instruction; Sets the wave type on the specified channel.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setWave(Context context, Arguments args) {
		return board.setWave(args.checkInteger(0), args.checkInteger(1));
	}

	@Callback(doc = "function(channel:number, frequency:number); Instruction; Sets the frequency on the specified channel.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFrequency(Context context, Arguments args) {
		return board.tryAdd(new SetFrequency(checkChannel(args), (float) args.checkDouble(1)));
	}

	@Callback(doc = "function(channel:number, initial:number, mask:number); Instruction; Makes the specified channel generate LFSR noise. Functions like a wave type.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setLFSR(Context context, Arguments args) {
		return board.tryAdd(new SetLFSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2)));
	}

	@Callback(doc = "function(duration:number); Instruction; Adds a delay of the specified duration in milliseconds, allowing sound to generate.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] delay(Context context, Arguments args) {
		return board.delay(args.checkInteger(0));
	}

	@Callback(doc = "function(channel:number, modIndex:number, intensity:number); Instruction; Assigns a frequency modulator channel to the specified channel with the specified intensity.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setFM(Context context, Arguments args) {
		return board.tryAdd(new SetFM(checkChannel(args), checkChannel(args, 1), (float) args.checkDouble(2)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes the specified channel's frequency modulator.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetFM(Context context, Arguments args) {
		return board.tryAdd(new ResetFM(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, modIndex:number); Instruction; Assigns an amplitude modulator channel to the specified channel.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setAM(Context context, Arguments args) {
		return board.tryAdd(new SetAM(checkChannel(args), checkChannel(args, 1)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes the specified channel's amplitude modulator.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetAM(Context context, Arguments args) {
		return board.tryAdd(new ResetAM(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, attack:number, decay:number, attenuation:number, release:number); Instruction; Assigns ADSR to the specified channel with the specified phase durations in milliseconds and attenuation between 0 and 1.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setADSR(Context context, Arguments args) {
		return board.tryAdd(new SetADSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
	}

	@Callback(doc = "function(channel:number); Instruction; Removes ADSR from the specified channel.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] resetEnvelope(Context context, Arguments args) {
		return board.tryAdd(new ResetEnvelope(checkChannel(args)));
	}

	@Callback(doc = "function(channel:number, volume:number); Instruction; Sets the volume of the channel between 0 and 1.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		return board.tryAdd(new SetVolume(checkChannel(args), (float) args.checkDouble(1)));
	}

	@Callback(doc = "function(); Starts processing the queue; Returns true is processing began, false if there is still a queue being processed.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] process(Context context, Arguments args) {
		return board.process();
	}

	@Override
	public void sendMusicPacket(SoundCardPacket pkt) {
		int receivers = 0;
		boolean sent = false;
		if(host instanceof TileEntity) {
			if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
				int oldReceivers = receivers;
				receivers += IntegrationCharsetAudio.send(host.world(), ((TileEntity) host).getBlockPos(), pkt, 1.0F, true);
				if(receivers > oldReceivers) {
					sent = true;
				}
			}
		}
		if(!sent) {
			if(host instanceof TileEntity) {
				for(Direction dir : Direction.values()) {
					TileEntity tile = host.world().getBlockEntity(((TileEntity) host).getBlockPos().relative(dir));
					if(tile != null) {
						if(!tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).equals(LazyOptional.empty())) {
							IColorable hostCol = ColorUtils.getColorable((TileEntity) host, dir);
							IColorable targetCol = ColorUtils.getColorable(tile, dir.getOpposite());
							if(hostCol != null && targetCol != null && hostCol.canBeColored() && targetCol.canBeColored()
								&& !ColorUtils.isSameOrDefault(hostCol, targetCol)) {
								continue;
							}
							tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).orElse(null).receivePacket(pkt, dir.getOpposite());
							receivers++;
						}
					}
				}
			}
			if(receivers == 0) {
				internalSpeaker.receivePacket(pkt, null);
			}
			pkt.sendPacket();
		}
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
		facing = host instanceof Rotatable ? ((Rotatable) host).toGlobal(facing) : facing;
		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
			if(capability == IntegrationCharsetAudio.SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
				return true;
			}
		}
		return capability == AUDIO_SOURCE_CAPABILITY && facing != null && connectsAudio(facing);
	}

	private Object charsetAudioSource;

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
		facing = host instanceof Rotatable ? ((Rotatable) host).toGlobal(facing) : facing;
		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
			if(capability == IntegrationCharsetAudio.SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
				if(charsetAudioSource == null) {
					charsetAudioSource = new org.libreflock.charset.api.audio.IAudioSource() {
					};
				}
				return IntegrationCharsetAudio.SOURCE_CAPABILITY.cast((org.libreflock.charset.api.audio.IAudioSource) charsetAudioSource);
			}
		}
		if(capability == AUDIO_SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
			return AUDIO_SOURCE_CAPABILITY.cast(this);
		}
		return null;
	}

	@Override
	public boolean connectsAudio(Direction side) {
		if(host instanceof TileEntity) {
			IColorable hostCol = ColorUtils.getColorable((TileEntity) host, side);
			IColorable targetCol = ColorUtils.getColorable(host.world().getTileEntity(((TileEntity) host).getBlockPos().offset(side)), side.getOpposite());
			if(hostCol != null && targetCol != null && hostCol.canBeColored() && targetCol.canBeColored()) {
				return ColorUtils.isSameOrDefault(hostCol, targetCol);
			}
		}
		return true;
	}

	@Override
	public int getSourceId() {
		return board.codecId;
	}

	@Override
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Audio interface",
			OCUtils.Vendors.Yanaki,
			"MinoSound 244-X"
		);
	}

	@Override
	public World world() {
		return host.world();
	}

	@Override
	public boolean tryConsumeEnergy(double energy) {
		return node.tryChangeBuffer(-energy);
	}

	@Override
	public String address() {
		return node().address();
	}

	@Override
	public Vector3d position() {
		return new Vector3d(host.xPosition(), host.yPosition(), host.zPosition());
	}

	@Override
	public void setDirty() {
		// Not necessary here.
	}

}
