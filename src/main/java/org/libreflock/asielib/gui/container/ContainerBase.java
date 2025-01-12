package org.libreflock.asielib.gui.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import org.libreflock.asielib.tile.TileEntityBase;

public abstract class ContainerBase extends ContainerInventory {

	private final TileEntityBase entity;

	public ContainerBase(TileEntityBase entity, PlayerInventory inventoryPlayer) {
		super(entity instanceof IInventory ? ((IInventory) entity) : null);
		this.entity = entity;

		entity.openInventory();
	}

	public TileEntityBase getEntity() {
		return entity;
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return this.entity.isUsableByPlayer(player);
	}

	@Override
	public void onContainerClosed(PlayerEntity par1EntityPlayer) {
		super.onContainerClosed(par1EntityPlayer);
		this.entity.closeInventory();
	}
}
