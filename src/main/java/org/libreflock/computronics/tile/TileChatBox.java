package org.libreflock.computronics.tile;

// import dan200.computercraft.api.lua.ILuaContext;
// import dan200.computercraft.api.lua.LuaException;
// import dan200.computercraft.api.peripheral.IComputerAccess;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ServerChatEvent;
// import net.minecraftforge.fml.common.Optional;
import org.libreflock.computronics.api.chat.ChatAPI;
import org.libreflock.computronics.api.chat.IChatListener;
import org.libreflock.computronics.block.BlockChatBox;
import org.libreflock.computronics.reference.Config;
import org.libreflock.computronics.reference.Mods;
import org.libreflock.computronics.util.ChatBoxUtils;
import org.libreflock.computronics.util.OCUtils;

public class TileChatBox extends TileEntityPeripheralBase implements IChatListener { // TODO: figure out ITickable

	private int distance;
	private boolean hasDistance = false;
	private int ticksUntilOff = 0;
	private boolean mustRefresh = false;
	private String name = "";

	public TileChatBox() {
		super("chat_box");
		distance = Config.COMMON.CHATBOX_DISTANCE.get();
	}

	@Override
	public int requestCurrentRedstoneValue(Direction side) {
		return (ticksUntilOff > 0) ? 15 : 0;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState) {
		return oldState != newState;
	}

	public boolean isCreative() {
		BlockPos pos = getBlockPos();
		if(Config.COMMON.CHATBOX_CREATIVE.get() && getLevel() != null && getLevel().isLoaded(pos)) {
			BlockState state = getLevel().getBlockState(pos);
			return state.getValue(BlockChatBox.CREATIVE);// TODO: figure this out
		}
		return false;
	}

	@Override
	public void update() {
		super.update();
		if(Config.COMMON.REDSTONE_REFRESH.get() && ticksUntilOff > 0) {
			ticksUntilOff--;
			if(ticksUntilOff == 0 || mustRefresh) {
				this.getLevel().notifyNeighborsOfStateChange(getBlockPos(), this.getBlockState(), true); // TODO: figure this out, useful
			}
		}
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int dist) {
		if(dist > 32767) {
			dist = 32767;
		}

		if(!isCreative()) {
			this.distance = Math.min(Config.COMMON.CHATBOX_DISTANCE.get(), dist);
		} else {
			this.distance = dist;
		}
		this.hasDistance = true;
		if(this.distance < 0) {
			this.distance = Config.COMMON.CHATBOX_DISTANCE.get();
			this.hasDistance = false;
		}
	}

	@Override
	public void receiveChatMessage(ServerChatEvent event) {
		if(!getLevel().isLoaded(getBlockPos())) {
			return;
		}
		if(!Config.COMMON.CHATBOX_MAGIC.get() && !isCreative() && (event.getPlayer().getLevel() != this.getLevel() || event.getPlayer().position().distanceToSqr(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()) > distance * distance)) {
			return;
		}

		if(Config.COMMON.REDSTONE_REFRESH.get()) {
			ticksUntilOff = 5;
			mustRefresh = true;
		}
		if(Mods.isLoaded(Mods.OpenComputers)) {
			eventOC(event);
		}
		// if(Mods.isLoaded(Mods.ComputerCraft)) {
		// 	eventCC(event);
		// }
	}

	@Override
	public void validate() {
		super.validate();
		ChatAPI.registry.registerChatListener(this);
	}

	@Override
	public void invalidate() {
		super.invalidate();
		ChatAPI.registry.unregisterChatListener(this);
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		ChatAPI.registry.unregisterChatListener(this);
	}

	// @Optional.Method(modid = Mods.OpenComputers)
	public void eventOC(ServerChatEvent event) {
		if(node() != null) {
			node().sendToReachable("computer.signal", "chat_message", event.getUsername(), event.getMessage());
		}
	}

	// @Optional.Method(modid = Mods.ComputerCraft)
	// public void eventCC(ServerChatEvent event) {
	// 	if(attachedComputersCC != null) {
	// 		for(IComputerAccess computer : attachedComputersCC) {
	// 			computer.queueEvent("chat_message", new Object[] {
	// 				computer.getAttachmentName(),
	// 				event.getUsername(), event.getMessage()
	// 			});
	// 		}
	// 	}
	// }

	@Override
	// @Optional.Method(modid = Mods.OpenComputers)
	protected OCUtils.Device deviceInfo() {
		return new OCUtils.Device(
			DeviceClass.Multimedia,
			"Chat interface",
			OCUtils.Vendors.NSA,
			"[CLASSIFIED]"
		);
	}
	// OpenComputers API

