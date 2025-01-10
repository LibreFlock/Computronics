package org.libreflock.computronics.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.oc.block.IComputronicsEnvironmentBlock;
import org.libreflock.computronics.oc.manual.IBlockWithDocumentation;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileEntityPeripheralBase;
import org.libreflock.computronics.util.internal.IBlockWithColor;
import org.libreflock.asielib.block.BlockBase;
import org.libreflock.asielib.util.ColorUtils;
import org.libreflock.asielib.util.ColorUtils.Color;
import org.libreflock.asielib.util.internal.IColorable;

import static org.libreflock.asielib.util.WorldUtils.notifyBlockUpdate;

@Optional.InterfaceList({
	@Optional.Interface(iface = "org.libreflock.computronics.oc.block.IComputronicsEnvironmentBlock", modid = Mods.OpenComputers)
})
public abstract class BlockPeripheral extends BlockBase implements IComputronicsEnvironmentBlock, IBlockWithDocumentation, IBlockWithColor {

	public BlockPeripheral(String documentationName, Rotation rotation) {
		super(Material.IRON, Computronics.instance, rotation);
		this.setCreativeTab(Computronics.tab);
		this.documentationName = documentationName;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, Direction side, float hitX, float hitY, float hitZ) {
		TileEntity tile = world.getTileEntity(pos);
		ItemStack heldItem = player.getHeldItem(hand);
		if(tile instanceof TileEntityPeripheralBase && ((TileEntityPeripheralBase) tile).canBeColored() && !heldItem.isEmpty()) {
			Color color = ColorUtils.getColor(heldItem);
			if(color != null) {
				((TileEntityPeripheralBase) tile).setColor(color.color);
				notifyBlockUpdate(world, pos, state);
				return true;
			}
		}
		return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
	}

	public int getRenderColor(IBlockState state) {
		return 0xFFFFFF;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int renderPass) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			return ((IColorable) tile).getColor();
		}
		return getRenderColor(state);
	}

	@Override
	public boolean recolorBlock(World world, BlockPos pos, Direction side, EnumDyeColor color) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof IColorable && ((IColorable) tile).canBeColored()) {
			((IColorable) tile).setColor(ColorUtils.fromColor(color).color);
			notifyBlockUpdate(world, pos);
			return true;
		}
		return super.recolorBlock(world, pos, side, color);
	}

	@Override
	@Deprecated
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos otherPos) {
		super.neighborChanged(state, world, pos, block, otherPos);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isNormalCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isBlockNormalCube(IBlockState state) {
		return true;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, Direction face) {
		return true;
	}

	protected String documentationName;

	@Override
	public String getDocumentationName(World world, BlockPos pos) {
		return this.documentationName;
	}

	@Override
	public String getDocumentationName(ItemStack stack) {
		return this.documentationName;
	}
}
