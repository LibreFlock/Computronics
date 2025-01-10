package org.libreflock.asielib.util.color;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public class ItemColorizer {

	/**
     * Return whether the specified armor ItemStack has a color.
	 */
	public static boolean hasColor(ItemStack stack) {
		return (stack.hasTagCompound() && (stack.getTagCompound().contains("display") && stack.getTagCompound().getCompound("display").contains("color")));
	}

	/**
	 * Return the color for the specified armor ItemStack.
	 */
	public static int getColor(ItemStack stack) {
		CompoundNBT stackCompound = stack.getTagCompound();

        if(stackCompound != null) {
            CompoundNBT displayCompound = stackCompound.getCompound("display");
            return displayCompound.contains("color") ? displayCompound.getInt("color") : -1;
        } else {
            return -1;
        }
	}

	public static void removeColor(ItemStack par1ItemStack) {
		CompoundNBT stackCompound = par1ItemStack.getTagCompound();

		if(stackCompound != null) {
			CompoundNBT displayCompound = stackCompound.getCompound("display");
            if(displayCompound.contains("color")) {
                displayCompound.removeTag("color");
            }
		}
	}

	public static void setColor(ItemStack par1ItemStack, int par2) {
		CompoundNBT nbttagcompound = par1ItemStack.getTagCompound();

		if(nbttagcompound == null) {
			nbttagcompound = new CompoundNBT();
			par1ItemStack.putCompound(nbttagcompound);
		}

		CompoundNBT nbttagcompound1 = nbttagcompound.getCompound("display");

		if(!nbttagcompound.contains("display")) {
			nbttagcompound.put("display", nbttagcompound1);
		}

		nbttagcompound1.putInt("color", par2);
	}
}
