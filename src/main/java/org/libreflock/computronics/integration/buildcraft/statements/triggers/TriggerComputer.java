package org.libreflock.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import li.cil.oc.api.internal.Case;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

/**
 * @author Vexatos
 */
public class TriggerComputer {

	public static class Running implements IComputronicsTrigger {

		@Override
		public boolean isTriggerActive(TileEntity tile, Direction side, IStatementContainer container, IStatementParameter[] statements) {
			return tile != null && tile instanceof Case && ((Case) tile).machine() != null
				&& ((Case) tile).machine().isRunning();
		}
	}

	public static class Stopped implements IComputronicsTrigger {

		@Override
		public boolean isTriggerActive(TileEntity tile, Direction side, IStatementContainer container, IStatementParameter[] statements) {
			return tile != null && tile instanceof Case && ((Case) tile).machine() != null
				&& !((Case) tile).machine().isRunning()
				&& !((Case) tile).machine().isPaused();
		}
	}
}