	@Callback(doc = "function(text:string [, distance:number]):boolean; "
		+ "Makes the chat box say some text with the currently set or the specified distance. Returns true on success")
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] say(Context context, Arguments args) {
		int d = distance;
		if(args.count() >= 1) {
			boolean isCreative = isCreative();
			boolean hasDistance = this.hasDistance;
			if(args.isInteger(1)) {
				d = isCreative ? args.checkInteger(1) : Math.min(Config.COMMON.CHATBOX_DISTANCE.get(), args.checkInteger(1));
				if(d <= 0) {
					d = distance;
				}
				hasDistance = true;
			}
			if(args.isString(0)) {
				ChatBoxUtils.sendChatMessage(this, d, name.length() > 0 ? name : Config.COMMON.CHATBOX_PREFIX.get(), args.checkString(0), !hasDistance && (Config.COMMON.CHATBOX_MAGIC.get() || isCreative));
				return new Object[] { true };
			}
		}
		return new Object[] { false };
	}

	@Callback(doc = "function():number; Returns the chat distance the chat box is currently set to", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] getDistance(Context context, Arguments args) {
		return new Object[] { distance };
	}

	@Callback(doc = "function(distance:number):number; Sets the distance of the chat box. Returns the new distance", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setDistance(Context context, Arguments args) {
		setDistance(args.checkInteger(0));
		return new Object[] { distance };
	}

	@Callback(doc = "function():string; Returns the name of the chat box", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] getName(Context context, Arguments args) {
		return new Object[] { name };
	}

	@Callback(doc = "function(name:string):string; Sets the name of the chat box. Returns the new name", direct = true)
	// @Optional.Method(modid = Mods.OpenComputers)
	public Object[] setName(Context context, Arguments args) {
		this.name = args.checkString(0);
		return new Object[] { this.name };
	}

	@Override
	public boolean canBeColored() {
		return !isCreative() && super.canBeColored();
	}

	@Override
	public int getColor() {
		int color = super.getColor();
		if(isCreative()) {
			return 0xFF60FF;
		}
		return color;
	}

	@Override
	public void setColor(int color) {
		if(isCreative()) {
			super.setColor(0xFF60FF);
		} else {
			super.setColor(color);
		}
	}

	@Override
	public void readFromNBT(final CompoundNBT nbt) {
		super.readFromNBT(nbt);
		if(nbt.contains("d")) {
			this.distance = nbt.getShort("d");
		}
		if(nbt.contains("hd")) {
			this.hasDistance = nbt.getBoolean("hd");
		}
		if(nbt.contains("n")) {
			this.name = nbt.getString("n");
		}
	}

	@Override
	public CompoundNBT writeToNBT(final CompoundNBT nbt) {
		super.writeToNBT(nbt);
		nbt.putShort("d", (short) this.distance);
		nbt.putBoolean("hd", this.hasDistance);
		if(name.length() > 0) {
			nbt.putString("n", this.name);
		}
		return nbt;
	}

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public String[] getMethodNames() {
	// 	return new String[] { "say", "getDistance", "setDistance", "getName", "setName" };
	// }

	// @Override
	// @Optional.Method(modid = Mods.ComputerCraft)
	// public Object[] callMethod(IComputerAccess computer, ILuaContext context,
	// 	int method, Object[] arguments) throws LuaException,
	// 	InterruptedException {
	// 	switch(method) {
	// 		case 0: { // say
	// 			if(arguments.length >= 1 && arguments[0] instanceof String) {
	// 				int d = distance;
	// 				boolean hasDistance = this.hasDistance;
	// 				boolean isCreative = isCreative();
	// 				if(arguments.length >= 2 && arguments[1] instanceof Double) {
	// 					d = isCreative ? ((Double) arguments[1]).intValue() : Math.min(Config.CHATBOX_DISTANCE, ((Double) arguments[1]).intValue());
	// 					if(d <= 0) {
	// 						d = distance;
	// 					}
	// 					hasDistance = true;
	// 				}
	// 				ChatBoxUtils.sendChatMessage(this, d, name.length() > 0 ? name : Config.CHATBOX_PREFIX, ((String) arguments[0]), !hasDistance && (Config.CHATBOX_MAGIC || isCreative));
	// 				return new Object[] { true };
	// 			}
	// 			return new Object[] { false };
	// 		}
	// 		case 1: { // getDistance
	// 			return new Object[] { distance };
	// 		}
	// 		case 2: { // setDistance
	// 			if(arguments.length == 1 && arguments[0] instanceof Double) {
	// 				setDistance(((Double) arguments[0]).intValue());
	// 				return new Object[] { distance };
	// 			}
	// 			throw new LuaException("first argument needs to be a number");
	// 		}
	// 		case 3: { // getName
	// 			return new Object[] { name };
	// 		}
	// 		case 4: { // setName
	// 			if(arguments.length == 1 && arguments[0] instanceof String) {
	// 				this.name = (String) arguments[0];
	// 				return new Object[] { this.name };
	// 			}
	// 			throw new LuaException("first argument needs to be a string");
	// 		}
	// 	}
	// 	return null;
	// }
}
