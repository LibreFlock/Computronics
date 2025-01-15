package org.libreflock.computronics.util;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.asielib.network.Packet;

import java.util.HashMap;
import java.util.Map;

public class ParticleUtils {

	public static void sendParticlePacket(ParticleTypes particle, World world, double x, double y, double z, double vx, double vy, double vz) {
		try {
			Packet pkt = Computronics.packet.create(PacketType.PARTICLE_SPAWN.ordinal())
				.writeFloat((float) x).writeFloat((float) y).writeFloat((float) z)
				.writeFloat((float) vx).writeFloat((float) vy).writeFloat((float) vz)
				.writeInt(particle.getParticleID());
			Computronics.packet.sendToAllAround(pkt, new TargetPoint(world.provider.getDimension(), x, y, z, 64.0D));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private static Map<String, ParticleTypes> particleTypeMap = null;

	private static Map<String, ParticleTypes> particleMap() {
		if(particleTypeMap == null) {
			particleTypeMap = new HashMap<String, ParticleTypes>();
			for(ParticleTypes type : ParticleTypes.values()) {
				particleTypeMap.put(type.getParticleName(), type);
			}
		}
		return particleTypeMap;
	}

	public static boolean isValidParticleType(String name) {
		return particleMap().containsKey(name);
	}

	public static ParticleTypes getParticleType(String name) {
		return particleMap().get(name);
	}
}
