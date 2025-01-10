package org.libreflock.computronics.item.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.libreflock.computronics.util.internal.IItemWithColor;

public class ComputronicsItemBlock extends ItemBlock implements IItemWithColor {

	private IBlockWithSpecialText specialBlock;
	private IBlockWithDifferentColors coloredBlock;

	public ComputronicsItemBlock(Block block) {
		super(block);
		if(block instanceof IBlockWithSpecialText) {
			this.specialBlock = (IBlockWithSpecialText) block;
			this.setHasSubtypes(specialBlock.hasSubTypes());
		}
		if(block instanceof IBlockWithDifferentColors) {
			this.coloredBlock = ((IBlockWithDifferentColors) block);
			this.setHasSubtypes(coloredBlock.hasSubTypes());
		}
	}

	@Override
	public String getTranslationKey(ItemStack stack) {
		if(this.specialBlock != null) {
			return this.specialBlock.getTranslationKey(stack);
		}
		return super.getTranslationKey(stack);
	}

	@Override
	public int getMetadata(int damage) {
		return damage;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public int getColorFromItemstack(ItemStack stack, int pass) {
		if(this.coloredBlock != null) {
			return this.coloredBlock.getColorFromItemstack(stack, pass);
		}
		return 0xFFFFFFFF;
	}
}
