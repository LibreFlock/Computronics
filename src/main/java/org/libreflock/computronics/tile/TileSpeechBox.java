package org.libreflock.computronics.tile;

// import dan200.computercraft.api.lua.ILuaContext;
// import dan200.computercraft.api.lua.LuaException;
// import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;

// import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.audio.tts.TextToSpeech.ICanSpeak;
import org.libreflock.computronics.integration.charset.audio.IntegrationCharsetAudio;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import static org.libreflock.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;

/**
 * @author Vexatos
 */
public class TileSpeechBox extends TileEntityPeripheralBase implements IAudioSource, ITickable, ICanSpeak {

	public TileSpeechBox() {
		super("speech_box");
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(Direction side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return getLevel();
		}

		@Override
		public Vector3d getSoundPos() {
			return new Vector3d(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D);
		}

		@Override
		public int getSoundDistance() {
			return Config.COMMON.TAPEDRIVE_DISTANCE.get();
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return AudioUtils.positionId(getBlockPos());
		}

	};

	private long lastCodecTime;
	private int codecId = -1;
	protected int packetSize = 1500;
	protected int soundVolume = 127;
	private boolean locked = false;

	@Override
	public void update() {
		super.update();
		AudioPacket pkt = null;
		long time = System.nanoTime();
		if((time - (250 * 1000000)) > lastCodecTime) {
			lastCodecTime += (250 * 1000000);
			pkt = createMusicPacket(this);
		}
		if(pkt != null) {
			int receivers = 0;

			boolean sent = false;
			if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
				int oldReceivers = receivers;
				receivers += IntegrationCharsetAudio.send(getLevel(), getBlockPos(), pkt, getVolume(), true);
				if(receivers > oldReceivers) {
					sent = true;
				}
			}

			if(!sent) {
				for(Direction dir : Direction.values()) {
					TileEntity tile = getLevel().getBlockEntity(getBlockPos().relative(dir));
					if(tile != null && !tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).equals(LazyOptional.empty())) {
						IColorable targetCol = org.libreflock.computronics.util.ColorUtils.getColorable(tile, dir.getOpposite());
						if(targetCol != null && targetCol.canBeColored()
							&& !org.libreflock.computronics.util.ColorUtils.isSameOrDefault(this, targetCol)) {
							continue;
						}
						tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).orElse(null).receivePacket(pkt, dir.getOpposite());
						receivers++;
					}
				}
				if(receivers == 0) {
					internalSpeaker.receivePacket(pkt, null);
				}

				pkt.sendPacket();
			}
		}
	}

	@Override
	public void startTalking(byte[] data) {
		if(getLevel() instanceof ServerWorld) {
			return;
		}
		storage = new ByteArrayInputStream(data);
		codecId = Computronics.instance.audio.newPlayer();
		Computronics.instance.audio.getPlayer(codecId);
		lastCodecTime = System.nanoTime();
	}

	private void stopTalking() {
		if(this.level != null && getLevel() instanceof ServerWorld) {
			return;
		}
		AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
		locked = false;
		storage = null;
	}

	private Object[] sendNewText(String text) throws IOException {
		// if(Computronics.tts != null) {
		// 	locked = true;
		// 	Computronics.tts.say(this, text);
		// } else {
		// 	return new Object[] { false, "text-to-speech system not available" };
		// }
		// return new Object[] { true };
		return new Object[] {false, "text-to-speech system not available"} ;
	}

	private ByteArrayInputStream storage;

	@Nullable
	private AudioPacket createMusicPacket(IAudioSource source) {
		if(storage == null) {
			return null;
		}
		byte[] pktData = new byte[packetSize];
		int amount = storage.read(pktData, 0, pktData.length); // read data into packet array

		if(amount > 0) {
			return new AudioPacketDFPWM(source, (byte) soundVolume, packetSize * 8 * 4, amount == packetSize ? pktData : Arrays.copyOf(pktData, amount));
		} else {
			stopTalking();
			return null;
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		super.writeToNBT(tag);
		if(tag.contains("vo")) {
			this.soundVolume = tag.getByte("vo");
		} else {
			this.soundVolume = 127;
		}
		return tag;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		if(this.soundVolume != 127) {
			tag.putByte("vo", (byte) this.soundVolume);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		stopTalking();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		stopTalking();
	}

	public void setVolume(float volume) {
		if(volume < 0.0F) {
			volume = 0.0F;
		}
		if(volume > 1.0F) {
			volume = 1.0F;
		}
		this.soundVolume = (int) Math.floor(volume * 127.0F);
	}

	@Callback(doc = "function(text:string):boolean; Say the specified message. Returns true on success, false and an error message otherwise.")
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		if(locked || storage != null) {
			return new Object[] { false, "already processing" };
		}
		String text = args.checkString(0);
		if(text.length() > Config.COMMON.TTS_MAX_LENGTH.get()) { // TODO: tts max length
			return new Object[] { false, "text too long" };
		}
		try {
			return this.sendNewText(text);
		} catch(IOException e) {
			throw new IllegalArgumentException("could not send string");
		} catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Callback(doc = "function():boolean; Stops the currently spoken phrase. Returns true on success, false and an error message otherwise.")
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] stop(Context context, Arguments args) {
		if(locked || storage != null) {
			stopTalking();
			return new Object[] { true };
		}
		return new Object[] { false, "not talking" };
	}

	@Callback(doc = "function():boolean; Returns true if the device is currently processing text.", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] isProcessing(Context context, Arguments args) {
		return new Object[] { locked || storage != null };
	}

	@Callback(doc = "function(speed:number); Sets the volume of the speech box. Needs to be beween 0 and 1")
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setVolume(Context context, Arguments args) {
		this.setVolume((float) args.checkDouble(0));
		return new Object[] {};
	}

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public String[] getMethodNames() {
	// 	return new String[] { "say", "stop", "isProcessing", "setVolume" };
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
	// 	switch(method) {
	// 		case 0: {
	// 			if(arguments.length < 1 || !(arguments[0] instanceof String)) {
	// 				throw new LuaException("first argument needs to be a string");
	// 			}
	// 			if(locked || storage != null) {
	// 				return new Object[] { false, "already processing" };
	// 			}
	// 			String text = (String) arguments[0];
	// 			if(text.length() > Config.TTS_MAX_LENGTH) {
	// 				return new Object[] { false, "text too long" };
	// 			}
	// 			try {
	// 				return this.sendNewText(text);
	// 			} catch(IOException e) {
	// 				throw new LuaException("could not send string");
	// 			}
	// 		}
	// 		case 1: {
	// 			if(locked || storage != null) {
	// 				stopTalking();
	// 				return new Object[] { true };
	// 			}
	// 			return new Object[] { false, "not talking" };
	// 		}
	// 		case 2: {
	// 			return new Object[] { locked || storage != null };
	// 		}
	// 		case 3: {
	// 			if(arguments.length < 1 || !(arguments[0] instanceof Number)) {
	// 				throw new LuaException("first argument needs to be a number");
	// 			}
	// 			this.setVolume(((Number) arguments[0]).floatValue());
	// 			return new Object[] {};
	// 		}
	// 	}
	// 	return new Object[] {};
	// }

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Text-To-Speech Interface",
			OCUtils.Vendors.DFKI,
			"Mary"
		);
	}

	@Override
	public int getSourceId() {
		return codecId;
	}

	@Override
	public boolean connectsAudio(Direction side) {
		return getLevel().getBlockState(getBlockPos()).getValue(Computronics.speechBox.rotation.FACING) != side;
	}
}
