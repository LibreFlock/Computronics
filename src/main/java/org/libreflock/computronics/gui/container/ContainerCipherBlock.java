package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Slot;
import org.libreflock.asielib.gui.container.ContainerBase;
import org.libreflock.asielib.tile.TileEntityBase;

public class ContainerCipherBlock extends ContainerBase {

	public ContainerCipherBlock(TileEntityBase entity,
		PlayerInventory inventoryPlayer) {
		super(entity, inventoryPlayer);
		for(int i = 0; i < 6; i++) {
			this.addSlotToContainer(new Slot((IInventory) entity, i, 35 + (i * 18), 34));
		}
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

}
