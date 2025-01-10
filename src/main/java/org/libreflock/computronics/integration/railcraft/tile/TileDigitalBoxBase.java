package org.libreflock.computronics.integration.railcraft.tile;

import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.DeviceInfo;
import li.cil.oc.api.network.BlacklistedPeripheral;
import li.cil.oc.api.network.Environment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import li.cil.oc.api.network.Visibility;
import mods.railcraft.common.blocks.machine.wayobjects.boxes.TileBoxBase;
import net.minecraft.block.Block;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.api.multiperipheral.IMultiPeripheral;
import org.libreflock.computronics.cc.ISidedPeripheral;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.internal.IComputronicsPeripheral;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author CovertJaguar, Vexatos
 */
@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.Environment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.driver.DeviceInfo", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "li.cil.oc.api.network.BlacklistedPeripheral", modid = Mods.OpenComputers),
	@Optional.Interface(iface = "org.libreflock.computronics.api.multiperipheral.IMultiPeripheral", modid = Mods.ComputerCraft)
})
public abstract class TileDigitalBoxBase extends TileBoxBase
	implements Environment, SidedEnvironment, DeviceInfo, IMultiPeripheral, IComputronicsPeripheral,
	ISidedPeripheral, BlacklistedPeripheral, ITickable {

	public TileDigitalBoxBase(String name) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC();
		}
	}

	public TileDigitalBoxBase(String name, double bufferSize) {
		super();
		this.peripheralName = name;
		if(Mods.isLoaded(Mods.OpenComputers)) {
			initOC(bufferSize);
		}
	}

	@Override
	public void update() {
		super.update();
		if(!addedToNetwork && Mods.isLoaded(Mods.OpenComputers)) {
			addedToNetwork = true;
			Network.joinOrCreateNetwork(this);
		}
	}

	@Override
	public Block getBlockType() {
		if(this.blockType == null && this.world != null) {
			this.blockType = this.world.getBlockState(this.pos).getBlock();
		}
		return this.blockType;
	}

	@Override
	public boolean isSideSolid(Direction side) {
		return side == Direction.UP;
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT data) {
		data = super.writeToNBT(data);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			writeToNBT_OC(data);
		}
		return data;
	}

	@Override
	public void readFromNBT(CompoundNBT data) {
		super.readFromNBT(data);
		if(Mods.isLoaded(Mods.OpenComputers)) {
			readFromNBT_OC(data);
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			onChunkUnload_OC();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if(Mods.isLoaded(Mods.OpenComputers)) {
			invalidate_OC();
		}
	}

	// Computer Stuff //

	protected String peripheralName;
	// Has to be an Object for getDeclaredFields to not error when
	// called on this class without OpenComputers being present. Blame OpenPeripheral.
	protected Object node;
	protected CopyOnWriteArrayList<IComputerAccess> attachedComputersCC;
	protected boolean addedToNetwork = false;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean isPeripheralBlacklisted() {
		return true;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node node() {
		return (Node) node;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onConnect(Node node) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onDisconnect(Node node) {

	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public void onMessage(Message message) {

	}

	protected Map<String, String> deviceInfo;

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Map<String, String> getDeviceInfo() {
		if(deviceInfo == null) {
			OCUtils.Device device = deviceInfo();
			if(device != null) {
				return deviceInfo = device.deviceInfo();
			}
		}
		return deviceInfo;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected abstract OCUtils.Device deviceInfo();

	@Nullable
	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(Direction forgeDirection) {
		return forgeDirection == Direction.DOWN || forgeDirection == Direction.UP ? node() : null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	@OnlyIn(Dist.CLIENT)
	public boolean canConnect(Direction forgeDirection) {
		return forgeDirection == Direction.DOWN || forgeDirection == Direction.UP;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String getType() {
		return peripheralName;
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOC(double s) {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).withConnector(s).create();
	}

	@Optional.Method(modid = Mods.OpenComputers)
	private void initOC() {
		node = Network.newNode(this, Visibility.Network).withComponent(this.peripheralName, Visibility.Network).create();
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void readFromNBT_OC(final CompoundNBT nbt) {
		if(node() != null && node().host() == this) {
			node().load(nbt.getCompound("oc:node"));
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	public void writeToNBT_OC(final CompoundNBT nbt) {
		if(node() != null && node().host() == this) {
			final CompoundNBT nodeNbt = new CompoundNBT();
			node().save(nodeNbt);
			nbt.put("oc:node", nodeNbt);
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void onChunkUnload_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	@Optional.Method(modid = Mods.OpenComputers)
	protected void invalidate_OC() {
		if(node() != null) {
			node().remove();
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void attach(IComputerAccess computer) {
		if(attachedComputersCC == null) {
			attachedComputersCC = new CopyOnWriteArrayList<IComputerAccess>();
		}
		attachedComputersCC.add(computer);
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public void detach(IComputerAccess computer) {
		if(attachedComputersCC != null) {
			attachedComputersCC.remove(computer);
		}
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public boolean equals(@Nullable IPeripheral other) {
		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(other instanceof TileEntity) {
			TileEntity tother = (TileEntity) other;
			return tother.getWorld().equals(world)
				&& tother.getPos().equals(this.getPos());
		}

		return false;
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public int peripheralPriority() {
		return 5;
	}

	@Override
	public boolean canConnectPeripheralOnSide(Direction side) {
		return side == Direction.DOWN || side == Direction.UP;
	}
}
