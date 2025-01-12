package org.libreflock.asielib.gui.managed;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

/**
 * @author Vexatos
 */
public abstract class GuiProviderBase implements IGuiProvider {

	protected int guiID;

	@Override
	public void setGuiID(int guiID) {
		this.guiID = guiID;
	}

	@Override
	public int getGuiID() {
		return this.guiID;
	}

	@Override
	public boolean canOpen(World world, int x, int y, int z, PlayerEntity player, Direction side) {
		return true;
	}
}
