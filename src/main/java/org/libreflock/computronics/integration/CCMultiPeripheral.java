package org.libreflock.computronics.integration;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.libreflock.computronics.api.multiperipheral.ICombinedMultiPeripheral;

/**
 * @author Vexatos
 */
public abstract class CCMultiPeripheral<T> extends CCTilePeripheral<T>
	implements ICombinedMultiPeripheral {

	protected CCMultiPeripheral() {

	}

	protected CCMultiPeripheral(T tile, String name, World world, BlockPos pos) {
		super(tile, name, world, pos);
	}

	@Override
	public int peripheralPriority() {
		return 0;
	}
}
