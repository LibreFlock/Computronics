package org.libreflock.computronics.integration.railcraft.item;

import mods.railcraft.common.carts.EntityLocomotive;
import mods.railcraft.common.carts.EntityLocomotiveElectric;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import org.libreflock.computronics.oc.manual.IItemWithPrefix;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.StringUtil;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Vexatos
 */
public class ItemRelaySensor extends Item implements IItemWithPrefix {

	public ItemRelaySensor() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setTranslationKey("computronics.relaySensor");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setFull3D();
		this.setNoRepair();
	}

	@Override
	public ActionResultType onItemUseFirst(PlayerEntity player, World world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, Hand hand) {
		if(player.world.isRemote) {
			return ActionResultType.PASS;
		}
		ItemStack stack = player.getHeldItem(hand);
		TileEntity tile = world.getTileEntity(pos);
		if(player.isSneaking() && tile instanceof TileLocomotiveRelay) {
			if(!stack.hasTagCompound()) {
				stack.putCompound(new CompoundNBT());
			}
			if(stack.hasTagCompound()) {
				CompoundNBT data = stack.getTagCompound();
				data.putInt("relayX", pos.getX());
				data.putInt("relayY", pos.getY());
				data.putInt("relayZ", pos.getZ());
				data.putBoolean("bound", true);
				stack.putCompound(data);
				player.swingArm(hand);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	@Override
	public boolean onLeftClickEntity(ItemStack stack, PlayerEntity player, Entity entity) {
		if(player.isSneaking() && entity != null) {
			if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound") && !player.world.isRemote) {
				CompoundNBT data = stack.getTagCompound();
				final BlockPos pos = new BlockPos(
					data.getInt("relayX"),
					data.getInt("relayY"),
					data.getInt("relayZ")
				);
				if(entity instanceof EntityLocomotiveElectric) {
					if(!player.world.isBlockLoaded(pos)) {
						player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.noRelayDetected"));
						return true;
					}
					TileEntity tile = entity.world.getTileEntity(pos);
					if(tile != null && tile instanceof TileLocomotiveRelay) {
						TileLocomotiveRelay relay = (TileLocomotiveRelay) tile;
						EntityLocomotiveElectric loco = (EntityLocomotiveElectric) entity;
						if(loco.dimension == relay.getWorld().provider.getDimension()) {
							if(loco.getDistanceSq(relay.getBlockPos()) <= Config.LOCOMOTIVE_RELAY_RANGE * Config.LOCOMOTIVE_RELAY_RANGE) {
								relay.setLocomotive(loco);
								player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.bound"));
								player.swingArm(Hand.MAIN_HAND);
								player.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
								ForgeEventFactory.onPlayerDestroyItem(player, stack, Hand.MAIN_HAND);
							} else {
								player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.tooFarAway"));
							}
						} else {
							player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.wrongDim"));
						}
					} else {
						player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.noRelay"));
					}
				} else if(entity instanceof EntityLocomotive) {
					player.sendMessage(new TranslationTextComponent("chat.computronics.sensor.wrongLoco"));
					return true;
				}
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> text, ITooltipFlag flagIn) {
		String descKey;
		if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound")) {
			CompoundNBT data = stack.getTagCompound();
			int x = data.getInt("relayX");
			int y = data.getInt("relayY");
			int z = data.getInt("relayZ");
			text.add(TextFormatting.AQUA + StringUtil.localizeAndFormat("tooltip.computronics.sensor.bound",
				String.valueOf(x), String.valueOf(y), String.valueOf(z)));

			descKey = "tooltip.computronics.sensor.desc2";
		} else {
			descKey = "tooltip.computronics.sensor.desc1";
		}
		String[] local = StringUtil.localize(descKey).split("\n");
		for(String s : local) {
			text.add(TextFormatting.GRAY + s);
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "relay_sensor";
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return "railcraft/";
	}

	@OnlyIn(Dist.CLIENT)
	public static class MeshDefinition implements ItemMeshDefinition {

		private final ModelResourceLocation icon_off = new ModelResourceLocation(Mods.Computronics + ":relay_sensor_off", "inventory");
		private final ModelResourceLocation icon_on = new ModelResourceLocation(Mods.Computronics + ":relay_sensor_on", "inventory");

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if(stack.hasTagCompound() && stack.getTagCompound().getBoolean("bound")) {
				return icon_on;
			}
			return icon_off;
		}
	}
}
