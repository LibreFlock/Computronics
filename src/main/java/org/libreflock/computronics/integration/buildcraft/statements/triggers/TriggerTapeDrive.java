package org.libreflock.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import org.libreflock.computronics.tile.TapeDriveState.State;
import org.libreflock.computronics.tile.TileTapeDrive;

/**
 * @author Vexatos
 */
public class TriggerTapeDrive implements IComputronicsTrigger {

	private State state;

	public TriggerTapeDrive(State state) {
		this.state = state;
	}

	@Override
	public boolean isTriggerActive(TileEntity tile, EnumFacing side, IStatementContainer container, IStatementParameter[] statements) {
		return tile != null && tile instanceof TileTapeDrive
			&& ((TileTapeDrive) tile).getEnumState() == state;
	}
}
