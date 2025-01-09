package org.libreflock.computronics.oc.manual;

import net.minecraft.item.ItemStack;

/**
 * @author Vexatos
 */
public interface IItemWithPrefix extends IItemWithDocumentation {

	public String getPrefix(ItemStack stack);
}
