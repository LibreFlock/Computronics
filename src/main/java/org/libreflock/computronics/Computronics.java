package org.libreflock.computronics;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreflock.computronics.audio.DFPWMPlaybackManager;
import org.libreflock.computronics.audio.SoundCardPlaybackManager;
import org.libreflock.computronics.block.BlockAudioCable;
import org.libreflock.computronics.block.BlockCamera;
import org.libreflock.computronics.block.BlockChatBox;
import org.libreflock.computronics.block.BlockCipher;
import org.libreflock.computronics.block.BlockCipherAdvanced;
import org.libreflock.computronics.block.BlockColorfulLamp;
import org.libreflock.computronics.block.BlockIronNote;
import org.libreflock.computronics.block.BlockRadar;
import org.libreflock.computronics.block.BlockSpeaker;
import org.libreflock.computronics.block.BlockSpeechBox;
import org.libreflock.computronics.block.BlockTapeReader;
import org.libreflock.computronics.tape.StorageManager;
import org.libreflock.computronics.tape.TapeStorageEventHandler;
import org.libreflock.computronics.util.event.ServerTickHandler;
import org.libreflock.asielib.network.PacketHandler;

import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("computronics")
public class Computronics {

    // Directly reference a log4j logger.
    public static final Logger log = LogManager.getLogger();

    public static Computronics instance;

    public static StorageManager storage;
	public static TapeStorageEventHandler storageEventHandler;
	// public static ManagedGuiHandler gui;
	public static PacketHandler packet;
	public static ExecutorService rsaThreads;
	public static ServerTickHandler serverTickHandler;
	public DFPWMPlaybackManager audio;
	public int managerId;

    public static BlockIronNote ironNote;
	public static BlockTapeReader tapeReader;
	public static BlockAudioCable audioCable;
	public static BlockSpeaker speaker;
	public static BlockSpeechBox speechBox;
	public static BlockCamera camera;
	public static BlockChatBox chatBox;
	public static BlockCipher cipher;
	public static BlockCipherAdvanced cipher_advanced;
	public static BlockRadar radar;
	public static BlockColorfulLamp colorfulLamp;

	public SoundCardPlaybackManager soundCardAudio;
	public int soundCardManagerId;

    public Computronics() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // some preinit code


        log.info("HELLO FROM PREINIT");
        log.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        log.info("Got game settings {}", event.getMinecraftSupplier().get().options);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("computronics", "helloworld", () -> { log.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        log.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        log.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            log.info("HELLO from Register Block");
        }
    }
}
