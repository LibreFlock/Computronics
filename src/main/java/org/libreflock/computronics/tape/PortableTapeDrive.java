package org.libreflock.computronics.tape;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.libreflock.computronics.Computronics;
import org.libreflock.computronics.api.audio.AudioPacket;
import org.libreflock.computronics.api.audio.IAudioReceiver;
import org.libreflock.computronics.api.audio.IAudioSource;
import org.libreflock.computronics.api.tape.IItemTapeStorage;
import org.libreflock.computronics.audio.MachineSound;
import org.libreflock.computronics.network.PacketType;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.reference.Sounds;
import org.libreflock.computronics.tile.TapeDriveState;
import org.libreflock.asielib.network.Packet;

import javax.annotation.Nullable;

/**
 * @author Vexatos
 */
public class PortableTapeDrive implements IAudioSource {

	protected World world;
	protected Vector3d pos;
	protected Entity carrier;
	protected ItemStack self;
	protected int time = 0;

	private String storageName = "";
	private TapeDriveState state = new TapeDriveState();
	private ItemStack inventory = ItemStack.EMPTY;

	public PortableTapeDrive() {
	}

	public void updateCarrier(Entity carrier, ItemStack self) {
		this.world = carrier.world;
		this.pos = carrier.getPositionVector();
		this.carrier = carrier;
		this.self = self;
	}

	public TapeDriveState.State getEnumState() {
		return this.state.getState();
	}

	public void switchState(TapeDriveState.State s) {
		//System.out.println("Switchy switch to " + s.name());
		if(this.getEnumState() != s) {
			this.state.switchState(world, s);
			updateState();
		}
	}

	public void resetTime() {
		this.time = 0;
	}

	public void update() {
		if(world.isRemote) {
			updateSound();
		}
		TapeDriveState.State st = getEnumState();
		AudioPacket pkt = state.update(this, world);
		if(pkt != null) {
			internalSpeaker.receivePacket(pkt, null);

			pkt.sendPacket();
		}
		if(!world.isRemote && st != getEnumState()) {
			updateState();
		}
	}

	public CompoundNBT getTag() {
		CompoundNBT tag = self.getTagCompound();
		if(tag == null) {
			tag = new CompoundNBT();
			self.putCompound(tag);
		}
		return tag;
	}

	public ItemStack getSelf() {
		return self;
	}

	protected void updateState() {
		if(world.isRemote) {
			return;
		}

		save(getTag());
		sendState();
	}

