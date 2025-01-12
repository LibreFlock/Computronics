package org.libreflock.asielib.item;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemMultiple extends Item {

	protected final String mod;
	protected final String[] parts;

	public ItemMultiple(String mod, String[] parts) {
		super();
		this.mod = mod;
		this.parts = parts;
		this.setCreativeTab(ItemGroup.MISC);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	@Override
	public String getTranslationKey() {
		return "item.asielib.unknown";
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if(stack.isEmpty()) {
			return "item.asielib.unknown";
		} else {
			return "item." + this.mod + "." + this.parts[stack.getItemDamage() % parts.length];
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	@SuppressWarnings("unchecked")
	public void getSubItems(ItemGroup tab, NonNullList<ItemStack> list) {
		if(!this.isInCreativeTab(tab)) {
			return;
		}
		for(int i = 0; i < parts.length; i++) {
			list.add(new ItemStack(this, 1, i));
		}
	}
}
