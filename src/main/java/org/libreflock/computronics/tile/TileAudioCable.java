package org.libreflock.computronics.tile;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioConnection;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.audio.AudioUtils;
import org.libreflock.computronics.reference.Capabilities;
import org.libreflock.computronics.util.ColorUtils;
import org.libreflock.asielib.tile.TileEntityBase;
import org.libreflock.asielib.util.internal.IColorable;

import javax.annotation.Nullable;
import java.util.HashSet;

import static org.libreflock.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;
import static org.libreflock.computronics.reference.Capabilities.AUDIO_SOURCE_CAPABILITY;

public class TileAudioCable extends TileEntityBase implements IAudioReceiver, IColorable {

	private final HashSet<Object> packetIds = new HashSet<Object>();
	private long idTick = -1;

	private int ImmibisMicroblocks_TransformableTileEntityMarker;

	private byte connectionMap = 0;
	private boolean initialConnect = false;

	public void updateConnections() {
		final byte oldConnections = connectionMap;
		connectionMap = 0;
		for(Direction dir : Direction.VALUES) {
			if(!connectsInternal(dir)) {
				continue;
			}

			if(world.isBlockLoaded(getPos().offset(dir))) {
				TileEntity tile = world.getTileEntity(getPos().offset(dir));
				if(tile instanceof TileAudioCable) {
					if(!((TileAudioCable) tile).connectsInternal(dir.getOpposite())) {
						continue;
					}
				} else if(tile instanceof IAudioConnection) {
					if(!((IAudioConnection) tile).connectsAudio(dir.getOpposite())) {
						continue;
					}
				} /*else if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
					if(!IntegrationCharsetAudio.connects(tile, dir.getOpposite())) {
						continue;
					}
				}*/ else if(Capabilities.hasAny(tile, dir.getOpposite(), AUDIO_SOURCE_CAPABILITY, AUDIO_RECEIVER_CAPABILITY)) {
					IAudioConnection con = Capabilities.getFirst(tile, dir.getOpposite(), AUDIO_SOURCE_CAPABILITY, AUDIO_RECEIVER_CAPABILITY);
					if(con == null || !con.connectsAudio(dir)) {
						continue;
					}
				} else {
					continue;
				}

				IColorable targetCol = ColorUtils.getColorable(tile, dir.getOpposite());
				if(targetCol != null) {
					if(targetCol.canBeColored() && !ColorUtils.isSameOrDefault(this, targetCol)) {
						continue;
					}
				}

				connectionMap |= 1 << dir.ordinal();
			}
		}
		if(connectionMap != oldConnections) {
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	protected boolean connectsInternal(Direction dir) {
		return ImmibisMicroblocks_isSideOpen(dir.ordinal());
	}

	@Override
	public boolean connectsAudio(Direction dir) {
		if(!initialConnect) {
			updateConnections();
			initialConnect = true;
		}
		return ((connectionMap >> dir.ordinal()) & 1) == 1;
	}

	public boolean receivePacketID(Object o) {
		if(!hasWorld() || idTick == world.getTotalWorldTime()) {
			if(packetIds.contains(o)) {
				return false;
			}
		} else {
			idTick = world.getTotalWorldTime();
			packetIds.clear();
		}

		packetIds.add(o);
		return true;
	}

	@Override
	public void receivePacket(AudioPacket packet, @Nullable Direction side) {
		if(!receivePacketID(packet.id)) {
			return;
		}

		for(Direction dir : Direction.VALUES) {
			if(dir == side || !connectsAudio(dir)) {
				continue;
			}

			BlockPos pos = getPos().offset(dir);
			if(!world.isBlockLoaded(pos)) {
				continue;
			}

			TileEntity tile = world.getTileEntity(pos);
			if(tile != null && tile.hasCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite())) {
				tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).receivePacket(packet, dir.getOpposite());
			}
		}
	}

	@Override
	public String getID() {
		return AudioUtils.positionId(getPos());
	}

	@Override
	public World getSoundWorld() {
		return null;
	}

	@Override
	public Vector3d getSoundPos() {
		return Vector3d.ZERO;
	}

	@Override
	public int getSoundDistance() {
		return 0;
	}

	protected int overlayColor = getDefaultColor();

	@Override
	public boolean canBeColored() {
		return true;
	}

	@Override
	public int getColor() {
		return overlayColor;
	}

	@Override
	public int getDefaultColor() {
		return ColorUtils.Color.LightGray.color;
	}

	@Override
	public void setColor(int color) {
		this.overlayColor = color;
		this.updateConnections();
		this.markDirty();
	}

	@Override
	public void readFromRemoteNBT(CompoundNBT nbt) {
		super.readFromRemoteNBT(nbt);
		int oldColor = this.overlayColor;
		byte oldConnections = this.connectionMap;
		if(nbt.contains("col")) {
			overlayColor = nbt.getInt("col");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(nbt.contains("con")) {
			this.connectionMap = nbt.getByte("con");
		}
		if(oldColor != this.overlayColor || oldConnections != this.connectionMap) {
			this.world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public CompoundNBT writeToRemoteNBT(CompoundNBT nbt) {
		super.writeToRemoteNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.putInt("col", overlayColor);
		}
		nbt.putByte("con", connectionMap);
		return nbt;
	}

	@Override
	public void readFromNBT(final CompoundNBT nbt) {
		super.readFromNBT(nbt);
		if(nbt.contains("col")) {
			overlayColor = nbt.getInt("col");
		}
		if(this.overlayColor < 0) {
			this.overlayColor = getDefaultColor();
		}
		if(nbt.contains("con")) {
			this.connectionMap = nbt.getByte("con");
		}
	}

	@Override
	public CompoundNBT writeToNBT(final CompoundNBT nbt) {
		super.writeToNBT(nbt);
		if(overlayColor != getDefaultColor()) {
			nbt.putInt("col", overlayColor);
		}
		nbt.putByte("con", connectionMap);
		return nbt;
	}

	public boolean ImmibisMicroblocks_isSideOpen(int side) {
		return true;
	}

	public void ImmibisMicroblocks_onMicroblocksChanged() {

	}
}
