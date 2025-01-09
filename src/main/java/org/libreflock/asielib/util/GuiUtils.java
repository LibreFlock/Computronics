package org.libreflock.asielib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.libreflock.asielib.gui.GuiBase;
import org.libreflock.asielib.gui.container.ContainerBase;
import org.libreflock.asielib.tile.TileEntityBase;

public class GuiUtils {

	@SideOnly(Side.CLIENT)
	public static TileEntityBase currentTileEntity() {
		GuiScreen gc = Minecraft.getMinecraft().currentScreen;
		if(gc instanceof GuiBase) {
			Container container = ((GuiBase) gc).container;
			if(container instanceof ContainerBase) {
				return ((ContainerBase) container).getEntity();
			}
		}
		return null;
	}

	@SideOnly(Side.CLIENT)
	public static GuiBase currentGui() {
		GuiScreen gc = Minecraft.getMinecraft().currentScreen;
		if(gc instanceof GuiBase) return ((GuiBase)gc);
		return null;
	}
}
