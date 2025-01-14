package org.libreflock.computronics.tile;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.api.tape.ITapeStorage;
import org.libreflock.computronics.audio.AudioUtils;

import javax.annotation.Nullable;
import java.util.Arrays;

public class TapeDriveState {

	public enum State {
		STOPPED,
		PLAYING,
		REWINDING,
		FORWARDING;

		public static final State[] VALUES = values();
	}

	private State state = State.STOPPED;
	private int codecId = -1;//, packetId;
	private long lastCodecTime;
	public int packetSize = 1500;
	public int soundVolume = 127;
	private ITapeStorage storage;

	@Nullable
	public ITapeStorage getStorage() {
		return storage;
	}

	public void setStorage(@Nullable ITapeStorage storage) {
		this.storage = storage;
	}

	public void setState(State state) {
		this.state = state;
	}

	public boolean setSpeed(float speed) {
		if(speed < 0.25F || speed > 2.0F) {
			return false;
		}
		this.packetSize = Math.round(1500 * speed);
		return true;
	}

	public int getId() {
		return codecId;
	}

	public byte getVolume() {
		return (byte) soundVolume;
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

	public void switchState(World world, State newState) {
		if(world instanceof ClientWorld) { // Client-side happening
			if(newState == state) {
				return;
			}
		}
		if(world instanceof ServerWorld) { // Server-side happening
			if(this.storage == null) {
				newState = State.STOPPED;
			}
			if(state == State.PLAYING) { // State is playing - stop playback
				AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
			}
			if(newState == State.PLAYING) { // Time to play again!
				codecId = Computronics.instance.audio.newPlayer();
				Computronics.instance.audio.getPlayer(codecId);
				lastCodecTime = System.nanoTime();
			}
		}
		state = newState;
		//sendState();
	}

	public State getState() {
		return state;
	}

	@Nullable
	private AudioPacket createMusicPacket(IAudioSource source, World world) {
		byte[] pktData = new byte[packetSize];
		int amount = storage.read(pktData, false); // read data into packet array

		if(amount < packetSize) {
			switchState(world, State.STOPPED);
		}

		if(amount > 0) {
			return new AudioPacketDFPWM(source, getVolume(), packetSize * 8 * 4, amount == packetSize ? pktData : Arrays.copyOf(pktData, amount));
		} else {
			return null;
		}
	}

	@Nullable
	public AudioPacket update(IAudioSource source, World world) {
		if(world instanceof ClientWorld) {
			switch(state) {
				case PLAYING: {
					if(storage.getPosition() >= storage.getSize() || storage.getPosition() < 0) {
						storage.setPosition(storage.getPosition());
					}
					long time = System.nanoTime();
					if((time - (250 * 1000000)) > lastCodecTime) {
						lastCodecTime += (250 * 1000000);
						return createMusicPacket(source, world);
					}
				}
				break;
				case REWINDING: {
					int seeked = storage.seek(-2048);
					if(seeked > -2048) {
						switchState(world, State.STOPPED);
					}
				}
				break;
				case FORWARDING: {
					int seeked = storage.seek(2048);
					if(seeked < 2048) {
						switchState(world, State.STOPPED);
					}
				}
				break;
				case STOPPED: {
				}
				break;
			}
		}
		return null;
	}
}
