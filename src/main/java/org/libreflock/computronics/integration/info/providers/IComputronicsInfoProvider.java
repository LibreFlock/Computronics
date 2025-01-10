package org.libreflock.computronics.integration.info.providers;

import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.reference.Mods;

/**
 * @author Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "mcp.mobius.waila.api.IWailaDataProvider", modid = Mods.Waila),
	@Optional.Interface(iface = "mcjty.theoneprobe.api.IProbeInfoProvider", modid = Mods.TheOneProbe)
})
public interface IComputronicsInfoProvider extends IWailaDataProvider, IProbeInfoProvider {

}
