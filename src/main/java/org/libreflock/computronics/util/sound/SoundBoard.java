package org.libreflock.computronics.util.sound;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.audio.SoundCardPacket;
import org.libreflock.computronics.audio.SoundCardPacketClientHandler;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.util.sound.AudioUtil.AudioProcess;
import org.libreflock.computronics.util.sound.Instruction.Delay;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Vexatos
 */
public class SoundBoard {

	public SoundBoard(ISoundHost host) {
		process = new AudioProcess(Config.Common.SOUND_CARD_CHANNEL_COUNT);
		this.host = host;
		initBuffers();
	}

	private ISoundHost host;
	public AudioProcess process;
	private ArrayDeque<Instruction> buildBuffer;
	private ArrayDeque<Instruction> nextBuffer;

	private int buildDelay = 0;
	private int nextDelay = 0;
	private long timeout = System.currentTimeMillis();
	private int soundVolume = 127;
	public Integer codecId;
	private String clientAddress = null;

	private final int soundTimeoutMS = 250;

	public static class SyncHandler {

		static Set<SoundBoard> envs = Collections.newSetFromMap(new WeakHashMap<SoundBoard, Boolean>());

		private boolean isAtLocation(IChunk chunk, int x, int z) {
			ChunkPos pos = chunk.getPos();
			return pos.x == x && pos.z == z;
		}

		@OnlyIn(Dist.CLIENT)
		private static SoundCardPacketClientHandler getHandler() {
			return (SoundCardPacketClientHandler) AudioPacketRegistry.INSTANCE.getClientHandler(AudioPacketRegistry.INSTANCE.getId(SoundCardPacket.class));
		}

		@SubscribeEvent
		public void onChunkUnload(ChunkEvent.Unload evt) {
			for(SoundBoard env : envs) {
				Vector3d pos = env.host.position();
				if(env.host.world() == evt.getWorld() && isAtLocation(evt.getChunk(), MathHelper.floor(pos.x) >> 4, MathHelper.floor(pos.z) >> 4)) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}

		@SubscribeEvent
		public void onWorldUnload(WorldEvent.Unload evt) {
			for(SoundBoard env : envs) {
				if(env.host.world() == evt.getWorld()) {
					getHandler().setProcess(env.clientAddress, null);
				}
			}
		}
	}

	private boolean bufferInit = false;

	private void initBuffers() {
		if(bufferInit) {
			return;
		}
		World world = host.world();
		if(world == null) {
			return;
		}
		if(world instanceof ServerWorld) {
			SyncHandler.envs.add(this);
			buildBuffer = null;
			nextBuffer = null;
			if(clientAddress != null) {
				SyncHandler.getHandler().setProcess(clientAddress, process);
			}
		} else {
			buildBuffer = new ArrayDeque<Instruction>();
			nextBuffer = new ArrayDeque<Instruction>();
		}
		bufferInit = true;
	}

	private boolean dirty = false;

	public void update() {
		initBuffers();
		if(host.world() instanceof ClientWorld) {
			if(nextBuffer != null && !nextBuffer.isEmpty() && System.currentTimeMillis() >= timeout - 100) {
				final ArrayDeque<Instruction> clone;
				synchronized(nextBuffer) {
					clone = nextBuffer.clone();
					timeout = timeout + nextDelay;
					nextBuffer.clear();
				}
				sendSound(clone);
				dirty = true;
			} else if(codecId != null && System.currentTimeMillis() >= timeout + soundTimeoutMS) {
				AudioUtils.removePlayer(Computronics.instance.soundCardManagerId, codecId);
				codecId = null;
			}
			if(dirty) {
				host.setDirty();
			}
		}
	}

	public void load(CompoundNBT nbt) {
		if(nbt.contains("process")) {
			process.load(nbt.getCompound("process"));
		}
		if(nbt.contains("node")) {
			CompoundNBT nodeNBT = nbt.getCompound("node");
			if(nodeNBT.contains("address")) {
				clientAddress = nodeNBT.getString("address");
			}
		}
		if(nbt.contains("bbuffer")) {
			if(buildBuffer != null) {
				synchronized(buildBuffer) {
					buildBuffer.clear();
					buildBuffer.addAll(Instruction.fromNBT(nbt.getList("bbuffer", Constants.NBT.TAG_COMPOUND)));
					buildDelay = 0;
					for(Instruction inst : buildBuffer) {
						if(inst instanceof Delay) {
							buildDelay += ((Delay) inst).delay;
						}
					}
				}
			}
		}
		if(nbt.contains("nbuffer")) {
			if(nextBuffer != null) {
				synchronized(nextBuffer) {
					nextBuffer.clear();
					nextBuffer.addAll(Instruction.fromNBT(nbt.getList("nbuffer", Constants.NBT.TAG_COMPOUND)));
					nextDelay = 0;
					for(Instruction inst : nextBuffer) {
						if(inst instanceof Delay) {
							nextDelay += ((Delay) inst).delay;
						}
					}
				}
			}
		}
		if(nbt.contains("volume")) {
			soundVolume = nbt.getByte("volume");
		}
	}

	public void save(CompoundNBT nbt) {
		CompoundNBT processNBT = new CompoundNBT();
		nbt.put("process", processNBT);
		process.save(processNBT);
		if(buildBuffer != null && !buildBuffer.isEmpty()) {
			ListNBT buildNBT = new ListNBT();
			synchronized(buildBuffer) {
				Instruction.toNBT(buildNBT, buildBuffer);
			}
			nbt.put("bbuffer", buildNBT);
		}
		if(nextBuffer != null && !nextBuffer.isEmpty()) {
			ListNBT nextNBT = new ListNBT();
			synchronized(nextBuffer) {
				Instruction.toNBT(nextNBT, nextBuffer);
			}
			nbt.put("nbuffer", nextNBT);
		}
		nbt.putByte("volume", (byte) soundVolume);
	}

