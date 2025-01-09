package org.libreflock.computronics.api.audio;

import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public interface IAudioReceiver extends IAudioConnection {

	@Nullable
	World getSoundWorld();

	Vector3d getSoundPos();

	int getSoundDistance();

	void receivePacket(AudioPacket packet, @Nullable Direction side);

	String getID();
}
