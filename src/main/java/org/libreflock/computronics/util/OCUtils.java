package org.libreflock.computronics.util;

import li.cil.oc.api.Driver;
import li.cil.oc.api.driver.DeviceInfo.DeviceAttribute;
import li.cil.oc.api.driver.DriverItem;
import li.cil.oc.api.internal.Colored;
import li.cil.oc.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Rarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.asielib.util.ColorUtils;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.libreflock.asielib.reference.Capabilities.COLORABLE_CAPABILITY;

/**
 * @author Vexatos
 */
public class OCUtils {

	public static CompoundNBT dataTag(final ItemStack stack) {
		if(!stack.hasTag()) {
			stack.setTag(new CompoundNBT());
		}
		final CompoundNBT nbt = stack.getTag();
		// This is the suggested key under which to store item component data.
		// You are free to change this as you please.
		if(!nbt.contains("oc:data")) {
			nbt.put("oc:data", new CompoundNBT());
		}
		return nbt.getCompound("oc:data");
	}

	public static class Device {

		private final String Class;
		private final String Description;
		private final String Vendor;
		private final String Product;
		private final String[] other;

		public Device(String Class, String Description, String Vendor, String Product, String... other) {
			this.Class = Class;
			this.Description = Description;
			this.Vendor = Vendor;
			this.Product = Product;
			this.other = other;
		}

		public Map<String, String> deviceInfo() {
			Map<String, String> deviceInfo = new HashMap<String, String>();
			deviceInfo.put(DeviceAttribute.Class, Class);
			deviceInfo.put(DeviceAttribute.Description, Description);
			deviceInfo.put(DeviceAttribute.Vendor, Vendor);
			deviceInfo.put(DeviceAttribute.Product, Product);
			for(int i = 0; i + 1 < other.length; i += 2) {
				deviceInfo.put(other[i], other[i + 1]);
			}
			return deviceInfo;
		}
	}

	public static final class Vendors {

		public static final String
			ACME = "ACME Co.",
			BuildCraft = "BuildCraft, Inc.",
			DFKI = "DFKI GmbH",
			Hosencorp = "Hosencorp AG",
			HuggingCreeper = "Hugging Creeper Industries",
			Lumiose = "Lumiose Lighting",
			NSA = "National Security Agency",
			Railcraft = "Railcraft, Inc.",
			Siekierka = "Siekierka Innovations",
			Soluna = "Soluna Technologies",
			Trumbour = "Trumbour Technology",
			Yanaki = "Yanaki Sound Systems";

		private Vendors() {
		}
	}

	private static final int maxWidth = 220;

	//Mostly stolen from Sangar
	@OnlyIn(Dist.CLIENT)
	public static void addTooltip(ItemStack stack, List<String> tooltip, ITooltipFlag flag) {
		{
			FontRenderer font = Minecraft.getInstance().font;
			final String key = stack.getItem().getDescriptionId() + ".tip";
			String tip = StringUtil.localize(key);
			if(!tip.equals(key)) {
				String[] lines = tip.split("\n");
				if(font == null) {
					Collections.addAll(tooltip, lines);
				} else {
					boolean shouldShorten = (font.width(tip) > maxWidth) && !KeyBindings.showExtendedTooltips();
					if(shouldShorten) {
						tooltip.add(StringUtil.localizeAndFormat("oc:tooltip.toolong",
							KeyBindings.getKeyBindingName(KeyBindings.extendedTooltip())));
					} else {
						for(String line : lines) {
							List<String> list = font.listFormattedStringToWidth(line, maxWidth);
							tooltip.addAll(list);
						}
					}
				}
			}
		}
		if(stack.hasTag() && stack.getTag().contains("oc:data")) {
			CompoundNBT data = stack.getTag().getCompound("oc:data");
			if(data.contains("node") && data.getCompound("node").contains("address")) {
				tooltip.add(TextFormatting.DARK_GRAY
					+ data.getCompound("node").getString("address").substring(0, 13) + "..."
					+ TextFormatting.GRAY);
			}
		}
		if(flag.isAdvanced()) {
			DriverItem item = Driver.driverFor(stack);
			tooltip.add(StringUtil.localizeAndFormat("oc:tooltip.tier", item != null ? item.tier(stack) + 1 : 0));
		}
	}

	private static final Rarity[] rarities = new Rarity[] { Rarity.COMMON, Rarity.UNCOMMON, Rarity.RARE, Rarity.EPIC };

	public static Rarity getRarityByTier(ItemStack stack) {
		DriverItem item = Driver.driverFor(stack);
		int tier = item != null ? Math.min(Math.max(item.tier(stack), 0), rarities.length - 1) : 0;
		return rarities[tier];
	}

	public static Rarity getRarityByTier(int tier) {
		return rarities[Math.min(Math.max(tier, 0), rarities.length - 1)];
	}

	@CapabilityInject(Colored.class)
	public static Capability<Colored> COLORED_CAPABILITY;

	@Nullable
	public static IColorable getColorable(@Nullable ICapabilityProvider provider, Direction side) {
		if(provider != null && provider.getCapability(COLORABLE_CAPABILITY, side).equals(LazyOptional.empty())) {
			return provider.getCapability(COLORABLE_CAPABILITY, side).orElse(null);
		}
		if(provider != null && provider.getCapability(COLORED_CAPABILITY, side).equals(LazyOptional.empty())) {
			return new ConvertedColorable(provider.getCapability(COLORED_CAPABILITY, side).orElse(null));
		}
		return null;
	}

	/**
	 * @author Vexatos
	 */
	public static class ConvertedColorable implements IColorable {

		private final Colored colored;

		public ConvertedColorable(Colored colored) {
			this.colored = colored;
		}

		@Override
		public boolean canBeColored() {
			return colored.controlsConnectivity();
		}

		@Override
		public int getColor() {
			return colored.getColor();
		}

		@Override
		public int getDefaultColor() {
			return ColorUtils.Color.LightGray.color;
		}

		@Override
		public void setColor(int color) {
			colored.setColor(color);
		}
	}
}
