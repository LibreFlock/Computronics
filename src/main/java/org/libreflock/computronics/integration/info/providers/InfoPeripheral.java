package org.libreflock.computronics.integration.info.providers;

import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Node;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.integration.info.ConfigValues;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.StringUtil;
import org.libreflock.computronics.util.internal.IComputronicsPeripheral;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vexatos
 */
public class InfoPeripheral extends ComputronicsInfoProvider {

	@Override
	@Optional.Method(modid = Mods.Waila)
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
		IWailaConfigHandler config) {

		if(Mods.isLoaded(Mods.OpenComputers) && ConfigValues.OCAddress.getValue(config)) {
			CompoundNBT nbt = accessor.getNBTData();
			currenttip = getInfo_OC(nbt, currenttip);
		}
		return currenttip;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private List<String> getInfo_OC(CompoundNBT nbt, List<String> currenttip) {
		CompoundNBT node = nbt.getCompound("oc:node");
		if(node.contains("address")) {
			currenttip.add(StringUtil.localizeAndFormat("oc:gui.Analyzer.Address", node.getString("address")));
		}
		return currenttip;
	}

	@Override
	@Optional.Method(modid = Mods.Waila)
	public CompoundNBT getNBTData(EntityPlayerMP player, @Nullable TileEntity te, CompoundNBT tag, World world, BlockPos pos) {
		if(te != null && te instanceof IComputronicsPeripheral) {
			if(Mods.isLoaded(Mods.OpenComputers)) {
				tag = getNBTData_OC(te, tag);
			}
		}
		return tag;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public CompoundNBT getNBTData_OC(@Nullable TileEntity te, CompoundNBT tag) {
		if(!(te instanceof Environment)) {
			return tag;
		}
		Environment tile = ((Environment) te);
		Node node = tile.node();
		if(node != null && node.host() == tile) {
			final CompoundNBT nodeNbt = new CompoundNBT();
			node.save(nodeNbt);
			tag.put("oc:node", nodeNbt);
		}
		return tag;
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public String getUID() {
		return "component";
	}

	@Override
	@Optional.Method(modid = Mods.TheOneProbe)
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
		TileEntity tile = world.getTileEntity(data.getPos());
		if(Mods.isLoaded(Mods.OpenComputers) && mode == ProbeMode.EXTENDED) {
			for(String s : getInfo_OC(getNBTData_OC(tile, new CompoundNBT()), new ArrayList<String>(1))) {
				probeInfo.text(s);
			}
		}
	}
}