	public void clearAndStop() {
		if(buildBuffer != null && !buildBuffer.isEmpty()) {
			synchronized(buildBuffer) {
				buildBuffer.clear();
			}
		}
		if(nextBuffer != null && !nextBuffer.isEmpty()) {
			synchronized(nextBuffer) {
				nextBuffer.clear();
			}
		}
		buildDelay = 0;
		if(codecId != null) {
			AudioUtils.removePlayer(Computronics.instance.soundCardManagerId, codecId);
			codecId = null;
		}
		dirty = true;
	}

	public Object[] tryAdd(Instruction inst) {
		synchronized(buildBuffer) {
			if(buildBuffer.size() >= Config.Common.SOUND_CARD_QUEUE_SIZE) {
				return new Object[] { false, "too many instructions" };
			}
			buildBuffer.add(inst);
		}
		dirty = true;
		return new Object[] { true };
	}

	private static HashMap<Object, Object> modes;

	public static HashMap<Object, Object> compileModes() {
		if(modes == null) {
			HashMap<Object, Object> modes = new HashMap<Object, Object>(AudioType.VALUES.length * 2);
			for(AudioType value : AudioType.VALUES) {
				String name = value.name().toLowerCase(Locale.ENGLISH);
				modes.put(value.ordinal() + 1, name);
				modes.put(name, value.ordinal() + 1);
			}
			// Adding white noise
			modes.put("noise", -1);
			modes.put(-1, "noise");
			SoundBoard.modes = modes;
		}
		return modes;
	}

	public int checkChannel(int channel) {
		channel--;
		if(channel >= 0 && channel < process.states.size()) {
			return channel;
		}
		throw new IllegalArgumentException("invalid channel: " + (channel + 1));
	}

	public void clear() {
		synchronized(buildBuffer) {
			buildBuffer.clear();
		}
		buildDelay = 0;
		dirty = true;
	}

	public Object[] delay(int duration) {
		if(duration < 0 || duration > Config.Common.SOUND_CARD_MAX_DELAY) {
			throw new IllegalArgumentException("invalid duration. must be between 0 and " + Config.Common.SOUND_CARD_MAX_DELAY);
		}
		if(buildDelay + duration > Config.Common.SOUND_CARD_MAX_DELAY) {
			return new Object[] { false, "too many delays in queue" };
		}
		buildDelay += duration;
		return tryAdd(new Delay(duration));
	}

	public void setTotalVolume(double volume) {
		if(volume < 0.0F) {
			volume = 0.0F;
		}
		if(volume > 1.0F) {
			volume = 1.0F;
		}
		soundVolume = MathHelper.floor(volume * 127.0F);
	}

	public Object[] setWave(int channel, int mode) {
		channel = checkChannel(channel);
		mode--;
		switch(mode) {
			case -2:
				return tryAdd(new Instruction.SetWhiteNoise(channel));
			default:
				if(mode >= 0 && mode < AudioType.VALUES.length) {
					return tryAdd(new Instruction.SetWave(channel, AudioType.fromIndex(mode)));
				}
		}
		throw new IllegalArgumentException("invalid mode: " + (mode + 1));
	}

	public Object[] process() {
		synchronized(buildBuffer) {
			if(nextBuffer != null && nextBuffer.isEmpty()) {
				if(buildBuffer.size() == 0) {
					return new Object[] { true };
				}
				if(!host.tryConsumeEnergy(Config.Common.SOUND_CARD_ENERGY_COST * (buildDelay / 1000D))) {
					return new Object[] { false, "not enough energy" };
				}
				synchronized(nextBuffer) {
					nextBuffer.addAll(new ArrayDeque<Instruction>(buildBuffer));
				}
				nextDelay = buildDelay;
				buildBuffer.clear();
				buildDelay = 0;
				if(System.currentTimeMillis() > timeout) {
					timeout = System.currentTimeMillis();
				}
				dirty = true;
				return new Object[] { true };
			} else {
				return new Object[] { false, System.currentTimeMillis() - timeout };
			}
		}
	}

	private void sendMusicPacket(Queue<Instruction> instructions) {
		if(codecId == null) {
			codecId = Computronics.instance.soundCardAudio.newPlayer();
			Computronics.instance.soundCardAudio.getPlayer(codecId);
		}
		SoundCardPacket pkt = new SoundCardPacket(host, (byte) soundVolume, host.address(), instructions);
		host.sendMusicPacket(pkt);
	}

	protected void sendSound(Queue<Instruction> buffer) {
		Queue<Instruction> sendBuffer = new ArrayDeque<Instruction>();
		while(!buffer.isEmpty() || process.delay > 0) {
			if(process.delay > 0) {
				process.delay = 0;
			} else {
				Instruction inst = buffer.poll();
				inst.encounter(process);
				sendBuffer.add(inst);
			}
		}
		if(sendBuffer.size() > 0) {
			sendMusicPacket(sendBuffer);
		}
	}

	public interface ISoundHost extends IAudioSource {

		World world();

		boolean tryConsumeEnergy(double energy);

		String address();

		Vector3d position();

		void sendMusicPacket(SoundCardPacket pkt);

		void setDirty();
	}

}
