package org.libreflock.computronics.integration.cofh;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.libreflock.computronics.integration.CCMultiPeripheral;
import org.libreflock.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverEnergyHandler {

	public static class CCDriver extends CCMultiPeripheral<IEnergyHandler> {

		public CCDriver() {
		}

		public CCDriver(IEnergyProvider tile, World world, BlockPos pos) {
			super(tile, Names.CoFH_PoweredTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return -1;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IEnergyProvider && !(te instanceof IEnergyReceiver)) {
				return new CCDriver((IEnergyProvider) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getEnergyStored", "getMaxEnergyStored" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			if(arguments.length > 0 && !(arguments[0] instanceof Integer)) {
				throw new LuaException("first argument needs to be a number or nil");
			}
			switch(method) {
				case 0: {
					final Direction side = arguments.length > 0 ? Direction.getFront((Integer) arguments[0]) : null;
					return new Object[] { tile.getEnergyStored(side) };
				}
				case 1: {
					final Direction side = arguments.length > 0 ? Direction.getFront((Integer) arguments[0]) : null;
					return new Object[] { tile.getMaxEnergyStored(side) };
				}
			}
			return null;
		}
	}
}
