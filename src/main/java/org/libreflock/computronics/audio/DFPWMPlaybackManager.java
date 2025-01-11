package org.libreflock.computronics.audio;

import org.libreflock.computronics.reference.Config;
import org.libreflock.asielib.audio.StreamingAudioPlayer;
import org.libreflock.asielib.audio.StreamingPlaybackManager;

public class DFPWMPlaybackManager extends StreamingPlaybackManager {

	public DFPWMPlaybackManager(boolean isClient) {
		super(isClient);
	}

	@Override
	public StreamingAudioPlayer create() {
		return new StreamingAudioPlayer(false, false, Math.round(Config.TAPEDRIVE_BUFFER_MS / 250F));
	}
}
