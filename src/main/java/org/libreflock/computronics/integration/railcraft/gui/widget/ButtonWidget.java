package org.libreflock.computronics.integration.railcraft.gui.widget;

import mods.railcraft.client.gui.GuiContainerRailcraft;
import mods.railcraft.common.gui.tooltips.ToolTip;
import mods.railcraft.common.gui.widgets.Widget;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ButtonWidget extends Widget {

	protected boolean pressed;
	protected int buttonPressed;

	public ButtonWidget(int x, int y, int u, int v, int w, int h) {
		super(x, y, u, v, w, h);
	}

	@Override
	public void draw(GuiContainerRailcraft gui, int guiX, int guiY, int mouseX, int mouseY) {
		int vv = this.pressed ? this.v + this.h : this.v;
		gui.drawTexturedModalRect(guiX + this.x, guiY + this.y, this.u, vv, this.w, this.h);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
		this.pressed = true;
		this.buttonPressed = mouseButton;
		this.onPress(this.buttonPressed);
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleMouseRelease(int mouseX, int mouseY, int eventType) {
		if(this.pressed) {
			this.pressed = false;
			this.onRelease(this.buttonPressed);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public void handleMouseMove(int mouseX, int mouseY, int mouseButton, long time) {
		if(this.pressed && !this.isMouseOver(mouseX, mouseY)) {
			this.pressed = false;
			this.onRelease(this.buttonPressed);
		}

	}

	@OnlyIn(Dist.CLIENT)
	public void onPress(int mouseButton) {
	}

	@OnlyIn(Dist.CLIENT)
	public void onRelease(int mouseButton) {
	}

	private ToolTip toolTip;

	@Override
	public ToolTip getToolTip() {
		return this.toolTip;
	}

	@OnlyIn(Dist.CLIENT)
	public void setToolTip(ToolTip toolTip) {
		this.toolTip = toolTip;
	}
}
