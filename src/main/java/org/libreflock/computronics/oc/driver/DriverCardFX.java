package org.libreflock.computronics.oc.driver;

import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.Connector;
import li.cil.oc.api.network.EnvironmentHost;
import li.cil.oc.api.network.Visibility;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.ParticleUtils;

import java.util.Map;
import java.util.Random;

public class DriverCardFX extends AbstractManagedEnvironment implements DeviceInfo {

	protected final EnvironmentHost container;

	public DriverCardFX(EnvironmentHost container) {
		this.container = container;
		this.setNode(Network.newNode(this, Visibility.Neighbors).
			withComponent("particle").
			withConnector().
			create());
	}

	// We allow spawning particle effects. The parameters are the particle
	// name, the position relative to the block the card is in to spawn
	// the particle at, as well as - optionally - the initial velocity.

	@Callback(doc = "function(name:string, xCoord:number, yCoord:number, zCoord:number [, defaultVelo:number]):boolean; "
		+ "function(name:string, xCoord:number, yCoord:number, zCoord:number [, xVelo:number, yVelo:number, zVelo:number]):boolean; "
		+ "Spawns a particle effect at the specified relative coordinates optionally with the specified velocity", direct = true, limit = 16)
	public Object[] spawn(Context context, Arguments args) {
		String name = args.checkString(0);

		if(name.length() > Short.MAX_VALUE) {
			return new Object[] { false, "name too long" };
		}
		ParticleTypes particle = ParticleUtils.getParticleType(name);
		if(particle == null) {
			return new Object[] { false, "invalid particle type" };
		}
		double xOffset = MathHelper.clamp(args.checkDouble(1), -65536D, 65536D);
		double yOffset = MathHelper.clamp(args.checkDouble(2), -65536D, 65536D);
		double zOffset = MathHelper.clamp(args.checkDouble(3), -65536D, 65536D);
		double distance = Math.sqrt(xOffset * xOffset + yOffset * yOffset + zOffset * zOffset);
		if(Config.COMMON.FX_RANGE.get() >= 0 && distance > Config.COMMON.FX_RANGE.get()) {
			return new Object[] { false, "out of range" };
		}
		if(((Connector) this.node()).tryChangeBuffer(0 - Config.COMMON.FX_ENERGY_COST.get() * distance)) {
			Random rng = container.world().random;
			double x = container.xPosition() + xOffset;
			double y = container.yPosition() + yOffset;
			double z = container.zPosition() + zOffset;
			double defaultv = (rng.nextDouble() * 0.1);
			if(args.count() >= 5) {
				defaultv = args.checkDouble(4);
			}
			double vx = defaultv * rng.nextGaussian();
			double vy = defaultv * rng.nextGaussian();
			double vz = defaultv * rng.nextGaussian();
			if(args.count() >= 7) {
				vx = args.checkDouble(4);
				vy = args.checkDouble(5);
				vz = args.checkDouble(6);
			}

			ParticleUtils.sendParticlePacket(particle, container.world(), x, y, z, vx, vy, vz);
			return new Object[] { true };
		}
		return new Object[] { false };
	}

	protected Map<String, String> deviceInfo;

	@Override
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			return deviceInfo = new OCUtils.Device(
				DeviceClass.Generic,
				"Particle emitter",
				OCUtils.Vendors.Siekierka,
				"Holotron FX-84"
			).deviceInfo();
		}
		return deviceInfo;
	}
}
