package org.libreflock.computronics.integration.info;

import mcp.mobius.waila.api.IWailaRegistrar;
import org.libreflock.computronics.block.BlockPeripheral;
import org.libreflock.computronics.integration.railcraft.block.BlockDigitalSignalBox;
import org.libreflock.computronics.reference.Mods;

public class IntegrationWaila {

	public static void register(IWailaRegistrar reg) {
		InfoProviders.initialize();

		InfoComputronics provider = new InfoComputronics();
		reg.registerBodyProvider(provider, BlockPeripheral.class);
		reg.registerNBTProvider(provider, BlockPeripheral.class);

		if(Mods.isLoaded(Mods.Railcraft)) {
			reg.registerBodyProvider(provider, BlockDigitalSignalBox.class);
			reg.registerNBTProvider(provider, BlockDigitalSignalBox.class);
		}

		ConfigValues.registerConfigs(reg);
	}

}
