package org.libreflock.computronics.item;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.oc.manual.IItemWithDocumentation;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tape.PortableDriveManager;
import org.libreflock.computronics.tape.PortableTapeDrive;
import org.libreflock.computronics.tile.TapeDriveState.State;
import org.libreflock.computronics.util.StringUtil;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Vexatos
 */
public class ItemPortableTapeDrive extends Item implements IItemWithDocumentation {

	public ItemPortableTapeDrive() {
		super();
		this.setCreativeTab(Computronics.tab);
		this.setHasSubtypes(false);
		this.setTranslationKey("computronics.portable_tape_drive");
		this.setMaxDamage(0);
		this.setMaxStackSize(1);
		this.setNoRepair();
	}

	@OnlyIn(Dist.CLIENT)
	public static class MeshDefinition implements ItemMeshDefinition {

		private static final ModelResourceLocation[] MODEL_LOCATIONS;
		private static final ModelResourceLocation BASE_MODEL = new ModelResourceLocation(
			Mods.Computronics + ":portable_tape_drive/base", "inventory");

		static {
			MODEL_LOCATIONS = new ModelResourceLocation[State.VALUES.length];
			for(int i = 0; i < State.VALUES.length; i++) {
				MODEL_LOCATIONS[i] = new ModelResourceLocation(Mods.Computronics + ":portable_tape_drive/"
					+ State.VALUES[i].name().toLowerCase(Locale.ENGLISH), "inventory");
			}
		}

		@Override
		public ModelResourceLocation getModelLocation(ItemStack stack) {
			if(stack.hasTagCompound()) {
				CompoundNBT tag = stack.getTagCompound();
				if(tag.contains("state") && tag.contains("inv") && tag.contains("tid")
					&& !new ItemStack(tag.getCompound("inv")).isEmpty()
					&& PortableDriveManager.INSTANCE.exists(tag.getString("tid"), true)) {
					byte state = tag.getByte("state");
					if(state >= 0 && state < MODEL_LOCATIONS.length) {
						return MODEL_LOCATIONS[state];
					}
				}
			}
			return BASE_MODEL;
		}

		public static void registerRenderers() {
			if(Computronics.portableTapeDrive == null) {
				return;
			}
			List<ResourceLocation> models = new ArrayList<ResourceLocation>();
			models.add(new ResourceLocation(Mods.Computronics, "portable_tape_drive/base"));
			for(int i = 0; i < State.VALUES.length; i++) {
				models.add(new ResourceLocation(Mods.Computronics, "portable_tape_drive/"
					+ State.VALUES[i].name().toLowerCase(Locale.ENGLISH)));
			}
			ModelBakery.registerItemVariants(Computronics.portableTapeDrive, models.toArray(new ResourceLocation[models.size()]));
			ModelLoader.setCustomMeshDefinition(Computronics.portableTapeDrive, new MeshDefinition());
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> info, ITooltipFlag flag) {
		if(stack.hasTagCompound()) {
			CompoundNBT tag = stack.getTagCompound();
			if(tag.contains("state") && tag.contains("tid")
				&& PortableDriveManager.INSTANCE.exists(tag.getString("tid"), true)) {
				ItemStack tape = tag.contains("inv") ? new ItemStack(tag.getCompound("inv")) : ItemStack.EMPTY;
				if(!tape.isEmpty()) {
					String label = Computronics.itemTape.getLabel(tape);
					if(label.length() > 0) {
						info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.labeltapeinserted",
							label + TextFormatting.RESET + TextFormatting.GRAY));
					} else {
						info.add(StringUtil.localize("tooltip.computronics.tape.tapeinserted"));
					}
					byte state = tag.getByte("state");
					if(state >= 0 && state < State.VALUES.length) {
						info.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.state",
							StringUtil.localize("tooltip.computronics.tape.state."
								+ State.VALUES[state].name().toLowerCase(Locale.ENGLISH))));
					}
				} else {
					info.add(StringUtil.localize("tooltip.computronics.tape.notapeinserted"));
				}
			}
		}
		super.addInformation(stack, world, info, flag);
	}

	@Override
	public boolean onItemEntityUpdate(ItemEntity entity) {
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(entity.getItem(), entity.world.isRemote);
		drive.resetTime();
		drive.updateCarrier(entity, entity.getItem());
		drive.update();
		return super.onItemEntityUpdate(entity);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity carrier, int slot, boolean isSelected) {
		super.onUpdate(stack, world, carrier, slot, isSelected);
		/*if(!world.isRemote) {
			return;
		}
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.resetTime();
		drive.updateCarrier(carrier, stack);
		drive.update();*/
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		PortableTapeDrive drive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
		drive.updateCarrier(player, stack);
		if(world.isRemote) {
			return super.onItemRightClick(world, player, hand);
		}
		if(player.isSneaking()) {
			player.openGui(Computronics.instance, Computronics.guiPortableTapeDrive.getGuiID(), world, 0, 0, 0);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
		} else {
			drive.switchState(drive.getEnumState() != State.STOPPED ? State.STOPPED : State.PLAYING);
			return new ActionResult<ItemStack>(ActionResultType.SUCCESS, stack);
		}
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return "portable_tape_drive";
	}
}
