package org.libreflock.computronics.util.internal;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * @author Vexatos
 */
public interface IItemWithColor {

	@OnlyIn(Dist.CLIENT)
	int getColorFromItemstack(ItemStack stack, int tintIndex);

}
