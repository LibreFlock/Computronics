// package org.libreflock.computronics.oc.driver;

// import li.cil.oc.api.Network;
// import li.cil.oc.api.internal.Rotatable;
// import li.cil.oc.api.machine.Arguments;
// import li.cil.oc.api.machine.Callback;
// import li.cil.oc.api.machine.Context;
// import li.cil.oc.api.network.EnvironmentHost;
// import li.cil.oc.api.network.Message;
// import li.cil.oc.api.network.Node;
// import li.cil.oc.api.network.Visibility;
// import net.minecraft.nbt.CompoundNBT;
// import net.minecraft.tileentity.TileEntity;
// import net.minecraft.util.Direction;
// import net.minecraft.util.math.vector.Vector3d;
// import net.minecraft.world.World;
// import net.minecraft.world.server.ServerWorld;
// import net.minecraftforge.common.capabilities.Capability;
// import net.minecraftforge.common.capabilities.ICapabilityProvider;
// import net.minecraftforge.common.util.LazyOptional;

// // import net.minecraftforge.fml.common.Optional;
// import org.libreflock.computronics.Computronics;
// import org.libreflock.computronics.api.audio.AudioPacket;
// import org.libreflock.computronics.api.audio.AudioPacketDFPWM;
// import org.libreflock.computronics.api.audio.IAudioReceiver;
// import org.libreflock.computronics.api.audio.IAudioSource;
// import org.libreflock.computronics.audio.AudioUtils;
// import org.libreflock.computronics.audio.tts.TextToSpeech.ICanSpeak;
// import org.libreflock.computronics.integration.charset.audio.IntegrationCharsetAudio;
// import org.libreflock.computronics.reference.Config;
// import org.libreflock.computronics.reference.Mods;
// import org.libreflock.computronics.util.ColorUtils;
// import org.libreflock.computronics.util.OCUtils;
// import org.libreflock.asielib.util.internal.IColorable;

// import javax.annotation.Nullable;
// import java.io.ByteArrayInputStream;
// import java.io.IOException;
// import java.util.Arrays;

// import static org.libreflock.computronics.reference.Capabilities.AUDIO_RECEIVER_CAPABILITY;
// import static org.libreflock.computronics.reference.Capabilities.AUDIO_SOURCE_CAPABILITY;

// /**
//  * @author Vexatos
//  */
// public class RobotUpgradeSpeech extends ManagedEnvironmentWithComponentConnector implements IAudioSource, ICapabilityProvider, ICanSpeak {

// 	protected final EnvironmentHost host;

// 	public RobotUpgradeSpeech(EnvironmentHost host) {
// 		this.host = host;
// 		this.setNode(Network.newNode(this, Visibility.Neighbors).
// 			withComponent("speech").
// 			withConnector().
// 			create());
// 	}

// 	private final IAudioReceiver internalSpeaker = new IAudioReceiver() {
// 		@Override
// 		public boolean connectsAudio(Direction side) {
// 			return true;
// 		}

// 		@Override
// 		public World getSoundWorld() {
// 			return host.world();
// 		}

// 		@Override
// 		public Vector3d getSoundPos() {
// 			return getPos();
// 		}

// 		@Override
// 		public int getSoundDistance() {
// 			return Config.COMMON.TAPEDRIVE_DISTANCE.get();
// 		}

// 		@Override
// 		public void receivePacket(AudioPacket packet, @Nullable Direction direction) {
// 			packet.addReceiver(this);
// 		}

// 		@Override
// 		public String getID() {
// 			return host instanceof TileEntity ? AudioUtils.positionId(host.xPosition(), host.yPosition(), host.zPosition()) : "";
// 		}

// 	};

// 	protected Vector3d getPos() {
// 		return new Vector3d(host.xPosition(), host.yPosition(), host.zPosition());
// 	}

// 	@Override
// 	public boolean canUpdate() {
// 		return true;
// 	}

