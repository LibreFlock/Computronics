package org.libreflock.computronics.integration.buildcraft.statements.parameters;

import buildcraft.api.statements.IStatement;
import buildcraft.api.statements.IStatementContainer;
import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.StatementMouseClick;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Items;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.integration.buildcraft.statements.StatementParameters;
import org.libreflock.computronics.util.StringUtil;

/**
 * @author Vexatos
 */
public class ActionParameterLampColor extends ComputronicsParameter {

	public ItemStack stack;

	public int color;

	public ActionParameterLampColor() {
		super(StatementParameters.Lamp_Color);
	}

	public ActionParameterLampColor(int slot) {
		this();
		this.setColor(slot);
	}

	private void setColor(int index) {
		this.color = (byte) (index == 0 ? 1 : index == 1 ? 2 : 4);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public TextureAtlasSprite getIcon() {
		return null;
	}

	@Override
	public ItemStack getItemStack() {
		return stack != null ? stack : new ItemStack(Items.dye, 1, 8);
	}

	@Override
	public String getDescription() {
		if(this.stack != null) {
			return StringUtil.localizeAndFormat("tooltip.computronics.gate.action.lamp_color."
				+ DyeColor.byDyeDamage(this.color), stack.stackSize);
		}
		return StringUtil.localizeAndFormat("tooltip.computronics.gate.action.lamp_color."
			+ DyeColor.byDyeDamage(this.color), 0);
	}

	@Override
	public void onClick(IStatementContainer container, IStatement statement, ItemStack stack, StatementMouseClick mouseClick) {
		int button = mouseClick.getButton();
		boolean shift = mouseClick.isShift();
		if(this.stack == null && button == 0 && !shift) {
			this.stack = new ItemStack(Items.dye, 1, color);
			if(stack != null && stack.stackSize >= 1) {
				this.stack.stackSize = stack.stackSize;
			}
		} else if(this.stack == null && (button == 2 || shift)) {
			this.stack = new ItemStack(Items.dye,
				(stack != null && stack.stackSize >= 1 && button != 2) ? stack.stackSize : button == 0 ? 10 : 31, color);
		} else if(this.stack != null) {
			if(stack != null && stack.stackSize >= 1) {
				int size = (button == 0 ? this.stack.stackSize + stack.stackSize :
					this.stack.stackSize - stack.stackSize);
				size = size > 31 ? 31 : size <= 0 ? 0 : size;
				this.stack.stackSize = size;
			} else {
				if(this.stack.stackSize < 31 && button == 0) {
					this.stack.stackSize += shift ? 10 : 1;
				} else if(this.stack.stackSize > 0 && button == 1) {
					this.stack.stackSize -= shift ? 10 : 1;
				}
			}
			if(this.stack.stackSize >= 0 && button == 2) {
				this.stack = null;
			}
		}
		if(this.stack != null) {
			this.stack.stackSize = this.stack.stackSize > 31 ? 31 : this.stack.stackSize < 0 ? 0 : this.stack.stackSize;
			if(this.stack.stackSize == 0) {
				this.stack = null;
			}
		}
	}

	@Override
	public void writeToNBT(CompoundNBT data) {
		if(this.stack != null) {
			data.putInt("size", this.stack.stackSize);
		}
		data.putInt("color", color);
	}

	@Override
	public IStatementParameter rotateLeft() {
		return null;
	}

	@Override
	public void readFromNBT(CompoundNBT data) {
		this.color = data.contains("color") ? data.getInt("color") : 1;
		this.stack = data.contains("size") ? new ItemStack(Items.dye, data.getInt("size"), this.color) : null;
	}

	@Override
	public boolean equals(Object object) {
		if((object instanceof ActionParameterLampColor)) {
			ActionParameterLampColor param = (ActionParameterLampColor) object;

			return (ItemStack.areItemStacksEqual(this.stack, param.stack))
				&& (ItemStack.areItemStackTagsEqual(this.stack, param.stack))
				&& this.color == param.color;
		}
		return false;
	}
}
