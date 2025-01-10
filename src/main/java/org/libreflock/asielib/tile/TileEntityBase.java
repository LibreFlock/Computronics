package org.libreflock.asielib.tile;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.libreflock.asielib.util.WorldUtils;

import javax.annotation.Nullable;

public class TileEntityBase extends TileEntity {

	// Base functions for containers
	public void openInventory() {

	}

	public void closeInventory() {

	}

	public boolean isUsableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(getPos()) == this
			&& player.getDistanceSq(getPos().add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	// Remote NBT data management
	public void readFromRemoteNBT(CompoundNBT tag) {
		// NO-OP
	}

	public CompoundNBT writeToRemoteNBT(CompoundNBT tag) {
		return tag;
	}

	@Override
	@Nullable
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.writeToRemoteNBT(super.getUpdateTag());
	}

	@Override
	public void handleUpdateTag(CompoundNBT tag) {
		super.handleUpdateTag(tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		if(tag != null) {
			this.readFromRemoteNBT(tag);
		}
	}

	protected void notifyBlockUpdate() {
		WorldUtils.notifyBlockUpdate(getWorld(), getPos());
	}

	// Dummy functions

	public void onBlockDestroy() {
	}

	protected int oldRedstoneSignal = -1;

	public int getOldRedstoneSignal() {
		return this.oldRedstoneSignal;
	}

	public void setRedstoneSignal(int value) {
		this.oldRedstoneSignal = value;
	}

	@Override
	public void readFromNBT(CompoundNBT tag) {
		super.readFromNBT(tag);
		this.oldRedstoneSignal = tag.getInt("old_redstone");
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT tag) {
		tag = super.writeToNBT(tag);
		tag.putInt("old_redstone", this.oldRedstoneSignal);
		return tag;
	}

	public int requestCurrentRedstoneValue(@Nullable Direction side) {
		return 0;
	}
}
