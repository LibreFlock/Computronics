package org.libreflock.computronics.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.item.block.IBlockWithDifferentColors;
import org.libreflock.computronics.oc.manual.IBlockWithDocumentation;
import org.libreflock.computronics.tile.TileAudioCable;
import org.libreflock.computronics.util.internal.IBlockWithColor;
import org.libreflock.asielib.block.BlockBase;
import org.libreflock.asielib.util.ColorUtils;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;

import static org.libreflock.asielib.util.WorldUtils.notifyBlockUpdate;

public class BlockAudioCable extends BlockBase implements IBlockWithDocumentation, IBlockWithDifferentColors, IBlockWithColor {

	private int connectionMask = 0x3f;
	private int ImmibisMicroblocks_TransformableBlockMarker;

	public BlockAudioCable() {
		super(Material.IRON, Computronics.instance, Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		this.setTranslationKey("computronics.audiocable");
	}

	public void setRenderMask(int m) {
		this.connectionMask = m;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		ItemStack heldItem = player.getHeldItem(hand);
		if(tile instanceof TileAudioCable && !heldItem.isEmpty()) {
			ColorUtils.Color color = ColorUtils.getColor(heldItem);
			if(color != null) {
				((TileAudioCable) tile).setColor(color.color);
				notifyBlockUpdate(world, pos, state);
				return true;
			}
		}
		return false;
	}

	public int getRenderColor() {
		return ColorUtils.Color.LightGray.color;
	}

	public int getRenderColor(BlockState state) {
		return getRenderColor();
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int renderPass) {
		if(world != null && pos != null) {
			TileEntity tile = world.getTileEntity(pos);
			if(tile instanceof IColorable) {
				return ((IColorable) tile).getColor();
			}
		}
		return getRenderColor(state);
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, Direction side, DyeColor color) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			((IColorable) tile).setColor(ColorUtils.fromColor(color).color);
			notifyBlockUpdate(world, pos);
			return true;
		}
		return super.recolorBlock(world, pos, side, color);
	}

	@Override
	public boolean hasSubTypes() {
		return false;
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int pass) {
		return getRenderColor();
	}

	// Collision box magic

	/*@Override
	public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
		setBlockBoundsBasedOnState(world, pos);
		return super.getCollisionBoundingBox(world, pos, state);
	}

	@Override
	public final void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		setBlockBounds(BoundingBox.getBox(neighbors(world, pos)));
	}*/

	/**
	 * @author Sangar, Vexatos, asie
	 */
	@SuppressWarnings("PointlessBitwiseExpression")
	private static class BoundingBox {

		private static final AxisAlignedBB[] bounds = new AxisAlignedBB[0x40];

		static {
			for(int mask = 0; mask < 0x40; ++mask) {
				bounds[mask] = new AxisAlignedBB(
					((mask & (1 << 4)) != 0 ? 0 : 0.3125),
					((mask & (1 << 0)) != 0 ? 0 : 0.3125),
					((mask & (1 << 2)) != 0 ? 0 : 0.3125),
					((mask & (1 << 5)) != 0 ? 1 : 0.6875),
					((mask & (1 << 1)) != 0 ? 1 : 0.6875),
					((mask & (1 << 3)) != 0 ? 1 : 0.6875)
				);
			}
		}

		private static AxisAlignedBB getBox(int msk) {
			return bounds[msk];
		}
	}

	private int neighbors(IBlockAccess world, BlockPos pos) {
		int result = 0;
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileAudioCable) {
			for(Direction side : Direction.VALUES) {
				if(((TileAudioCable) tile).connectsAudio(side)) {
					result |= 1 << side.ordinal();
				}
			}
		}
		return result;
	}

	/*@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB aabb, List boxes, Entity entity) {
		boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z + 0.3125, x + 1 - 0.3125, y + 1 - 0.3125, z + 1 - 0.3125));
		TileAudioCable tac = (TileAudioCable) world.getTileEntity(x, y, z);
		if(tac != null) {
			for(int i = 0; i < 6; i++) {
				if(tac.connects(ForgeDirection.getOrientation(i))) {
					switch(i) {
						case 0:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y, z + 0.3125, x + 1 - 0.3125, y + 0.3125, z + 1 - 0.3125));
							break;
						case 1:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 1 - 0.3125, z + 0.3125, x + 1 - 0.3125, y + 1, z + 1 - 0.3125));
							break;
						case 2:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z, x + 1 - 0.3125, y + 1 - 0.3125, z + 0.3125));
							break;
						case 3:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 0.3125, y + 0.3125, z + 1 - 0.3125, x + 1 - 0.3125, y + 1 - 0.3125, z + 1));
							break;
						case 4:
							boxes.add(AxisAlignedBB.getBoundingBox(x, y + 0.3125, z + 0.3125, x + 0.3125, y + 1 - 0.3125, z + 1 - 0.3125));
							break;
						case 5:
							boxes.add(AxisAlignedBB.getBoundingBox(x + 1 - 0.3125, y + 0.3125, z + 0.3125, x + 1, y + 1 - 0.3125, z + 1 - 0.3125));
							break;
					}
				}
			}
		}
	}*/

	// End of collision box magic

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public static final PropertyBool UP = PropertyBool.create("up");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool WEST = PropertyBool.create("west");

	@Override
	protected BlockStateContainer createActualBlockState() {
		return new BlockStateContainer(this,
			DOWN,
			UP,
			NORTH,
			SOUTH,
			WEST,
			EAST
		);
	}

	@Override
	@Deprecated
	public BlockState getActualState(BlockState state, IBlockAccess world, BlockPos pos) {
		TileEntity t = world.getTileEntity(pos);

		if(t instanceof TileAudioCable) {
			TileAudioCable tile = ((TileAudioCable) t);
			return state
				.withProperty(DOWN, tile.connectsAudio(Direction.DOWN))
				.withProperty(UP, tile.connectsAudio(Direction.UP))
				.withProperty(NORTH, tile.connectsAudio(Direction.NORTH))
				.withProperty(SOUTH, tile.connectsAudio(Direction.SOUTH))
				.withProperty(WEST, tile.connectsAudio(Direction.WEST))
				.withProperty(EAST, tile.connectsAudio(Direction.EAST));
		} else {
			return state;
		}
	}

	@Override
	@Deprecated
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		TileEntity t = world.getTileEntity(pos);

		if(t instanceof TileAudioCable) {
			((TileAudioCable) t).updateConnections();
			world.notifyBlockUpdate(pos, state, state, 3);
		}
		super.neighborChanged(state, world, pos, block, otherPos);
	}

	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity t = world.getTileEntity(pos);

		if(t instanceof TileAudioCable) {
			((TileAudioCable) t).updateConnections();
		}
		super.onNeighborChange(world, pos, neighbor);
	}

	@Override
	@Deprecated
	public boolean isSideSolid(BlockState state, IBlockAccess world, BlockPos pos, Direction side) {
		return false;
	}

	@Override
	@Deprecated
	public AxisAlignedBB getBoundingBox(BlockState state, IBlockAccess world, BlockPos pos) {
		return BoundingBox.getBox(neighbors(world, pos));
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(BlockState state) {
		return false;
	}

	@Override
	@Deprecated
	@OnlyIn(Dist.CLIENT)
	public boolean shouldSideBeRendered(BlockState state, IBlockAccess worldIn, BlockPos pos, Direction side) {
		return (connectionMask & (1 << side.ordinal())) != 0;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileAudioCable();
	}

	protected String documentationName = "audio_cable";

	@Override
	public String getDocumentationName(World world, BlockPos pos) {
		return this.documentationName;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return this.documentationName;
	}
}
