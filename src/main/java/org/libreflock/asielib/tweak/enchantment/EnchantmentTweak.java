package org.libreflock.asielib.tweak.enchantment;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.libreflock.asielib.AsieLibMod;
import org.libreflock.asielib.util.RayTracer;

/**
 * @author Vexatos
 */
public class EnchantmentTweak {

	public static EnchantmentBetterBane bane;

	public static void registerBaneEnchantment(int enchID) {
		if(!(enchID < 0 || enchID >= 256)) {
			bane = new EnchantmentBetterBane();
			if(Enchantment.getEnchantmentByID(enchID) == null) {
				Enchantment.REGISTRY.register(244, new ResourceLocation("bane_of_arthropods_better"), bane);
				return;
			}
			for(int i = enchID; i < 256; i++) {
				if(Enchantment.getEnchantmentByID(i) == null) {
					AsieLibMod.log.info("Enchantment ID " + enchID + " already occupied, using " + i + " instead");
					Enchantment.REGISTRY.register(i, new ResourceLocation("bane_of_arthropods_better"), bane);
					return;
				}
			}
		}
		throw new IllegalArgumentException("No valid enchantment id! " + EnchantmentBetterBane.class + " Enchantment ID:" + enchID);
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void anvilEvent(AnvilUpdateEvent e) {
		if(e.getLeft().isEmpty() || e.getRight().isEmpty() || e.isCanceled()) {
			return;
		}
		if(e.getLeft().isItemStackDamageable() && e.getLeft().isItemEnchanted()) {
			if(e.getRight().getItem() == Items.FERMENTED_SPIDER_EYE && !hasBaneEnchantment(e.getLeft())) {
				if(e.getRight().getCount() == e.getRight().getMaxStackSize()) {
					e.setOutput(e.getLeft().copy());
					e.setCost(37);
					if(!addBaneEnchantment(e.getOutput(), 9)) {
						e.setOutput(ItemStack.EMPTY);
						if(e.isCancelable()) {
							e.setCanceled(true);
						}
					}
				} else {
					e.setOutput(ItemStack.EMPTY);
					if(e.isCancelable()) {
						e.setCanceled(true);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void enchEvent(TickEvent.PlayerTickEvent e) {
		EntityPlayer player = e.player;
		if(player.world.isRemote) {
			return;
		}
		if(!player.getHeldItemMainhand().isEmpty() && hasBaneEnchantment(player.getHeldItemMainhand())
			&& player.getHeldItemMainhand().isItemStackDamageable()) {

			RayTracer.instance().fire(player, 10.0);
			RayTraceResult target = RayTracer.instance().getTarget();
			if(target != null && target.typeOfHit == RayTraceResult.Type.ENTITY) {
				Entity entity = target.entityHit;
				if(entity != null
					&& entity instanceof EntityLivingBase
					&& ((EntityLivingBase) entity).getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD
					&& entity.hurtResistantTime <= 10
					&& !player.isActiveItemStackBlocking()) {
					player.attackTargetEntityWithCurrentItem(entity);
					if(player.getHeldItemMainhand().isItemStackDamageable()) {
						float distance = player.getDistance(entity);
						int damage = Math.max(Math.min((int) distance + 1, 10), 1);
						player.getHeldItemMainhand().damageItem(damage, player);
					}
				}
			}
		}
	}

	private static boolean hasBaneEnchantment(ItemStack stack) {
		if(stack.getTagCompound() == null || stack.getTagCompound().isEmpty()) {
			return false;
		}

		if(!stack.getTagCompound().contains("ench", 9)) {
			return false;
		}

		ListNBT list = stack.getTagCompound().getTagList("ench", 10);
		for(int i = 0; i < list.tagCount(); i++) {
			CompoundNBT tag = list.getCompoundAt(i);
			if(tag.getShort("id") == Enchantment.getEnchantmentID(bane)) {
				return true;
			}
		}
		return false;
	}

	private static boolean addBaneEnchantment(ItemStack stack, int level) {
		if(stack.getTagCompound() == null) {
			stack.putCompound(new CompoundNBT());
		}

		if(!stack.getTagCompound().contains("ench", 9)) {
			stack.getTagCompound().put("ench", new ListNBT());
		}

		ListNBT list = stack.getTagCompound().getTagList("ench", 10);
		for(int i = 0; i < list.tagCount(); i++) {
			CompoundNBT tag = list.getCompoundAt(i);
			if(tag.getShort("id") == Enchantment.getEnchantmentID(Enchantments.BANE_OF_ARTHROPODS) && tag.getShort("lvl") == (short) 5) {
				list.removeTag(i);
				CompoundNBT data = new CompoundNBT();
				data.putShort("id", (short) Enchantment.getEnchantmentID(bane));
				data.putShort("lvl", (short) ((byte) level));
				list.appendTag(data);
				return true;
			}
		}
		return false;
	}
}
