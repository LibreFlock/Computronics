package org.libreflock.computronics.integration.conventional;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.reference.Mods;
import vexatos.conventional.event.PermissionEvent;

/**
 * @author Vexatos
 */
public class IntegrationConventional {

	public static final IntegrationConventional INSTANCE = new IntegrationConventional();

	private IntegrationConventional() {
	}

	public static class Permissions {
		public static final String TapeScroll = "computronics:tapescroll";
	}

	public void init() {
		registerPermission(Permissions.TapeScroll);
	}

	@Optional.Method(modid = Mods.Conventional)
	public void registerPermission(String permission) {
		PermissionEvent.Registry.register(permission);
	}

	@Optional.Method(modid = Mods.Conventional)
	public boolean isDenied(String permission, PlayerEntity player) {
		return MinecraftForge.EVENT_BUS.post(new PermissionEvent(permission, player));
	}
}