	protected void sendState() {
		String id = PortableDriveManager.INSTANCE.getID(this, world.isRemote);
		if(id != null) {
			try {
				Packet packet = Computronics.packet.create(PacketType.PORTABLE_TAPE_STATE.ordinal())
					.writeString(id)
					.writeByte((byte) state.getState().ordinal())
					.writeInt(getSourceId());
				//.writeByte((byte)soundVolume);
				Computronics.packet.sendToAllAround(packet, carrier, 64.0D);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private MachineSound sound;

	private ResourceLocation soundRes = new ResourceLocation(Mods.Computronics, "tape_rewind");

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
	protected void updateSound() {
		if(shouldPlaySound()) {
			if(sound == null) {
				sound = new MachineSound(soundRes, (float) pos.x, (float) pos.y,  (float) pos.z, getVolume(), getPitch(), shouldRepeat()) {
					@Override
					public void update() {
						this.xPosF = (float) PortableTapeDrive.this.pos.x;
						this.yPosF = (float) PortableTapeDrive.this.pos.y;
						this.zPosF = (float) PortableTapeDrive.this.pos.z;
					}
				};
				FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
			}
		} else if(sound != null) {
			sound.endPlaying();
			sound = null;
		}
	}

	public boolean shouldPlaySound() {
		switch(getEnumState()) {
			case REWINDING:
			case FORWARDING:
				return true;
			default:
				return false;
		}
	}

	// Storage handling

	private void loadStorage() {
		if(world != null && world.isRemote) {
			return;
		}

		if(state.getStorage() != null) {
			unloadStorage();
		}
		ItemStack stack = this.inventory;
		if(!stack.isEmpty()) {
			// Get Storage.
			Item item = stack.getItem();
			if(item instanceof IItemTapeStorage) {
				state.setStorage(((IItemTapeStorage) item).getStorage(stack));
			}

			// Get possible label.
			if(stack.getTagCompound() != null) {
				CompoundNBT tag = stack.getTagCompound();
				storageName = tag.contains("label") ? tag.getString("label") : "";
			} else {
				storageName = "";
			}
		}
	}

	public void saveStorage() {
		unloadStorage();
	}

	private void unloadStorage() {
		if(world.isRemote || state.getStorage() == null) {
			return;
		}

		switchState(TapeDriveState.State.STOPPED);
		try {
			state.getStorage().onStorageUnload();
		} catch(Exception e) {
			e.printStackTrace();
		}
		state.setStorage(null);
	}

	public void onInvUpdate() {
		if(this.inventory.isEmpty()) {
			if(state.getStorage() != null) { // Tape was inserted
				// Play eject sound
				world.playSound(null, pos.x, pos.y, pos.z, Sounds.TAPE_EJECT.event, SoundCategory.BLOCKS, 1, 0);
			}
			unloadStorage();
		} else {
			loadStorage();
			if(this.inventory.getItem() instanceof IItemTapeStorage) {
				// Play insert sound
				world.playSound(null, pos.x, pos.y, pos.z, Sounds.TAPE_INSERT.event, SoundCategory.BLOCKS, 1, 0);
			}
		}
		save(getTag());
	}

	protected int clientId = -1;

	public void load(CompoundNBT tag) {
		if(tag.contains("inv")) {
			this.inventory = new ItemStack(tag.getCompound("inv"));
		}
		if(tag.contains("state")) {
			this.state.setState(TapeDriveState.State.VALUES[tag.getByte("state")]);
		}
		if(tag.contains("sp")) {
			this.state.packetSize = tag.getShort("sp");
		}
		if(tag.contains("vo")) {
			this.state.soundVolume = tag.getByte("vo");
		} else {
			this.state.soundVolume = 127;
		}
		if(tag.contains("cId")) {
			this.clientId = tag.getInt("cId");
		}
		loadStorage();
	}

	public void save(CompoundNBT tag) {
		CompoundNBT inv = new CompoundNBT();
		if(!inventory.isEmpty()) {
			inventory.writeToNBT(inv);
		}
		tag.put("inv", inv);
		tag.putShort("sp", (short) this.state.packetSize);
		tag.putByte("state", (byte) this.state.getState().ordinal());
		if(this.state.soundVolume != 127) {
			tag.putByte("vo", (byte) this.state.soundVolume);
		}
		tag.putInt("cId", clientId);
	}

	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
		@Override
		public boolean connectsAudio(Direction side) {
			return false;
		}

		@Override
		public World getSoundWorld() {
			return world;
		}

		@Override
		public Vector3d getSoundPos() {
			return pos;
		}

		@Override
		public int getSoundDistance() {
			return Config.PORTABLE_TAPEDRIVE_DISTANCE;
		}

		@Override
		public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
			packet.addReceiver(this);
		}

		@Override
		public String getID() {
			return ""; // Not needed since there is always only this one receiver.
		}

	};

	@Override
	public int getSourceId() {
		return state.getId();
	}

	public int getSourceIdClient() {
		return clientId;
	}

	public void setSourceIdClient(int clientId) {
		this.clientId = clientId;
	}

	@Override
	public boolean connectsAudio(Direction side) {
		return true;
	}

	public final IInventory fakeInventory = new IInventory() {
		@Override
		public String getName() {
			return "portabletapedrive.inventory";
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}

		@Override
		public ITextComponent getDisplayName() {
			return new TranslationTextComponent(getName());
		}

		@Override
		public int getSizeInventory() {
			return 1;
		}

		@Override
		public boolean isEmpty() {
			return inventory.isEmpty();
		}

		@Override
		public int getInventoryStackLimit() {
			return 1;
		}

		@Override
		public boolean isItemValidForSlot(int slot, ItemStack stack) {
			return false;
		}

		@Override
		public int getField(int id) {
			return 0;
		}

		@Override
		public void setField(int id, int value) {

		}

		@Override
		public int getFieldCount() {
			return 0;
		}

		@Override
		public void clear() {

		}

		@Override
		public ItemStack getStackInSlot(int slot) {
			if(slot != 0) {
				return ItemStack.EMPTY;
			}
			return PortableTapeDrive.this.inventory;
		}

		@Override
		public ItemStack decrStackSize(int slot, int amount) {
			if(slot != 0) {
				return ItemStack.EMPTY;
			}
			if(!PortableTapeDrive.this.inventory.isEmpty()) {
				ItemStack stack;
				if(PortableTapeDrive.this.inventory.getCount() <= amount) {
					stack = PortableTapeDrive.this.inventory;
					PortableTapeDrive.this.inventory = ItemStack.EMPTY;
					PortableTapeDrive.this.onInvUpdate();
					return stack;
				} else {
					stack = PortableTapeDrive.this.inventory.splitStack(amount);

					if(PortableTapeDrive.this.inventory.getCount() == 0) {
						PortableTapeDrive.this.inventory = ItemStack.EMPTY;
					}

					PortableTapeDrive.this.onInvUpdate();

					return stack;
				}
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack removeStackFromSlot(int slot) {
			ItemStack stack = getStackInSlot(slot);
			if(stack.isEmpty()) {
				return ItemStack.EMPTY;
			}
			PortableTapeDrive.this.inventory = ItemStack.EMPTY;
			PortableTapeDrive.this.onInvUpdate();
			return stack;
		}

		@Override
		public void setInventorySlotContents(int slot, ItemStack stack) {
			if(slot != 0) {
				return;
			}
			PortableTapeDrive.this.inventory = stack;
			PortableTapeDrive.this.onInvUpdate();
		}

		@Override
		public boolean isUsableByPlayer(PlayerEntity player) {
			return true;
		}

		@Override
		public void openInventory(PlayerEntity player) {

		}

		@Override
		public void closeInventory(PlayerEntity player) {

		}

		@Override
		public void setChanged() {

		}
	};
}
