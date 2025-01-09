package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import org.libreflock.computronics.Computronics;
import org.libreflock.lib.block.ContainerBase;
import org.libreflock.lib.block.TileEntityBase;
import org.libreflock.lib.util.SlotTyped;

public class ContainerTapeReader extends ContainerBase {

	public ContainerTapeReader(TileEntityBase entity,
		InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		this.addSlotToContainer(new SlotTyped((IInventory) entity, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
