package org.libreflock.computronics.oc.driver;

import li.cil.oc.api.driver.DriverItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import org.libreflock.computronics.oc.IntegrationOpenComputers;
import org.libreflock.computronics.util.OCUtils;

/**
 * @author Vexatos
 */
public abstract class DriverOCSpecialPart implements DriverItem {

	private final int metadata;

	protected DriverOCSpecialPart(int metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean worksWith(ItemStack stack) {
		return stack.getItem().equals(IntegrationOpenComputers.itemOCSpecialParts) && stack.getItemDamage() == metadata;
	}

	@Override
	public CompoundNBT dataTag(ItemStack stack) {
		return OCUtils.dataTag(stack);
	}
}
