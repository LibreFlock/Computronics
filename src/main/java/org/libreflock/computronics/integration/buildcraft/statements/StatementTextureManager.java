package org.libreflock.computronics.integration.buildcraft.statements;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.libreflock.computronics.integration.buildcraft.statements.actions.Actions;
import org.libreflock.computronics.integration.buildcraft.statements.triggers.Triggers;

/**
 * @author Vexatos
 */
public class StatementTextureManager {

	@SubscribeEvent
	public void stitchTextures(TextureStitchEvent.Pre event) {
		for(Triggers trigger : Triggers.VALUES) {
			trigger.stitchTextures(event);
		}
		for(Actions action : Actions.VALUES) {
			action.stitchTextures(event);
		}
	}

}
