package org.libreflock.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileCamera;
import org.libreflock.asielib.tile.TileEntityBase;

public class BlockCamera extends BlockPeripheral {

	public BlockCamera() {
		super("camera", Rotation.SIX);
		this.setTranslationKey("computronics.camera");
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileCamera();
	}

	@Override
	public boolean emitsRedstone(IBlockAccess world, BlockPos pos, Direction side) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(BlockState state) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(null);
		}
		return super.getComparatorInputOverride(state, world, pos);
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileCamera.class;
	}
}
