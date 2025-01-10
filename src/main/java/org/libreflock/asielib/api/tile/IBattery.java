package org.libreflock.asielib.api.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

/*
 * Interface heavily inspired by CoFH (RF) and IC2 (EU) APIs.
 */
public interface IBattery {

	public double insert(@Nullable Direction side, double maximum, boolean simulate);

	public double extract(@Nullable Direction side, double maximum, boolean simulate);

	public double getEnergyStored();

	public double getMaxEnergyStored();

	public double getMaxEnergyInserted();

	public double getMaxEnergyExtracted();

	public boolean canInsert(@Nullable Direction side, String type);

	public boolean canExtract(@Nullable Direction side, String type);

	public void readFromNBT(CompoundNBT tag);

	public void writeToNBT(CompoundNBT tag);

	public void onTick();

	public double getEnergyUsage();

	public double getMaxEnergyUsage();

	@Nullable
	IEnergyStorage getStorage(Direction side);
}
