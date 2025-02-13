package org.libreflock.computronics.block;

import li.cil.oc.api.network.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.item.block.IBlockWithDifferentColors;
import org.libreflock.computronics.item.block.IBlockWithSpecialText;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tile.TileChatBox;
import org.libreflock.computronics.util.StringUtil;
import org.libreflock.asielib.tile.TileEntityBase;

import javax.annotation.Nullable;
import java.util.List;

public class BlockChatBox extends BlockPeripheral implements IBlockWithSpecialText, IBlockWithDifferentColors {

	public static final PropertyBool CREATIVE = PropertyBool.create("creative");

	public BlockChatBox() {
		super("chatbox", Rotation.NONE);
		this.setCreativeTab(Computronics.tab);
		this.setDefaultState(this.blockState.getBaseState().withProperty(CREATIVE, false));
		this.setTranslationKey("computronics.chatBox");
	}

	// I'm such a cheater.
	@Override
	public int getRenderColor(BlockState state) {
		return state.getValue(CREATIVE) ? 0xFF60FF : 0xFFFFFF;
	}

	// Cheaters never win! ~ jaquadro
	@Override
	@OnlyIn(Dist.CLIENT)
	public int colorMultiplier(BlockState state, IBlockAccess world, BlockPos pos, int renderPass) {
		return state.getValue(CREATIVE) ? getRenderColor(state) : super.colorMultiplier(state, world, pos, renderPass);
	}

	@Override
	public int getColorFromItemstack(ItemStack stack, int pass) {
		return (stack.getItemDamage() & 8) != 0 ? 0xFF60FF : 0xFFFFFF;
	}

	@Override
	public TileEntity createTileEntity(World world, BlockState state) {
		return new TileChatBox();
	}

	@Override
	public void getSubBlocks(ItemGroup creativeTabs, NonNullList<ItemStack> blockList) {
		blockList.add(new ItemStack(this, 1, 0));
		if(Config.CHATBOX_CREATIVE) {
			blockList.add(new ItemStack(this, 1, 8));
		}
	}

	@Override
	@Deprecated
	public BlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta).withProperty(CREATIVE, (meta & 8) != 0);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return super.getMetaFromState(state) | (state.getValue(CREATIVE) ? 8 : 0);
	}

	@Override
	public int damageDropped(BlockState state) {
		return getMetaFromState(state);
	}

	@Override
	protected BlockStateContainer createActualBlockState() {
		return new BlockStateContainer(this, CREATIVE);
	}

	@Override
	protected BlockState createDefaultState() {
		return super.createDefaultState().withProperty(CREATIVE, false);
	}

	@Override
	public boolean emitsRedstone(IBlockAccess world, BlockPos pos, Direction side) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public boolean hasComparatorInputOverride(BlockState state) {
		return Config.REDSTONE_REFRESH;
	}

	@Override
	@Deprecated
	public int getComparatorInputOverride(BlockState state, World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if(tile instanceof TileEntityBase) {
			return ((TileEntityBase) tile).requestCurrentRedstoneValue(null);
		}
		return super.getComparatorInputOverride(state, world, pos);
	}

	@Override
	public boolean hasSubTypes() {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> list, ITooltipFlag flag) {
		if(stack.getItemDamage() >= 8) {
			list.add(TextFormatting.GRAY + StringUtil.localize("tooltip.computronics.chatBox.creative"));
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		return (stack.getItemDamage() >= 8 ? "tile.computronics.chatBox.creative" : this.getTranslationKey());
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Class<? extends Environment> getTileEntityClass(int meta) {
		return TileChatBox.class;
	}
}
