package org.libreflock.computronics.integration.buildcraft.statements.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import org.libreflock.computronics.api.tape.IItemTapeStorage;
import org.libreflock.computronics.tile.TapeDriveState;
import org.libreflock.computronics.tile.TileTapeDrive;

/**
 * @author Vexatos
 */
public class ActionTapeDrive implements IComputronicsAction {

	private TapeDriveState.State state;

	public ActionTapeDrive(TapeDriveState.State state) {
		this.state = state;
	}

	@Override
	public void actionActivate(TileEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters) {
		if(tile != null && tile instanceof TileTapeDrive && ((TileTapeDrive) tile).getEnumState() != this.state) {
			ItemStack is = ((TileTapeDrive) tile).getStackInSlot(0);
			if(is != null && is.getItem() instanceof IItemTapeStorage) {
				((TileTapeDrive) tile).switchState(state);
			}
		}
	}
}
