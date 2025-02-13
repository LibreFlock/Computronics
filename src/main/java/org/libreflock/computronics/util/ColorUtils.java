package org.libreflock.computronics.util;

import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;

import static org.libreflock.asielib.reference.Capabilities.COLORABLE_CAPABILITY;

/**
 * @author Vexatos
 */
public class ColorUtils extends org.libreflock.asielib.util.ColorUtils {

	@Nullable
	public static IColorable getColorable(@Nullable ICapabilityProvider provider, Direction side) {
		if(Mods.isLoaded(Mods.OpenComputers)) {
			return OCUtils.getColorable(provider, side);
		}
		if(provider != null) {
			// LazyOptional = provider.getCapability(COLORABLE_CAPABILITY, side).equals(LazyOptional.empty());
			return provider.getCapability(COLORABLE_CAPABILITY, side).orElse(null);
		}
		return null;
	}
}
