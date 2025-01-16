package org.libreflock.computronics.integration.charset.audio;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.libreflock.charset.api.audio.AudioPacket;
import org.libreflock.charset.api.audio.IAudioReceiver;
import org.libreflock.computronics.tile.TileAudioCable;

public class AudioReceiverCable implements IAudioReceiver {

	private final TileAudioCable cable;
	private final Direction side;

	public AudioReceiverCable(TileAudioCable cable, Direction side) {
		this.cable = cable;
		this.side = side;
	}

	@Override
	public boolean receive(AudioPacket packet) {
		if(!cable.receivePacketID(packet)) {
			return false;
		}

		World worldObj = cable.getWorld();
		boolean sent = false;

		for(Direction dir : Direction.VALUES) {
			if(dir == side || !cable.connectsAudio(dir)) {
				continue;
			}

			BlockPos pos = cable.getBlockPos().offset(dir);
			if(!worldObj.isBlockLoaded(pos)) {
				continue;
			}

			TileEntity tile = worldObj.getTileEntity(pos);
			if(tile != null && tile.hasCapability(IntegrationCharsetAudio.RECEIVER_CAPABILITY, dir.getOpposite())) {
				sent |= tile.getCapability(IntegrationCharsetAudio.RECEIVER_CAPABILITY, dir.getOpposite()).receive(packet);
			}
		}

		return sent;
	}
}
