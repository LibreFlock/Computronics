package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.tape.PortableTapeDrive;
import org.libreflock.lib.block.ContainerInventory;
import org.libreflock.lib.util.SlotTyped;

/**
 * @author Vexatos
 */
public class ContainerPortableTapeDrive extends ContainerInventory {

	public ContainerPortableTapeDrive(PortableTapeDrive tapeDrive, InventoryPlayer inventoryPlayer) {
		super(tapeDrive.fakeInventory);
		this.addSlotToContainer(new SlotTyped(tapeDrive.fakeInventory, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

	@Override
	public boolean canInteractWith(EntityPlayer p_75145_1_) {
		return true;
	}

}
