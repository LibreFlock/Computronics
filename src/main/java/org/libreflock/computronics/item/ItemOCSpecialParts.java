package org.libreflock.computronics.item;

import li.cil.oc.api.driver.EnvironmentProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Rarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.oc.driver.DriverMagicalMemory;
import org.libreflock.computronics.oc.manual.IItemWithDocumentation;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public class ItemOCSpecialParts extends ItemMultipleComputronics implements IItemWithDocumentation, EnvironmentProvider {

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
	public Rarity getRarity(ItemStack stack) {
		switch(stack.getItemDamage()) {
			case 0:
				return Rarity.EPIC;
			default:
				return OCUtils.getRarityByTier(stack);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		OCUtils.addTooltip(stack, tooltip, flag);
	}

	@Nullable
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

	@Override
	public void registerItemModels() {
		if(!Computronics.proxy.isClient()) {
			return;
		}
		if(Config.OC_MAGICAL_MEMORY) {
			registerItemModel(0);
		}
	}
}
