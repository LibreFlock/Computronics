package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.libreflock.computronics.Computronics;
import org.libreflock.asielib.gui.container.ContainerBase;
import org.libreflock.asielib.tile.TileEntityBase;
import org.libreflock.asielib.util.SlotTyped;

public class ContainerTapeReader extends ContainerBase {

	public ContainerTapeReader(TileEntityBase entity,
		InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped((IInventory) entity, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
