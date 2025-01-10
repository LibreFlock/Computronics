package org.libreflock.computronics.integration.buildcraft.statements.actions;

import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;

/**
 * @author Vexatos
 */
public interface IComputronicsAction {

	void actionActivate(TileEntity tile, Direction side, IStatementContainer container, IStatementParameter[] parameters);

}
