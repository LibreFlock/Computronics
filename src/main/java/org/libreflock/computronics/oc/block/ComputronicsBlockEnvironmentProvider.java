package org.libreflock.computronics.oc.block;

import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public class ComputronicsBlockEnvironmentProvider implements EnvironmentProvider {

	@Override
	public Class<?> getEnvironment(ItemStack stack) {
		if(stack.isEmpty() || !(stack.getItem() instanceof BlockItem)
			|| !(((BlockItem) stack.getItem()).getBlock() instanceof IComputronicsEnvironmentBlock)) {
			return null;
		}

		return ((IComputronicsEnvironmentBlock) ((BlockItem) stack.getItem()).getBlock())
			.getTileEntityClass(stack.getItemDamage());
	}
}
