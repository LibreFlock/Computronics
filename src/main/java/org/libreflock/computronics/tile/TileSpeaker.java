package org.libreflock.computronics.tile;

import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import gnu.trove.set.hash.TIntHashSet;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.cc.ISidedPeripheral;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;

import javax.annotation.Nullable;

@Optional.InterfaceList({
	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
})
public class TileSpeaker extends TileEntityPeripheralBase implements IAudioReceiver, ISidedPeripheral, SidedEnvironment {

	private final TIntHashSet packetIds = new TIntHashSet();
	private long idTick = -1;

	public TileSpeaker() {
		super("speaker");
	}

	@Override
	public World getSoundWorld() {
		return world;
	}

	@Override
	public Vector3d getSoundPos() {
		return new Vector3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Override
	public int getSoundDistance() {
		return Config.TAPEDRIVE_DISTANCE;
	}

	@Override
	public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
		if(!hasWorld() || idTick == world.getTotalWorldTime()) {
			if(packetIds.contains(packet.id)) {
				return;
			}
		} else {
			idTick = world.getTotalWorldTime();
			packetIds.clear();
		}

		packetIds.add(packet.id);
		packet.addReceiver(this);
	}

	@Override
	public String getID() {
		return AudioUtils.positionId(getPos());
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public String[] getMethodNames() {
		return new String[0];
	}

	@Override
	@Optional.Method(modid = Mods.ComputerCraft)
	public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
		return new Object[0];
	}

	@Override
	public boolean connectsAudio(Direction side) {
		if(hasWorld()) {
			BlockState state = world.getBlockState(getPos());
			return state.getValue(Computronics.speaker.rotation.FACING) != side;
		} else {
			return false;
		}
	}

	@Override
	public boolean canConnectPeripheralOnSide(Direction side) {
		return false;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void initOC(double s) {
		// NO-OP
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected void initOC() {
		// NO-OP
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return null;
	}

	@Nullable
	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(Direction side) {
		return null;
	}

	@Override
	@Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(Direction side) {
		return false;
	}
}
