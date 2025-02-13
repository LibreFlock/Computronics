package org.libreflock.computronics.tile;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.network.BlacklistedPeripheral;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.multiperipheral.IMultiPeripheral;
import org.libreflock.computronics.audio.MachineSound;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.internal.IComputronicsPeripheral;
import org.libreflock.asielib.tile.TileMachine;
import org.libreflock.asielib.util.ColorUtils;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

//import org.libreflock.computronics.api.multiperipheral.IMultiPeripheral;

// #######################################################
//
// REMEMBER TO SYNC ME WITH TILEENTITYPERIPHERALINVENTORY!
//
// #######################################################

// @Optional.InterfaceList({
// 	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
// 	@Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = Mods.OpenComputers),
// 	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
// 	@Optional.Interface(iface = "org.libreflock.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft)
// })
public abstract class TileEntityPeripheralBase extends TileMachine implements Environment, DeviceInfo,
	IMultiPeripheral, IComputronicsPeripheral, BlacklistedPeripheral, IColorable {

	protected String peripheralName;

	public TileEntityPeripheralBase(String name) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC();
		}
		soundRes = getSoundFor(getSoundName());
	}

	public TileEntityPeripheralBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
		soundRes = getSoundFor(getSoundName());
	}

	public boolean isInvalid() {
		return this.level == null || !this.isRemoved();
	}

	public boolean isValid() {
		return !isInvalid();
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	protected void initOC(double s) {
		setNode(Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).withConnector(s).create());
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	protected void initOC() {
		setNode(Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).create());
	}

	// OpenComputers Environment boilerplate
	// From TileEntityEnvironment

	// Has to be an Object for getDeclaredFields to not error when
	// called on this class without OpenComputers being present. Blame OpenPeripheral.
	private Object node;
	// protected CopyOnWriteArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return (Node) node;
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	public void setNode(final Node node) {
		this.node = node;
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(final Node node) {
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public void onDisconnect(final Node node) {
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public void onMessage(final Message message) {
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			addToNetwork_OC();
		}
	}

	@Override
	public void update() {
		super.update();
		if(getLevel() == null) {
			return;
		}
		if(getLevel() instanceof ServerWorld && hasSound()) {
			updateSound();
		}
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	protected void addToNetwork_OC() {
		Computronics.opencomputers.scheduleForNetworkJoin(this);
		this.onOCNetworkCreation();
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	public void onOCNetworkCreation() {

	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			onChunkUnload_OC();
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			invalidate_OC();
		}
		if(getLevel() instanceof ServerWorld && hasSound()) {
			updateSound();
		}
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	protected void onChunkUnload_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	protected void invalidate_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	protected Map<String, String> deviceInfo;

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			OCUtils.Device device = deviceInfo();
			if(device != null) {
				return deviceInfo = device.deviceInfo();
			}
		}
		return deviceInfo;
	}

	@Nullable
	// @Optional.Method(modid = Mods.OpenComputers)
	protected abstract OCUtils.Device deviceInfo();

	// @Optional.Method(modid = Mods.OpenComputers)
	public void readFromNBT_OC(final CompoundNBT nbt) {
		if(node() != null && node().host() == this) {
			node().loadData(nbt.getCompound("oc:node"));
		}
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(final CompoundNBT nbt) {
		if(node() != null && node().host() == this) {
			final CompoundNBT nodeNbt = new CompoundNBT();
			node().saveData(nodeNbt);
			nbt.put("oc:node", nodeNbt);
		}
	}

	@Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	public String getType() {
		return peripheralName;
	}

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public void attach(IComputerAccess computer) {
	// 	if(attachedComputersCC == null) {
	// 		attachedComputersCC = new CopyOnWriteArrayList<IComputerAccess>();
	// 	}
	// 	attachedComputersCC.add(computer);
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public void detach(IComputerAccess computer) {
	// 	if(attachedComputersCC != null) {
	// 		attachedComputersCC.remove(computer);
	// 	}
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public boolean equals(IPeripheral other) {
	// 	if(other == null) {
	// 		return false;
	// 	}
	// 	if(this == other) {
	// 		return true;
	// 	}
	// 	if(other instanceof TileEntity) {
	// 		TileEntity tother = (TileEntity) other;
	// 		return tother.getWorld().equals(world)
	// 			&& tother.getBlockPos().equals(this.getBlockPos());
	// 	}

	// 	return false;
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public int peripheralPriority() {
	// 	return 1;
	// }

	protected int overlayColor = getDefaultColor();

	@Override
	public int getColor() {
		return overlayColor;
	}

	@Override
	public int getDefaultColor() {
		return ColorUtils.Color.White.color;
	}

	@Override
	public void setColor(int color) {
		this.overlayColor = color;
		this.setChanged();
	}

	@Override
	public boolean canBeColored() {
		return true;
	}

	@Override
	public void readFromRemoteNBT(CompoundNBT nbt) {
		super.readFromRemoteNBT(nbt);
		int oldColor = this.overlayColor;
		if(nbt.contains("computronics:color")) {
			overlayColor = nbt.getInt("computronics:color");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(oldColor != this.overlayColor) {
			this.getLevel().markBlockRangeForRenderUpdate(getBlockPos(), getBlockPos());
		}
	}

	@Override
	public CompoundNBT writeToRemoteNBT(CompoundNBT nbt) {
		nbt = super.writeToRemoteNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.putInt("computronics:color", overlayColor);
		}
		return nbt;
	}

	@Override
	public void readFromNBT(final CompoundNBT nbt) {
		super.readFromNBT(nbt);
		if(nbt.contains("computronics:color")) {
			overlayColor = nbt.getInt("computronics:color");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(nbt);
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		nbt = super.writeToNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.putInt("computronics:color", overlayColor);
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(nbt);
		}
		return nbt;
	}

	@Override
	public void removeFromNBTForTransfer(CompoundNBT data) {
		super.removeFromNBTForTransfer(data);
		data.remove("oc:node");
	}

	// Sound related, thanks to EnderIO code for this!

	@OnlyIn(Dist.CLIENT)
	private MachineSound sound;

	private ResourceLocation soundRes;

	@Nullable
	protected static ResourceLocation getSoundFor(@Nullable String sound) {
		return sound == null ? null : new ResourceLocation(Mods.Computronics + ":" + sound);
	}

	@Nullable
	public String getSoundName() {
		return null;
	}

	public ResourceLocation getSoundRes() {
		return soundRes;
	}

	public boolean shouldPlaySound() {
		return false;
	}

	public boolean hasSound() {
		return getSoundName() != null;
	}

	public float getVolume() {
		return 1.0f;
	}

	public float getPitch() {
		return 1.0f;
	}

	public boolean shouldRepeat() {
		return true;
	}

	@OnlyIn(Dist.CLIENT)
	private void updateSound() {
		if(hasSound()) {
			if(shouldPlaySound() && !isInvalid()) {
				if(sound == null) {
					sound = new MachineSound(getSoundRes(), getBlockPos(), getVolume(), getPitch(), shouldRepeat());
					FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
				}
			} else if(sound != null) {
				sound.endPlaying();
				sound = null;
			}
		}
	}
}
