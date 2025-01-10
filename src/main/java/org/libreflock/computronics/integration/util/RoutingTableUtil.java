package org.libreflock.computronics.integration.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

/**
 * @author Vexatos
 */
public class RoutingTableUtil {
	public static String getRoutingTableTitle(ItemStack stack) {
		if(stack.hasTagCompound()) {
			CompoundNBT nbt = stack.getTagCompound();
			String title = nbt.getString("title");
			if(title != null) {
				return title;
			}
		}
		return "";
	}

	public static boolean setRoutingTableTitle(ItemStack stack, String title) {
		if(!stack.hasTagCompound()) {
			stack.putCompound(new CompoundNBT());
		}
		if(stack.hasTagCompound()) {
			CompoundNBT nbt = stack.getTagCompound();
			if(title != null && !title.isEmpty()) {
				nbt.putString("title", title);
				stack.putCompound(nbt);
				return true;
			}
		}
		return false;
	}
}
