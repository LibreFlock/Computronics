package org.libreflock.computronics.integration.enderio;

import com.enderio.core.api.common.util.IProgressTile;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.libreflock.computronics.integration.CCMultiPeripheral;
import org.libreflock.computronics.integration.DriverSpecificTileEntity;
import org.libreflock.computronics.integration.NamedManagedEnvironment;
import org.libreflock.computronics.reference.Names;

/**
 * @author Vexatos
 */
public class DriverProgressTile {

	public static class OCDriver extends DriverSpecificTileEntity<IProgressTile> {

		public class InternalManagedEnvironment extends NamedManagedEnvironment<IProgressTile> {

			public InternalManagedEnvironment(IProgressTile tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 2;
			}

			@Callback(doc = "function():boolean; Returns the progress of the machine")
			public Object[] getProgress(Context c, Arguments a) {
				return new Object[] { tile.getProgress() };
			}
		}

		public OCDriver() {
			super(IProgressTile.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side, IProgressTile tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<IProgressTile> {

		public CCDriver() {
		}

		public CCDriver(IProgressTile tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_MachineTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 2;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof IProgressTile) {
				return new CCDriver((IProgressTile) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getProgress" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getProgress() };
				}
			}
			return new Object[] {};
		}
	}
}
