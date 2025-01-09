package org.libreflock.computronics.api.multiperipheral;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.reference.Mods;

/**
 * Allows having Multiple peripherals merged into a single one.
 * <p/>
 * Register is using
 * @author Vexatos
 */
public interface IMultiPeripheral extends IPeripheral {

	/**
	 * The priority of the peripheral. Higher number means that this peripheral's methods will be preferred.
	 * @return The priority, default should be 0
	 */
	@Optional.Method(modid = Mods.ComputerCraft)
	public int peripheralPriority();
}
