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
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.integration.info.ConfigValues;
import org.libreflock.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.StringUtil;

import java.util.List;

/**
 * @author Vexatos
 */
public class InfoLocomotiveRelay extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(!ConfigValues.RelayBound.getValue(config)) {
			return currenttip;
		}

		CompoundNBT nbt = accessor.getNBTData();
		String boundKey = "tooltip.computronics.waila.relay." + (nbt.getBoolean("bound") ? "bound" : "notbound");
		currenttip.add(StringUtil.localize(boundKey));
		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public CompoundNBT getNBTData(ServerPlayerEntity player, TileEntity te, CompoundNBT tag, World world, BlockPos pos) {
		if(te instanceof TileLocomotiveRelay) {
			TileLocomotiveRelay relay = (TileLocomotiveRelay) te;
			tag.putBoolean("bound", relay.isBound());
		}
		return tag;
	}

	@Override
	protected String getUID() {
		return "locomotive_relay";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(!(tile instanceof TileLocomotiveRelay)) {
			return;
		}
		TileLocomotiveRelay relay = (TileLocomotiveRelay) tile;
		String boundKey = "tooltip.computronics.waila.relay." + (relay.isBound() ? "bound" : "notbound");
		probeInfo.text(StringUtil.localize(boundKey));
	}
}

