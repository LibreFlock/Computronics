package org.libreflock.computronics.integration.charset.audio;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import org.libreflock.charset.api.audio.AudioSink;
import org.libreflock.computronics.tile.TileSpeaker;
import org.libreflock.asielib.AsieLibMod;

public class AudioSinkSpeaker extends AudioSink {

	private TileSpeaker speaker;

	public AudioSinkSpeaker() {

	}

	public AudioSinkSpeaker(TileSpeaker speaker) {
		this.speaker = speaker;
	}

	@Override
	public World getWorld() {
		return speaker.getWorld();
	}

	@Override
	public Vector3d getBlockPos() {
		return new Vector3d(speaker.getBlockPos());
	}

	@Override
	public float getDistance() {
		return speaker.getSoundDistance();
	}

	@Override
	public float getVolume() {
		return speaker.getVolume();
	}

	@Override
	public void writeData(ByteBuf buffer) {
		super.writeData(buffer);
		buffer.writeInt(speaker.getWorld().provider.getDimension());
		buffer.writeInt(speaker.getBlockPos().getX());
		buffer.writeInt(speaker.getBlockPos().getY());
		buffer.writeInt(speaker.getBlockPos().getZ());
	}

	@Override
	public void readData(ByteBuf buffer) {
		super.readData(buffer);
		speaker = null;

		int dimension = buffer.readInt();
		int x = buffer.readInt();
		int y = buffer.readInt();
		int z = buffer.readInt();

		World world = AsieLibMod.proxy.getWorld(dimension);
		if(world != null) {
			TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
			if(tile instanceof TileSpeaker) {
				speaker = (TileSpeaker) tile;
			}
		}
	}

}
