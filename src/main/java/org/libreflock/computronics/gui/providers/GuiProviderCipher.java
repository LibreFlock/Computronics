package org.libreflock.computronics.gui.providers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.libreflock.computronics.gui.GuiCipherBlock;
import org.libreflock.computronics.gui.container.ContainerCipherBlock;
import org.libreflock.asielib.gui.GuiBase;
import org.libreflock.asielib.gui.container.ContainerBase;
import org.libreflock.asielib.gui.managed.LegacyGuiProvider;
import org.libreflock.asielib.tile.TileEntityBase;

/**
 * @author Vexatos
 */
public class GuiProviderCipher extends LegacyGuiProvider {

	@Override
	@SideOnly(Side.CLIENT)
	protected GuiBase makeGuiBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new GuiCipherBlock(makeContainerBase(guiID, entityPlayer, world, pos, tile));
	}

	@Override
	protected ContainerBase makeContainerBase(int guiID, EntityPlayer entityPlayer, World world, BlockPos pos, TileEntityBase tile) {
		return new ContainerCipherBlock(tile, entityPlayer.inventory);
	}
}
