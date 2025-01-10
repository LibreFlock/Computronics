package org.libreflock.computronics.util.internal;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public interface IBlockWithColor {

	@OnlyIn(Dist.CLIENT)
	int colorMultiplier(BlockState state, @Nullable IWorldReader worldIn, @Nullable BlockPos pos, int tintIndex);
}
