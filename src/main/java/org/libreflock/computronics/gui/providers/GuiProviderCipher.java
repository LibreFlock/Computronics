package org.libreflock.computronics.gui.providers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.libreflock.computronics.gui.GuiCipherBlock;
import org.libreflock.computronics.gui.container.ContainerCipherBlock;
import org.libreflock.lib.block.ContainerBase;
import org.libreflock.lib.block.TileEntityBase;
import org.libreflock.lib.gui.GuiBase;
import org.libreflock.lib.gui.managed.LegacyGuiProvider;

/**
 * @author Vexatos
 */
public class GuiProviderCipher extends LegacyGuiProvider {
	@Override
	@SideOnly(Side.CLIENT)
	protected GuiBase makeGuiBase(int i, EntityPlayer entityPlayer, World world, int x, int y, int z, TileEntityBase tile) {
		return new GuiCipherBlock(makeContainerBase(guiID, entityPlayer, world, x, y, z, tile));
	}

	@Override
	protected ContainerBase makeContainerBase(int i, EntityPlayer entityPlayer, World world, int x, int y, int z, TileEntityBase tile) {
		return new ContainerCipherBlock(tile, entityPlayer.inventory);
	}
}
