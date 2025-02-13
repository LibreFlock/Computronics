package org.libreflock.computronics.integration.railcraft.gui;

import mods.railcraft.common.plugins.forge.PlayerPlugin;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.integration.railcraft.gui.container.ContainerTicketMachine;
import org.libreflock.computronics.integration.railcraft.tile.TileTicketMachine;
import org.libreflock.asielib.gui.managed.GuiProviderBase;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class GuiProviderTicketMachine extends GuiProviderBase {

	@Override
	public boolean canOpen(World world, int x, int y, int z, PlayerEntity player, Direction side) {
		if(!super.canOpen(world, x, y, z, player, side)) {
			return false;
		}
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			TileTicketMachine machine = ((TileTicketMachine) tile);
			boolean triesMaintenanceWork = player.isSneaking() && player.getHeldItemMainhand().isEmpty();
			return !triesMaintenanceWork || (!machine.isLocked()
				|| machine.isOwner(player.getGameProfile())
				|| PlayerPlugin.isOwnerOrOp(machine.getOwner(), player.getGameProfile()));
		}
		return false;
	}

	@Nullable
	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen makeGui(int guiID, PlayerEntity player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			return new GuiTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getHeldItemMainhand().isEmpty());
		}
		return null;
	}

	@Nullable
	@Override
	public Container makeContainer(int guiID, PlayerEntity player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
		if(tile instanceof TileTicketMachine) {
			return new ContainerTicketMachine(player.inventory, (TileTicketMachine) tile, player.isSneaking() && player.getHeldItemMainhand().isEmpty());
		}
		return null;
	}
}
