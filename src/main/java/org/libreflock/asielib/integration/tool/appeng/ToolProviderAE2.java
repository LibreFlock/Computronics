package org.libreflock.asielib.integration.tool.appeng;

import appeng.api.implementations.items.IAEWrench;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.libreflock.asielib.api.tool.IToolProvider;

/**
 * @author Vexatos
 */
public class ToolProviderAE2 implements IToolProvider {

	@Override
	public boolean isTool(ItemStack stack, PlayerEntity player, int x, int y, int z) {
		return stack.getItem() instanceof IAEWrench;
	}

	@Override
	public boolean useTool(ItemStack stack, PlayerEntity player, int x, int y, int z) {
		if(stack.getItem() instanceof IAEWrench) {
			IAEWrench wrench = (IAEWrench) stack.getItem();
			if(wrench.canWrench(stack, player, x, y, z)) {
				return true;
			}
		}
		return false;
	}
}
