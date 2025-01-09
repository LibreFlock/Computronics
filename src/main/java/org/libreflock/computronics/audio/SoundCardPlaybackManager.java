package org.libreflock.computronics.audio;

import org.libreflock.lib.audio.StreamingAudioPlayer;
import org.libreflock.lib.audio.StreamingPlaybackManager;

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
