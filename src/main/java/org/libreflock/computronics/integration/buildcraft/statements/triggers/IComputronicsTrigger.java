package org.libreflock.computronics.integration.buildcraft.statements.triggers;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

/**
 * @author Vexatos
 */
public interface IComputronicsTrigger {

	boolean isTriggerActive(TileEntity tile, Direction side, IStatementContainer container, IStatementParameter[] statements);

}
