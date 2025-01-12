package org.libreflock.computronics.integration.buildcraft.pluggable;

import buildcraft.api.transport.IPipe;
import buildcraft.api.transport.pluggable.IPipePluggableItem;
import buildcraft.api.transport.pluggable.PipePluggable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.oc.manual.IItemWithPrefix;

/**
 * @author Vexatos
 */
public class ItemDroneStation extends Item implements IPipePluggableItem, IItemWithPrefix {

	public ItemDroneStation() {
		super();
		setCreativeTab(Computronics.tab);
		setUnlocalizedName("computronics.dockingStation");
	}

	@Override
	public boolean doesSneakBypassUse(World world, int x, int y, int z, PlayerEntity player) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister) {
		// NOOP
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getSpriteNumber() {
		return 0;
	}

	@Override
	public PipePluggable createPipePluggable(IPipe pipe, ForgeDirection side, ItemStack stack) {
		switch(side) {
			case UP:
				return new DroneStationPluggable();
			default:
				return null;
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "drone_station";
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return "buildcraft/";
	}
}
