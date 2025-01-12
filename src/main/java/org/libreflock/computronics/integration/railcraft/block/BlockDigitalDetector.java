package org.libreflock.computronics.integration.railcraft.block;

import li.cil.oc.api.network.Environment;
import mods.railcraft.common.util.misc.MiscTools;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.block.BlockPeripheral;
import org.libreflock.computronics.integration.railcraft.tile.TileDigitalDetector;
import org.libreflock.computronics.oc.manual.IBlockWithPrefix;
import org.libreflock.computronics.reference.Mods;

import static org.libreflock.asielib.util.WorldUtils.notifyBlockUpdate;

/**
 * @author CovertJaguar, Vexatos, marcin212, Kubuxu
 */
public class BlockDigitalDetector extends BlockPeripheral implements IBlockWithPrefix {

	public static final PropertyEnum<Direction> FRONT = PropertyEnum.create("front", Direction.class);

	public BlockDigitalDetector() {
		super("digital_detector", Rotation.NONE);
		this.setTranslationKey("computronics.detector");
		this.setResistance(4.5F);
		this.setHardness(2.0F);
		this.setSoundType(SoundType.STONE);
		this.setCreativeTab(Computronics.tab);
		this.setHarvestLevel("pickaxe", 2);
		this.setHarvestLevel("crowbar", 0);
	}

	@Deprecated
	@Override
	public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
		state = super.getActualState(state, world, pos);
		TileEntity t = world.getTileEntity(pos);

		if(t instanceof TileDigitalDetector) {
			return state.withProperty(FRONT, ((TileDigitalDetector) t).direction);
		}
		return state;
	}

	@Override
	protected BlockStateContainer createActualBlockState() {
		return new BlockStateContainer(this, FRONT);
	}

	@Override
	@Deprecated
	public boolean isSideSolid(BlockState base_state, IBlockAccess world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileDigitalDetector();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if((tile instanceof TileDigitalDetector)) {
			((TileDigitalDetector) tile).direction = MiscTools.getSideFacingPlayer(pos, placer);
			notifyBlockUpdate(world, pos, state);
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		return false;
	}

	@Override
	public boolean rotateBlock(World world, BlockPos pos, Direction axis) {
		TileEntity tile = world.getTileEntity(pos);
		if((tile instanceof TileDigitalDetector)) {
			TileDigitalDetector detector = (TileDigitalDetector) tile;
			if(detector.direction == axis) {
				detector.direction = axis.getOpposite();
			} else {
				detector.direction = axis;
			}
			notifyBlockUpdate(world, pos);
			return true;
		}
		return false;
	}

	@Override
	public Direction[] getValidRotations(World world, BlockPos pos) {
		return Direction.VALUES;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, BlockState state) {
		super.onBlockAdded(world, pos, state);
		notifyBlockUpdate(world, pos, state);
		if(world.isRemote) {
			return;
		}
		for(Direction side : Direction.VALUES) {
			world.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), false);
		}
	}

	@Override
	public void breakBlock(World world, BlockPos pos, BlockState state) {
		super.breakBlock(world, pos, state);
		if(world.isRemote) {
			return;
		}
		for(Direction side : Direction.VALUES) {
			world.notifyNeighborsOfStateChange(pos.offset(side), state.getBlock(), false);
		}
	}

	@Override
	public boolean canConnectRedstone(BlockState state, IBlockAccess world, BlockPos pos, Direction sid) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileDigitalDetector.class;
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