// 	@Override
// 	public void update() {
// 		super.update();
// 		AudioPacket pkt = null;
// 		long time = System.nanoTime();
// 		if((time - (250 * 1000000)) > lastCodecTime) {
// 			lastCodecTime += (250 * 1000000);
// 			pkt = createMusicPacket(this);
// 		}
// 		int receivers = 0;
// 		boolean sent = false;
// 		if(pkt != null) {
// 			if(host instanceof TileEntity) {
// 				if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
// 					int oldReceivers = receivers;
// 					receivers += IntegrationCharsetAudio.send(host.world(), ((TileEntity) host).getBlockPos(), pkt, 1.0F, true);
// 					if(receivers > oldReceivers) {
// 						sent = true;
// 					}
// 				}
// 			}
// 			if(!sent) {
// 				if(host instanceof TileEntity) {
// 					for(Direction dir : Direction.values()) {
// 						TileEntity tile = host.world().getBlockEntity(((TileEntity) host).getBlockPos().relative(dir));
// 						if(tile != null) {
// 							if(!tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).equals(LazyOptional.empty())) {
// 								IColorable hostCol = ColorUtils.getColorable((TileEntity) host, dir);
// 								IColorable targetCol = ColorUtils.getColorable(tile, dir.getOpposite());
// 								if(hostCol != null && targetCol != null && hostCol.canBeColored() && targetCol.canBeColored()
// 									&& !ColorUtils.isSameOrDefault(hostCol, targetCol)) {
// 									continue;
// 								}
// 								tile.getCapability(AUDIO_RECEIVER_CAPABILITY, dir.getOpposite()).receivePacket(pkt, dir.getOpposite());
// 								receivers++;
// 							}
// 						}
// 					}
// 				}
// 				if(receivers == 0) {
// 					internalSpeaker.receivePacket(pkt, null);
// 				}
// 				pkt.sendPacket();
// 			}
// 		}
// 	}

// 	protected boolean isValid = false;

// 	@Override
// 	public boolean isValid() {
// 		return isValid;
// 	}

// 	@Override
// 	public void onConnect(Node node) {
// 		super.onConnect(node);
// 		if(node == this.node()) {
// 			isValid = true;
// 		}
// 	}

// 	@Override
// 	public void onDisconnect(final Node node) {
// 		super.onDisconnect(node);
// 		if(node == this.node()) {
// 			isValid = false;
// 			stopTalking();
// 		}
// 	}

// 	@Override
// 	public void onMessage(Message message) {
// 		super.onMessage(message);
// 		if((message.name().equals("computer.stopped")
// 			|| message.name().equals("computer.started"))
// 			&& node().isNeighborOf(message.source())) {
// 			if(locked || storage != null) {
// 				stopTalking();
// 			}
// 			isValid = message.name().equals("computer.started");
// 		}
// 	}

// 	private long lastCodecTime;
// 	private int codecId = -1;
// 	protected int packetSize = 1500;
// 	protected int soundVolume = 127;
// 	private boolean locked = false;

// 	@Override
// 	public void startTalking(byte[] data) {
// 		if(host.world() instanceof ServerWorld) {
// 			return;
// 		}
// 		storage = new ByteArrayInputStream(data);
// 		codecId = Computronics.instance.audio.newPlayer();
// 		Computronics.instance.audio.getPlayer(codecId);
// 		lastCodecTime = System.nanoTime();
// 	}

// 	private void stopTalking() {
// 		if(host.world() != null && host.world() instanceof ServerWorld) {
// 			return;
// 		}
// 		AudioUtils.removePlayer(Computronics.instance.managerId, codecId);
// 		locked = false;
// 		storage = null;
// 	}

// 	private Object[] sendNewText(String text) throws IOException {
// 		// if(Computronics.tts != null) {
// 		// 	locked = true;
// 		// 	Computronics.tts.say(this, text);
// 		// } else {
// 		// 	return new Object[] { false, "text-to-speech system not available" };
// 		// }
// 		// return new Object[] { true };
// 		return new Object[] { false, "text-to-speech system not available" };
// 	}

// 	private ByteArrayInputStream storage;

// 	@Nullable
// 	private AudioPacket createMusicPacket(IAudioSource source) {
// 		if(storage == null) {
// 			return null;
// 		}
// 		byte[] pktData = new byte[packetSize];
// 		int amount = storage.read(pktData, 0, pktData.length); // read data into packet array

// 		if(amount > 0) {
// 			return new AudioPacketDFPWM(source, (byte) soundVolume, packetSize * 8 * 4, amount == packetSize ? pktData : Arrays.copyOf(pktData, amount));
// 		} else {
// 			stopTalking();
// 			return null;
// 		}
// 	}

// 	@Override
// 	public void loadData(CompoundNBT tag) {
// 		super.loadData(tag);
// 		if(this.soundVolume != 127) {
// 			tag.putByte("vo", (byte) this.soundVolume);
// 		}
// 	}

// 	@Override
// 	public void saveData(CompoundNBT tag) {
// 		super.saveData(tag);
// 		if(tag.contains("vo")) {
// 			this.soundVolume = tag.getByte("vo");
// 		} else {
// 			this.soundVolume = 127;
// 		}
// 	}

