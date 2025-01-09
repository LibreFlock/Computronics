package org.libreflock.computronics.audio;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacketRegistry;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.lib.network.Packet;

public final class AudioUtils {

	private AudioUtils() {

	}

	public static synchronized void removePlayer(int managerId, int codecId) {
		AudioPacketRegistry.INSTANCE.getManager(managerId).removePlayer(codecId);
		try {
			Packet pkt = Computronics.packet.create(PacketType.AUDIO_STOP.ordinal())
				.writeInt(managerId)
				.writeInt(codecId);
			Computronics.packet.sendToAll(pkt);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static String positionId(int x, int y, int z) {
		return String.format("(%d, %d, %d)", x, y, z);
	}

	public static String positionId(BlockPos pos) {
		return positionId(pos.getX(), pos.getY(), pos.getZ());
	}

	public static String positionId(double x, double y, double z) {
		return positionId(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	public static String positionId(Vec3d pos) {
		return positionId(pos.x, pos.y, pos.z);
	}
}
