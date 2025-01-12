package org.libreflock.computronics.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.tape.PortableTapeDrive;
import org.libreflock.asielib.gui.container.ContainerInventory;
import org.libreflock.asielib.util.SlotTyped;

/**
 * @author Vexatos
 */
public class ContainerPortableTapeDrive extends ContainerInventory {

	public ContainerPortableTapeDrive(PortableTapeDrive tapeDrive, PlayerInventory inventoryPlayer) {
		super(tapeDrive.fakeInventory);
		this.addSlotToContainer(new SlotTyped(tapeDrive.fakeInventory, 0, 80, 34, new Object[] { Computronics.itemTape }));
		this.bindPlayerInventory(inventoryPlayer, 8, 84);
	}

	@Override
	public boolean canInteractWith(PlayerEntity p_75145_1_) {
		return true;
	}

}
