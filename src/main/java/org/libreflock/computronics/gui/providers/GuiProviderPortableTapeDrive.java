package org.libreflock.computronics.gui.providers;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.gui.GuiTapePlayer;
import org.libreflock.computronics.gui.IGuiTapeDrive;
import org.libreflock.computronics.gui.container.ContainerPortableTapeDrive;
import org.libreflock.computronics.item.ItemPortableTapeDrive;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.computronics.tape.PortableDriveManager;
import org.libreflock.computronics.tape.PortableTapeDrive;
import org.libreflock.computronics.tile.TapeDriveState;
import org.libreflock.asielib.gui.container.ContainerInventory;
import org.libreflock.asielib.gui.managed.GuiProviderBase;
import org.libreflock.asielib.network.Packet;

/**
 * @author Vexatos
 */
public class GuiProviderPortableTapeDrive extends GuiProviderBase {

	@Override
	@OnlyIn(Dist.CLIENT)
	public ContainerScreen makeGui(int ID, PlayerEntity player, final World world, int x, int y, int z) {
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemPortableTapeDrive) {
			final PortableTapeDrive tapeDrive = PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote);
			return new GuiTapePlayer(new IGuiTapeDrive() {
				@Override
				public void setState(TapeDriveState.State state) {
					String id = PortableDriveManager.INSTANCE.getID(tapeDrive, world.isRemote);
					if(id != null) {
						try {
							Packet packet = Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
								.writeString(id)
								.writeByte((byte) state.ordinal());
							Computronics.packet.sendToServer(packet);
							tapeDrive.switchState(state);
						} catch(Exception e) {
							//NO-OP
						}
					}
				}

				@Override
				public TapeDriveState.State getState() {
					return tapeDrive.getEnumState();
				}

				@Override
				public boolean isLocked(Slot slot, int index, int button, ClickType type) {
					ItemStack slotstack = slot.getStack();
					return !slotstack.isEmpty() && ItemStack.areItemStacksEqual(tapeDrive.getSelf(), slotstack);
				}

				@Override
				public boolean shouldCheckHotbarKeys() {
					return false;
				}
			}, makeContainer(player, tapeDrive));
		}
		return null;
	}

	@Override
	public Container makeContainer(int ID, PlayerEntity player, World world, int x, int y, int z) {
		ItemStack stack = player.getHeldItemMainhand();
		if(!stack.isEmpty() && stack.getItem() instanceof ItemPortableTapeDrive) {
			return makeContainer(player, PortableDriveManager.INSTANCE.getOrCreate(stack, world.isRemote));
		}
		return null;
	}

	protected ContainerInventory makeContainer(PlayerEntity player, PortableTapeDrive tapeDrive) {
		return new ContainerPortableTapeDrive(tapeDrive, player.inventory);
	}
}
