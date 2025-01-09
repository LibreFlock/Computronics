package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import org.libreflock.lib.block.ContainerBase;
import org.libreflock.lib.block.TileEntityBase;

public class ContainerCipherBlock extends ContainerBase {

	public ContainerCipherBlock(TileEntityBase entity,
			InventoryPlayer inventoryPlayer) {
		super(entity, inventoryPlayer);
		for(int i = 0; i < 6; i++) {
			this.addSlotToContainer(new Slot((IInventory)entity, i, 35 + (i * 18), 34));
		}
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
