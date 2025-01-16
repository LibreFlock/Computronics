package org.libreflock.computronics.integration.charset.audio;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.libreflock.charset.api.audio.AudioSink;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.audio.AudioUtils;

import javax.annotation.Nullable;

/**
 * Created by asie on 6/14/16.
 */
public class AudioReceiverConverted implements IAudioReceiver {

	private final AudioSink sink;

	public AudioReceiverConverted(AudioSink sink) {
		this.sink = sink;
	}

	@Override
	public World getSoundWorld() {
		return sink.getWorld();
	}

	@Override
	public Vector3d getSoundPos() {
		return sink.getBlockPos();
	}

	@Override
	public int getSoundDistance() {
		return Math.round(sink.getDistance());
	}

	@Override
	public void receivePacket(AudioPacket packet, @Nullable Direction side) {

	}

	@Override
	public String getID() {
		return AudioUtils.positionId(sink.getBlockPos());
	}

	@Override
	public boolean connectsAudio(Direction side) {
		return false;
	}
}
