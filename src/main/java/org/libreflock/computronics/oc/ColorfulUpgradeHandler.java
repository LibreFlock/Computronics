package org.libreflock.computronics.oc;

import li.cil.oc.api.event.RobotAnalyzeEvent;
import li.cil.oc.api.event.RobotRenderEvent;
import li.cil.oc.api.internal.Agent;
import li.cil.oc.api.internal.Robot;
import li.cil.oc.api.network.Node;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.item.ItemOpenComputers;
import org.libreflock.computronics.oc.driver.RobotUpgradeColorful;
import org.libreflock.computronics.reference.Mods;

import java.util.Locale;

/**
 * @author Vexatos
 */
public class ColorfulUpgradeHandler {

	@SubscribeEvent
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotAnalyze(RobotAnalyzeEvent e) {
		int color = getColor(e.agent);
		if(color < 0) {
			return;
		}
		e.player.sendStatusMessage(new TextComponentTranslation("chat.computronics.colorful_upgrade.color", "0x"
			+ String.format("%06x", color).toUpperCase(Locale.ENGLISH)), false);
	}

	@OnlyIn(Dist.CLIENT)
	@SubscribeEvent(priority = EventPriority.LOW)
	@Optional.Method(modid = Mods.OpenComputers)
	public void onRobotRender(RobotRenderEvent e) {
		int color = -1;
		if(e.agent instanceof Robot) {
			Robot robot = ((Robot) e.agent);
			for(int i = 0; i < robot.getSizeInventory(); ++i) {
				//Environment component = robot.getComponentInSlot(i);
				//if(component instanceof RobotUpgradeColorful) {
				//	color = ((RobotUpgradeColorful) component).getColor();
				//	break;
				//}
				ItemStack stack = robot.getStackInSlot(i);
				if(!stack.isEmpty() && stack.getItem() instanceof ItemOpenComputers && stack.getItemDamage() == 7) {
					CompoundNBT tag = ((ItemOpenComputers) stack.getItem()).dataTag(stack);
					if(tag.contains("computronics:color")) {
						int newcol = tag.getInt("computronics:color");
						if(newcol > color) {
							color = newcol;
						}
					}
				}
			}
		}
		if(color < 0) {
			return;
		}
		color = color & 0xFFFFFF;
		GlStateManager.color(((color >> 16) & 0xFF) / 255f, ((color >> 8) & 0xFF) / 255f, (color & 0xFF) / 255f);
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private int getColor(Agent agent) {
		try {
			for(Node node : agent.machine().node().reachableNodes()) {
				if(node != null && node.host() instanceof RobotUpgradeColorful) {
					return ((RobotUpgradeColorful) node.host()).getColor();
				}
			}
		} catch(NullPointerException e) {
			return -1;
		}
		return -1;
	}
}