// 	public void setVolume(float volume) {
// 		if(volume < 0.0F) {
// 			volume = 0.0F;
// 		}
// 		if(volume > 1.0F) {
// 			volume = 1.0F;
// 		}
// 		this.soundVolume = (int) Math.floor(volume * 127.0F);
// 	}

// 	@Callback(doc = "function(text:string):boolean; Say the specified message. Returns true on success, false and an error message otherwise.")
// 	@Optional.Method(modid = Mods.OpenComputers)
// 	public Object[] say(Context context, Arguments args) {
// 		if(locked || storage != null) {
// 			return new Object[] { false, "already processing" };
// 		}
// 		String text = args.checkString(0);
// 		if(text.length() > Config.COMMON.TTS_MAX_LENGTH.get()) {
// 			return new Object[] { false, "text too long" };
// 		}
// 		try {
// 			return this.sendNewText(text);
// 		} catch(IOException e) {
// 			throw new IllegalArgumentException("could not send string");
// 		} catch(Exception e) {
// 			e.printStackTrace();
// 			throw e;
// 		}
// 	}

// 	@Callback(doc = "function():boolean; Stops the currently spoken phrase. Returns true on success, false and an error message otherwise.")
// 	@Optional.Method(modid = Mods.OpenComputers)
// 	public Object[] stop(Context context, Arguments args) {
// 		if(locked || storage != null) {
// 			stopTalking();
// 			return new Object[] { true };
// 		}
// 		return new Object[] { false, "not talking" };
// 	}

// 	@Callback(doc = "function():boolean; Returns true if the device is currently processing text.", direct = true)
// 	@Optional.Method(modid = Mods.OpenComputers)
// 	public Object[] isProcessing(Context context, Arguments args) {
// 		return new Object[] { locked || storage != null };
// 	}

// 	@Callback(doc = "function(speed:number); Sets the volume of the speech box. Needs to be beween 0 and 1")
// 	@Optional.Method(modid = Mods.OpenComputers)
// 	public Object[] setVolume(Context context, Arguments args) {
// 		this.setVolume((float) args.checkDouble(0));
// 		return new Object[] {};
// 	}

// 	@Override
// 	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
// 		facing = host instanceof Rotatable ? ((Rotatable) host).toGlobal(facing) : facing;
// 		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
// 			if(capability == IntegrationCharsetAudio.SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
// 				return true;
// 			}
// 		}
// 		return capability == AUDIO_SOURCE_CAPABILITY && facing != null && connectsAudio(facing);
// 	}

// 	private Object charsetAudioSource;

// 	@Nullable
// 	@Override
// 	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
// 		facing = host instanceof Rotatable ? ((Rotatable) host).toGlobal(facing) : facing;
// 		if(Mods.API.hasAPI(Mods.API.CharsetAudio)) {
// 			if(capability == IntegrationCharsetAudio.SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
// 				if(charsetAudioSource == null) {
// 					charsetAudioSource = new org.libreflock.charset.api.audio.IAudioSource() {
// 					};
// 				}
// 				return IntegrationCharsetAudio.SOURCE_CAPABILITY.cast((org.libreflock.charset.api.audio.IAudioSource) charsetAudioSource);
// 			}
// 		}
// 		if(capability == AUDIO_SOURCE_CAPABILITY && facing != null && connectsAudio(facing)) {
// 			return AUDIO_SOURCE_CAPABILITY.cast(this);
// 		}
// 		return null;
// 	}

// 	@Override
// 	public boolean connectsAudio(Direction side) {
// 		if(host instanceof TileEntity) {
// 			IColorable hostCol = ColorUtils.getColorable((TileEntity) host, side);
// 			IColorable targetCol = ColorUtils.getColorable(host.world().getTileEntity(((TileEntity) host).getPos().offset(side)), side.getOpposite());
// 			if(hostCol != null && targetCol != null && hostCol.canBeColored() && targetCol.canBeColored()) {
// 				return ColorUtils.isSameOrDefault(hostCol, targetCol);
// 			}
// 		}
// 		return true;
// 	}

// 	@Override
// 	public int getSourceId() {
// 		return codecId;
// 	}

// 	@Override
// 	@Optional.Method(modid = Mods.OpenComputers)
// 	protected OCUtils.Device deviceInfo() {
// 		return new OCUtils.Device(
// 			DeviceClass.Multimedia,
// 			"Text-To-Speech Interface",
// 			OCUtils.Vendors.DFKI,
// 			"Mary"
// 		);
// 	}
// }
