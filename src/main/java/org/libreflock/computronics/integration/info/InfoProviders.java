package org.libreflock.computronics.integration.info;

import org.libreflock.computronics.block.BlockColorfulLamp;
import org.libreflock.computronics.block.BlockPeripheral;
import org.libreflock.computronics.block.BlockTapeReader;
import org.libreflock.computronics.integration.info.providers.IComputronicsInfoProvider;
import org.libreflock.computronics.integration.info.providers.InfoColorfulLamp;
import org.libreflock.computronics.integration.info.providers.InfoLocomotiveRelay;
import org.libreflock.computronics.integration.info.providers.InfoPeripheral;
import org.libreflock.computronics.integration.info.providers.InfoTapeDrive;
import org.libreflock.computronics.integration.railcraft.block.BlockDigitalSignalBox;
import org.libreflock.computronics.integration.railcraft.block.BlockLocomotiveRelay;
import org.libreflock.computronics.reference.Mods;

import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * @author Vexatos
 */
public class InfoProviders {

	public static final ArrayList<InfoProviders> VALUES = new ArrayList<InfoProviders>();
	private IComputronicsInfoProvider provider;
	private Class<?> block;

	private static boolean init = false;

	static void initialize() {
		if(init) {
			return;
		}
		init = true;
		newProvider(new InfoPeripheral(), BlockPeripheral.class);
		if(Mods.isLoaded(Mods.Railcraft)) {
			newProvider(new InfoPeripheral(), BlockDigitalSignalBox.class);
			newProvider(new InfoLocomotiveRelay(), BlockLocomotiveRelay.class);
		}
		newProvider(new InfoTapeDrive(), BlockTapeReader.class);
		newProvider(new InfoColorfulLamp(), BlockColorfulLamp.class);
	}

	private static void newProvider(IComputronicsInfoProvider provider, Class<?> block) {
		new InfoProviders(provider, block);
	}

	private InfoProviders(IComputronicsInfoProvider provider, Class<?> block) {
		this.provider = provider;
		this.block = block;
		VALUES.add(this);
	}

	public IComputronicsInfoProvider getProvider() {
		return this.provider;
	}

	public boolean isInstance(@Nullable Object obj) {
		return obj != null && block.isInstance(obj);
	}
}
