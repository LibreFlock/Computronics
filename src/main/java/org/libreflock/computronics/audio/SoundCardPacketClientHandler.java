package org.libreflock.computronics.audio;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacketClientHandler;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.util.sound.AudioType;
import org.libreflock.computronics.util.sound.AudioUtil;
import org.libreflock.computronics.util.sound.AudioUtil.AudioProcess;
import org.libreflock.computronics.util.sound.Instruction;
import org.libreflock.computronics.util.sound.Instruction.Close;
import org.libreflock.computronics.util.sound.Instruction.Delay;
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
import org.libreflock.computronics.util.sound.Instruction.SetWave;
import org.libreflock.computronics.util.sound.Instruction.SetWhiteNoise;
import org.libreflock.lib.audio.StreamingAudioPlayer;
import org.libreflock.lib.network.Packet;

import javax.annotation.Nullable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author gamax92
 */
@OnlyIn(Dist.CLIENT)
public class SoundCardPacketClientHandler extends AudioPacketClientHandler {

	private Map<String, AudioProcess> processMap = new HashMap<String, AudioProcess>();
	private int sampleRate = Config.SOUND_SAMPLE_RATE;

	public void setProcess(String address, @Nullable AudioProcess process) {
		if(process != null) {
			processMap.put(address, process);
		} else {
			processMap.remove(address);
		}
	}

	@Override
	protected void readData(Packet packet, int packetId, int codecId) throws IOException {
		String address = packet.readString();
		int size = packet.readInt();
		Queue<Instruction> buffer = new ArrayDeque<Instruction>();
		for(int i = 0; i < size; i++) {
			int type = packet.readByte();
			switch(type) {
				case 0:
					buffer.add(new Open(packet.readByte()));
					break;
				case 1:
					buffer.add(new Close(packet.readByte()));
					break;
				case 2:
					buffer.add(new SetWave(packet.readByte(), AudioType.fromIndex(packet.readInt())));
					break;
				case 3:
					buffer.add(new Delay(packet.readInt()));
					break;
				case 4:
					buffer.add(new SetFM(packet.readByte(), packet.readInt(), packet.readFloat()));
					break;
				case 5:
					buffer.add(new ResetFM(packet.readByte()));
					break;
				case 6:
					buffer.add(new SetAM(packet.readByte(), packet.readInt()));
					break;
				case 7:
					buffer.add(new ResetAM(packet.readByte()));
					break;
				case 8:
					buffer.add(new SetADSR(packet.readByte(), packet.readInt(), packet.readInt(), packet.readFloat(), packet.readInt()));
					break;
				case 9:
					buffer.add(new ResetEnvelope(packet.readByte()));
					break;
				case 10:
					buffer.add(new SetVolume(packet.readByte(), packet.readFloat()));
					break;
				case 11:
					buffer.add(new SetFrequency(packet.readByte(), packet.readFloat()));
					break;
				case 12:
					buffer.add(new SetWhiteNoise(packet.readByte()));
					break;
				case 13:
					buffer.add(new SetLFSR(packet.readByte(), packet.readInt(), packet.readInt()));
					break;
			}
		}

		if(!processMap.containsKey(address)) {
			setProcess(address, new AudioUtil.AudioProcess(Config.SOUND_CARD_CHANNEL_COUNT));
		}
		AudioProcess process = processMap.get(address);

		ByteArrayOutputStream data = new ByteArrayOutputStream();
		while(!buffer.isEmpty() || process.delay > 0) {
			if(process.delay > 0) {
				int sampleCount = process.delay * sampleRate / 1000;
				for(int i = 0; i < sampleCount; ++i) {
					double sample = 0;
					for(AudioUtil.State state : process.states) {
						sample += state.gate.getValue(process, state);
					}
					sample = Math.max(Math.min(sample, 1), -1);
					double value = (sample * 127.0D + process.error);
					process.error = value - Math.floor(value);
					int bvalue = ((byte) Math.floor(value)) ^ 0x80;
					data.write((byte) bvalue);
				}
				process.delay = 0;
			} else {
				Instruction inst = buffer.poll();
				inst.encounter(process);
			}
		}

		if(data.size() > 0) {
			StreamingAudioPlayer codec = Computronics.instance.soundCardAudio.getPlayer(codecId);
			codec.setSampleRate(sampleRate);
			codec.push(data.toByteArray());
		}
	}

	@Override
	protected void playData(int packetId, int codecId, float x, float y, float z, int distance, byte volume, String deviceId) {
		StreamingAudioPlayer codec = Computronics.instance.soundCardAudio.getPlayer(codecId);

		codec.setHearing(distance, (volume * Config.SOUND_VOLUME) / (127.0F * 127.0F));
		try {
			codec.play("computronics:soundcard" + codecId + (deviceId.isEmpty() ? "" : "-" + deviceId), x, y, z, 1F);
		} catch(NullPointerException e) {
			// This exception occurs when there is no data to play, and is harmless.
		}
	}
}
