package org.libreflock.asielib.tile;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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

	public boolean isUsableByPlayer(PlayerEntity player) {
		return this.world.getTileEntity(getBlockPos()) == this
			&& player.getDistanceSq(getBlockPos().add(0.5, 0.5, 0.5)) <= 64.0D;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
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
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(getBlockPos(), getBlockMetadata(), getUpdateTag());
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
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT tag = pkt.getNbtCompound();
		if(tag != null) {
			this.readFromRemoteNBT(tag);
		}
	}

	protected void notifyBlockUpdate() {
		WorldUtils.notifyBlockUpdate(getWorld(), getBlockPos());
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
