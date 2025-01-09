package org.libreflock.computronics.tape;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import org.libreflock.computronics.tile.TileTapeDrive;

/**
 * @author Vexatos
 */
public class TapeStorageEventHandler {
	@SubscribeEvent
	public void handleTapeStorageSaving(WorldEvent.Unload event) {
		if(event.world.isRemote) {
			return;
		}

		for(Object tile : event.world.loadedTileEntityList) {
			if(tile instanceof TileTapeDrive && !((TileTapeDrive) tile).isInvalid()) {
				((TileTapeDrive) tile).saveStorage();
			}
		}
	}
}
