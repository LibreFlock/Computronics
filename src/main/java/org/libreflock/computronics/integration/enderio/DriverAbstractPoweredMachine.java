package org.libreflock.computronics.integration.enderio;

import crazypants.enderio.base.machine.baselegacy.AbstractPoweredMachineEntity;
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
public class DriverAbstractPoweredMachine {

	public static class OCDriver extends DriverSpecificTileEntity<AbstractPoweredMachineEntity> {

		public static class InternalManagedEnvironment extends NamedManagedEnvironment<AbstractPoweredMachineEntity> {

			public InternalManagedEnvironment(AbstractPoweredMachineEntity tile) {
				super(tile, Names.EnderIO_MachineTile);
			}

			@Override
			public int priority() {
				return 2;
			}

			@Callback(doc = "function():number; Returns the power use/production per tick")
			public Object[] getPowerPerTick(Context c, Arguments a) {
				return new Object[] { tile.getPowerUsePerTick() };
			}
		}

		public OCDriver() {
			super(AbstractPoweredMachineEntity.class);
		}

		@Override
		public InternalManagedEnvironment createEnvironment(World world, BlockPos pos, Direction side, AbstractPoweredMachineEntity tile) {
			return new InternalManagedEnvironment(tile);
		}
	}

	public static class CCDriver extends CCMultiPeripheral<AbstractPoweredMachineEntity> {

		public CCDriver() {
		}

		public CCDriver(AbstractPoweredMachineEntity tile, World world, BlockPos pos) {
			super(tile, Names.EnderIO_MachineTile, world, pos);
		}

		@Override
		public int peripheralPriority() {
			return 2;
		}

		@Override
		public CCMultiPeripheral getPeripheral(World world, BlockPos pos, Direction side) {
			TileEntity te = world.getTileEntity(pos);
			if(te != null && te instanceof AbstractPoweredMachineEntity) {
				return new CCDriver((AbstractPoweredMachineEntity) te, world, pos);
			}
			return null;
		}

		@Override
		public String[] getMethodNames() {
			return new String[] { "getPowerPerTick" };
		}

		@Override
		public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
			switch(method) {
				case 0: {
					return new Object[] { tile.getPowerUsePerTick() };
				}
			}
			return new Object[] {};
		}
	}

}

