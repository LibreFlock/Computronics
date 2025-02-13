package org.libreflock.computronics.tile;

// import dan200.computercraft.api.lua.ILuaContext;
// import dan200.computercraft.api.lua.LuaException;
// import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SidedEnvironment;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
// import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.audio.SoundCardPacket;
// import org.libreflock.computronics.cc.CCArgs;
// import org.libreflock.computronics.cc.ISidedPeripheral;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.OCUtils;
import org.libreflock.computronics.util.sound.Instruction.Close;
import org.libreflock.computronics.util.sound.Instruction.Open;
import org.libreflock.computronics.util.sound.Instruction.ResetAM;
import org.libreflock.computronics.util.sound.Instruction.ResetEnvelope;
import org.libreflock.computronics.util.sound.Instruction.ResetFM;
import org.libreflock.computronics.util.sound.Instruction.SetADSR;
import org.libreflock.computronics.util.sound.Instruction.SetAM;
import org.libreflock.computronics.util.sound.Instruction.SetFM;
import org.libreflock.computronics.util.sound.Instruction.SetFrequency;
import org.libreflock.computronics.util.sound.Instruction.SetLFSR;
import org.libreflock.computronics.util.sound.Instruction.SetVolume;
import org.libreflock.computronics.util.sound.SoundBoard;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
// @Optional.InterfaceList({
// 	@Optional.Interface(iface = "li.cil.oc.api.network.SidedEnvironment", modid = Mods.OpenComputers)
// })
public class TileSoundBoard extends TileEntityPeripheralBase implements IAudioSource, ITickable, SidedEnvironment, SoundBoard.ISoundHost {

	protected SoundBoard board;

	public TileSoundBoard() {
		super("sound");
		board = new SoundBoard(this);
	}

	@Override
	public void update() {
		super.update();
		board.update();
	}

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public boolean canConnectPeripheralOnSide(Direction side) {
	// 	return side == world.getBlockState(getBlockPos()).getValue(Computronics.computercraft.soundBoard.rotation.FACING);
	// }

	// @Optional.Method(modid = Mods.ComputerCraft)
	// protected int checkChannel(CCArgs args, int index) throws LuaException {
	// 	return board.checkChannel(args.checkInteger(index));
	// }

	// @Optional.Method(modid = Mods.ComputerCraft)
	// protected int checkChannel(CCArgs args) throws LuaException {
	// 	return checkChannel(args, 0);
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public String[] getMethodNames() {
	// 	return new String[] {
	// 		"getModes", "getChannelCount", "setTotalVolume", "clear",
	// 		"open", "close", "setWave", "setFrequency", "setLFSR",
	// 		"delay", "setFM", "resetFM", "setAM", "resetAM",
	// 		"setADSR", "resetEnvelope", "setVolume", "process"
	// 	};
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public Object[] callMethod(IComputerAccess computer, ILuaContext context,
	// 	int method, Object[] arguments) throws LuaException,
	// 	InterruptedException {
	// 	CCArgs args = new CCArgs(arguments);
	// 	switch(method) {
	// 		case 0: // getModes
	// 			return new Object[] { SoundBoard.compileModes() };
	// 		case 1: // getChannelCount
	// 			return new Object[] { board.process.states.size() };
	// 		case 2: // setTotalVolume
	// 			board.setTotalVolume(args.checkDouble(0));
	// 			return new Object[] {};
	// 		case 3: // clear
	// 			board.clear();
	// 			return new Object[] {};
	// 		case 4: // open
	// 			return board.tryAdd(new Open(checkChannel(args)));
	// 		case 5: // close
	// 			return board.tryAdd(new Close(checkChannel(args)));
	// 		case 6: // setWave
	// 			return board.setWave(args.checkInteger(0), args.checkInteger(1));
	// 		case 7: // setFrequency
	// 			return board.tryAdd(new SetFrequency(checkChannel(args), (float) args.checkDouble(1)));
	// 		case 8: // setLFSR
	// 			return board.tryAdd(new SetLFSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2)));
	// 		case 9: // delay
	// 			return board.delay(args.checkInteger(0));
	// 		case 10: // setFM
	// 			return board.tryAdd(new SetFM(checkChannel(args), checkChannel(args, 1), (float) args.checkDouble(2)));
	// 		case 11: // resetFM
	// 			return board.tryAdd(new ResetFM(checkChannel(args)));
	// 		case 12: // setAM
	// 			return board.tryAdd(new SetAM(checkChannel(args), checkChannel(args, 1)));
	// 		case 13: // resetAM
	// 			return board.tryAdd(new ResetAM(checkChannel(args)));
	// 		case 14: // setADSR
	// 			return board.tryAdd(new SetADSR(checkChannel(args), args.checkInteger(1), args.checkInteger(2), (float) args.checkDouble(3), args.checkInteger(4)));
	// 		case 15: // resetEnvelope
	// 			return board.tryAdd(new ResetEnvelope(checkChannel(args)));
	// 		case 16: // setVolume
	// 			return board.tryAdd(new SetVolume(checkChannel(args), (float) args.checkDouble(1)));
	// 		case 17: // process
	// 			return board.process();
	// 	}
	// 	return new Object[] {};
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public void detach(IComputerAccess computer) {
	// 	super.detach(computer);
	// 	if(attachedComputersCC.isEmpty()) {
	// 		board.clearAndStop();
	// 	}
	// }

	@Override
	public void setRemoved() {
		super.setRemoved();
		board.clearAndStop();
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		board.clearAndStop();
	}

	@Override
	public void readFromNBT(CompoundNBT nbt) {
		super.readFromNBT(nbt);
		board.load(nbt);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt) {
		nbt = super.writeToNBT(nbt);
		board.save(nbt);
		return nbt;
	}

	// No OC

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	protected void initOC(double s) {
		// NO-OP
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	protected void initOC() {
		// NO-OP
	}

	@Nullable
	@Override
	protected OCUtils.Device deviceInfo() {
		return null;
	}

	@Nullable
	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public Node sidedNode(Direction side) {
		return null;
	}

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	public boolean canConnect(Direction side) {
		return false;
	}

	@Override
	public boolean tryConsumeEnergy(double energy) {
		return true;
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(Direction side) {
			return true;
		}

		@Override
		public World getSoundWorld() {
			return getLevel();
		}

		@Override
		public Vector3d getSoundPos() {
			return new Vector3d(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D);
		}

		@Override
		public int getSoundDistance() {
			return Config.COMMON.TAPEDRIVE_DISTANCE.get();
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return AudioUtils.positionId(getBlockPos());
		}

	};

	@Override
	public String address() {
		for(IComputerAccess computer : attachedComputersCC) {
			if(computer != null) {
				return "cc_" + computer.getID();
			}
		}
		return this.toString();
	}

	@Override
	public void sendMusicPacket(SoundCardPacket pkt) {
		internalSpeaker.receivePacket(pkt, null);
		pkt.sendPacket();
	}

	@Override
	public World world() {
		return getLevel();
	}

	public Vector3d position() {
		return new Vector3d(getBlockPos().getX() + 0.5D, getBlockPos().getY() + 0.5D, getBlockPos().getZ() + 0.5D);
	}

	@Override
	public void setDirty() {
		setChanged();
	}

	@Override
	public int getSourceId() {
		return board.codecId;
	}

	@Override
	public boolean connectsAudio(Direction side) {
		return false;
	}
}
