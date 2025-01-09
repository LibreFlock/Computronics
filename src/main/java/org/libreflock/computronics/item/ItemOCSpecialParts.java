package org.libreflock.computronics.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.oc.driver.DriverMagicalMemory;
import org.libreflock.computronics.oc.manual.IItemWithDocumentation;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.lib.item.ItemMultiple;

import java.util.List;

/**
 * @author Vexatos
 */
public class ItemOCSpecialParts extends ItemMultiple implements IItemWithDocumentation, EnvironmentProvider {

	public ItemOCSpecialParts() {
		super(Mods.Computronics, new String[] {
			"magical_memory"
		});
		this.setCreativeTab(Computronics.tab);
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return "magical_memory";
			default:
				return "index";
		}
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return EnumRarity.epic;
			default:
				return OCUtils.getRarityByTier(stack);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
		OCUtils.addTooltip(stack, tooltip, advanced);
	}

	@Override
	public Class<?> getEnvironment(ItemStack stack) {
		if(!stack.getItem().equals(this)) {
			return null;
		}
		switch(stack.getItemDamage()) {
			case 0:
				return DriverMagicalMemory.class;
			default:
				return null;
		}
	}
}
