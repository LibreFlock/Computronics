package org.libreflock.computronics.network;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.INetHandler;
import net.minecraft.tileentity.TileEntity;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.tape.PortableDriveManager;
import org.libreflock.computronics.tape.PortableTapeDrive;
import org.libreflock.computronics.tile.TapeDriveState.State;
import org.libreflock.computronics.tile.TileTapeDrive;
import org.libreflock.asielib.network.MessageHandlerBase;
import org.libreflock.asielib.network.Packet;

import java.io.IOException;

public class NetworkHandlerServer extends MessageHandlerBase {

	@Override
	public void onMessage(Packet packet, INetHandler handler, PlayerEntity player, int command)
		throws IOException {
		PacketType type = PacketType.of(command);
		if(type == null) {
			return;
		}
		switch(type) {
			case TAPE_GUI_STATE: {
				TileEntity entity = packet.readTileEntityServer();
				State state = State.VALUES[packet.readUnsignedByte()];
				if(entity instanceof TileTapeDrive) {
					TileTapeDrive tile = (TileTapeDrive) entity;
					tile.switchState(state);
				}
			}
			break;
			case TICKET_SYNC: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.onMessageRailcraft(packet, player, true);
				}
			}
			break;
			case TICKET_PRINT: {
				if(Mods.isLoaded(Mods.Railcraft)) {
					Computronics.railcraft.printTicket(packet, player, true);
				}
			}
			break;
			case PORTABLE_TAPE_STATE: {
				PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getTapeDrive(packet.readString(), false);
				State state = State.VALUES[packet.readUnsignedByte()];
				if(tapeDrive != null) {
					tapeDrive.switchState(state);
				}
			}
			break;
		}
	}
}
