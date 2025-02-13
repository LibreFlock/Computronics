package org.libreflock.computronics.gui;

import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import org.libreflock.computronics.tile.TapeDriveState.State;

/**
 * @author Vexatos
 */
public interface IGuiTapeDrive {

	void setState(State state);

	State getState();

	boolean isLocked(Slot slot, int index, int button, ClickType type);

	boolean shouldCheckHotbarKeys();
}
