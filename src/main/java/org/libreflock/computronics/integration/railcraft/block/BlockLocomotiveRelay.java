package org.libreflock.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.block.BlockPeripheral;
import org.libreflock.computronics.integration.railcraft.tile.TileLocomotiveRelay;
import org.libreflock.computronics.oc.manual.IBlockWithPrefix;
import org.libreflock.computronics.reference.Mods;

/**
 * @author Vexatos
 */
public class BlockLocomotiveRelay extends BlockPeripheral implements IBlockWithPrefix {

	public BlockLocomotiveRelay() {
		super("locomotive_relay", Rotation.NONE);
		this.setTranslationKey("computronics.locomotiveRelay");
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileLocomotiveRelay();
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileLocomotiveRelay.class;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		if(!world.isRemote && player.isSneaking() && player.getHeldItemMainhand().isEmpty() && player.getHeldItemOffhand().isEmpty()) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof TileLocomotiveRelay) {
				String msg;
				if(((TileLocomotiveRelay) tile).unbind()) {
					msg = "chat.computronics.relay.unbound";
				} else {
					msg = "chat.computronics.relay.notBound";
				}
				player.sendMessage(new TranslationTextComponent(msg));
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	private final String prefix = "railcraft/";

	@Override
	public String getPrefix(World world, BlockPos pos) {
		return this.prefix;
	}

	@Override
	public String getPrefix(ItemStack stack) {
		return this.prefix;
	}
}
