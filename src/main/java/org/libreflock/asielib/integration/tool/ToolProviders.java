package org.libreflock.asielib.integration.tool;

//import org.libreflock.asielib.integration.tool.appeng.ToolProviderAE2;
//import org.libreflock.asielib.integration.tool.cofh.ToolProviderCoFH;

import org.libreflock.asielib.integration.tool.enderio.ToolProviderEnderIO;
import org.libreflock.asielib.integration.tool.oc.ToolProviderOC;
import org.libreflock.asielib.reference.Mods;

import static org.libreflock.asielib.integration.Integration.registerToolProvider;

//import org.libreflock.asielib.integration.tool.mekanism.ToolProviderMekanism

/**
 * @author Vexatos
 */
public class ToolProviders {

	public static void registerToolProviders() {
		if(Mods.hasVersion(Mods.API.OpenComputersInternal, "[5.1.1,)")) {
			registerToolProvider(new ToolProviderOC());
		}
		/*if(Mods.API.hasAPI(Mods.API.BuildCraftTools)) {
			registerToolProvider(new ToolProviderBuildCraft());
		}*/
		if(Mods.API.hasAPI(Mods.API.EnderIOTools)) {
			registerToolProvider(new ToolProviderEnderIO());
		}
		/*if(Mods.API.hasAPI(Mods.API.CoFHItems)) {
			registerToolProvider(new ToolProviderCoFH());
		}*/
		/*if(Mods.isLoaded(Mods.AE2)) {
			registerToolProvider(new ToolProviderAE2());
		}
		if(Mods.isLoaded(Mods.Mekanism)) {
			registerToolProvider(new ToolProviderMekanism());
		}*/
	}
}
