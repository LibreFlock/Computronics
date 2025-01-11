package org.libreflock.computronics.audio;

import org.libreflock.asielib.audio.StreamingAudioPlayer;
import org.libreflock.asielib.audio.StreamingPlaybackManager;

/**
 * @author gamax92
 */
public class SoundCardPlaybackManager extends StreamingPlaybackManager {

	public SoundCardPlaybackManager(boolean isClient) {
		super(isClient);
	}

	@Override
	public StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(false, false, -1);
	}
}
