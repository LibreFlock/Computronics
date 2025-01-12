package org.libreflock.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.api.charge.Charge;
import mods.railcraft.api.charge.IChargeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.block.BlockPeripheral;
import org.libreflock.computronics.integration.railcraft.tile.TileTicketMachine;
import org.libreflock.computronics.oc.manual.IBlockWithPrefix;

import java.util.Map;

/**
 * @author Vexatos
 */
public class BlockTicketMachine extends BlockPeripheral implements IBlockWithPrefix, IChargeBlock {

	public BlockTicketMachine() {
		super("ticket_machine", Rotation.FOUR);
		this.setTranslationKey("computronics.ticketMachine");
		this.setGuiProvider(Computronics.railcraft.guiTicketMachine);
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileTicketMachine) {
			((TileTicketMachine) tile).onBlockPlacedBy(placer, stack);
		}
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileTicketMachine();
	}

	@Override
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileTicketMachine.class;
	}

	@Override
	public Map<Charge, ChargeSpec> getChargeSpecs(BlockState state, IBlockAccess world, BlockPos pos) {
		return TileTicketMachine.CHARGE_SPECS;
	}

	private static final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return prefix;
	}
}
