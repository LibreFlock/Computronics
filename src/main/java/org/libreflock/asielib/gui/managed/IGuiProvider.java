package org.libreflock.asielib.gui.managed;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public interface IGuiProvider {

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen makeGui(int ID, PlayerEntity player, World world, int x, int y, int z);

	@Nullable
	public Container makeContainer(int ID, PlayerEntity player, World world, int x, int y, int z);

	public boolean canOpen(World world, int x, int y, int z, PlayerEntity player, Direction side);

	public void setGuiID(int guiID);

	public int getGuiID();
}
