package org.libreflock.computronics.integration.info.providers;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.tape.IItemTapeStorage;
import org.libreflock.computronics.integration.info.ConfigValues;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TapeDriveState;
import org.libreflock.computronics.tile.TileTapeDrive;
import org.libreflock.computronics.util.StringUtil;

import java.util.List;
import java.util.Locale;

public class InfoTapeDrive extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip,
		IWailaDataAccessor accessor, IWailaConfigHandler config) {

		if(!ConfigValues.Tape.getValue(config)) {
			return currenttip;
		}

		CompoundNBT data = accessor.getNBTData();
		ItemStack is = new ItemStack(data.getTagList("Inventory", 10).getCompoundAt(0));
		if(!is.isEmpty() && is.getItem() instanceof IItemTapeStorage) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0 && ConfigValues.TapeName.getValue(config)) {
				currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.labeltapeinserted",
					label + TextFormatting.RESET));
			} else {
				currenttip.add(StringUtil.localize("tooltip.computronics.tape.tapeinserted"));
			}
			if(ConfigValues.DriveState.getValue(config)) {
				currenttip.add(StringUtil.localizeAndFormat("tooltip.computronics.tape.state",
					StringUtil.localize("tooltip.computronics.tape.state."
						+ TapeDriveState.State.VALUES[data.getByte("state")].name().toLowerCase(Locale.ENGLISH))));
			}
		} else {
			currenttip.add(StringUtil.localize("tooltip.computronics.tape.notapeinserted"));
		}
		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public CompoundNBT getNBTData(ServerPlayerEntity player, TileEntity te, CompoundNBT tag, World world, BlockPos pos) {
		if(te instanceof TileTapeDrive) {
			TileTapeDrive drive = (TileTapeDrive) te;
			CompoundNBT data = new CompoundNBT();
			//I have to do this, for the inventory
			drive.writeToNBT(data);
			tag.putByte("state", data.getByte("state"));
			tag.put("Inventory", data.getTagList("Inventory", 10));
		}
		return tag;
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public String getUID() {
		return "tape_drive";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
		TileEntity tileEntity = world.getTileEntity(data.getBlockPos());
		if(!(tileEntity instanceof TileTapeDrive)) {
			return;
		}
		TileTapeDrive tile = (TileTapeDrive) tileEntity;
		ItemStack is = tile.getStackInSlot(0);
		if(!is.isEmpty() && is.getItem() instanceof IItemTapeStorage) {
			String label = Computronics.itemTape.getLabel(is);
			if(label.length() > 0) {
				probeInfo.text(StringUtil.localizeAndFormat("tooltip.computronics.waila.tape.labeltapeinserted",
					label + TextFormatting.RESET));
			} else {
				probeInfo.text(StringUtil.localize("tooltip.computronics.waila.tape.tapeinserted"));
			}
			probeInfo.text(StringUtil.localizeAndFormat("tooltip.computronics.waila.tape.state",
				StringUtil.localize("tooltip.computronics.waila.tape.state."
					+ tile.getEnumState().name().toLowerCase(Locale.ENGLISH))));
		} else {
			probeInfo.text(StringUtil.localize("tooltip.computronics.waila.tape.notapeinserted"));
		}
	}

	/*@Override
	public void decorateBlock(ItemStack itemStack, IWailaDataAccessor accessor, IWailaConfigHandler config) {
		BlockTapeReader block = (BlockTapeReader) accessor.getBlock();
		int front = block.getFrontSide(accessor.getMetadata());
		String text = "ERROR";
		float rot = 0F;
		float offX = 0.5F;
		float offZ = -0.01F;
		switch(front){
			case 3:{
				rot = 180F;
				offZ = 1.01F;
				break;
			}
			case 4:{
				rot = 90F;
				offX = -0.01F;
				offZ = 0.5F;
				break;
			}
			case 5:{
				rot = 270F;
				offX = 1.01F;
				offZ = 0.5F;
				break;
			}
		}
		TileTapeDrive drive = (TileTapeDrive) accessor.getTileEntity();
		ItemStack is = drive.getStackInSlot(0);
		if(is != null && is.getItem() instanceof IItemTapeStorage) {
			if(ConfigValues.DriveState.getValue(config)) {
				text = StringUtil.localize("tooltip.computronics.tape.state."
					+ drive.getEnumState().toString().toLowerCase(Locale.ENGLISH));
				UIHelper.drawFloatingText(text, accessor.getRenderingPosition(), offX, 0.3F, offZ, 0F,
					rot, 0F);
			}
		}
	}*/
}
