package org.libreflock.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.integration.railcraft.tile.TileTicketMachine;

/**
 * @author Vexatos
 */
public class SelectedSlotWidget extends Widget {

	private TileTicketMachine tile;
	private int slot;
	private boolean maintenanceMode;

	public SelectedSlotWidget(SlotSelectionWidget parent, int slot, TileTicketMachine tile, int x, int y, int u, int v, int w, int h, boolean maintenanceMode) {
		super(x, y, u, v, w, h);
		this.tile = tile;
		this.slot = slot;
		this.maintenanceMode = maintenanceMode;
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		if(this.slot == tile.getSelectedSlot()) {
			gui.drawTexturedModalRect(guiX + x - 3, guiY + y - 3, 184, 0, 22, 22);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public final boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		if(tile.isSelectionLocked()) {
			return false;
		}
		if(maintenanceMode) {
			return false;
		}
		this.tile.setSelectedSlot(this.slot);
		return true;
	}
}
