package org.libreflock.computronics.gui.providers;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.gui.GuiTapePlayer;
import org.libreflock.computronics.gui.IGuiTapeDrive;
import org.libreflock.computronics.gui.container.ContainerTapeReader;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.computronics.tile.TapeDriveState;
import org.libreflock.computronics.tile.TileTapeDrive;
import org.libreflock.asielib.gui.container.ContainerInventory;
import org.libreflock.asielib.gui.managed.GuiProviderBase;
import org.libreflock.asielib.network.Packet;

/**
 * @author Vexatos
 */
public class GuiProviderTapeDrive extends GuiProviderBase {

	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen makeGui(int guiID, PlayerEntity entityPlayer, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if(tileEntity instanceof TileTapeDrive) {
			final TileTapeDrive tile = (TileTapeDrive) tileEntity;
			return new GuiTapePlayer(new IGuiTapeDrive() {
				@Override
				public void setState(TapeDriveState.State state) {
					try {
						Packet packet = Computronics.packet.create(PacketType.TAPE_GUI_STATE.ordinal())
							.writeTileLocation(tile)
							.writeByte((byte) state.ordinal());
						Computronics.packet.sendToServer(packet);
						tile.switchState(state);
					} catch(Exception e) {
						//NO-OP
					}
				}

				@Override
				public TapeDriveState.State getState() {
					return tile.getEnumState();
				}

				@Override
				public boolean isLocked(Slot slot, int index, int button, ClickType type) {
					return false;
				}

				@Override
				public boolean shouldCheckHotbarKeys() {
					return true;
				}
			}, makeContainer(entityPlayer, tile));
		}
		return null;
	}

	@Override
	public Container makeContainer(int guiID, PlayerEntity entityPlayer, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
		if(tileEntity instanceof TileTapeDrive) {
			return makeContainer(entityPlayer, ((TileTapeDrive) tileEntity));
		}
		return null;
	}

	protected ContainerInventory makeContainer(PlayerEntity entityPlayer, TileTapeDrive tile) {
		return new ContainerTapeReader(tile, entityPlayer.inventory);
	}
}
