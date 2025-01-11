package org.libreflock.asielib;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.Logger;
import org.libreflock.asielib.api.AsieLibAPI;
import org.libreflock.asielib.api.tool.IToolRegistry;
import org.libreflock.asielib.integration.Integration;
import org.libreflock.asielib.integration.tool.ToolProviders;
import org.libreflock.asielib.reference.Capabilities;
import org.libreflock.asielib.reference.Mods;
import org.libreflock.asielib.tweak.enchantment.EnchantmentTweak;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.stream.Collectors;

/*@Mod(modid = Mods.AsieLib, name = Mods.AsieLib_NAME, version = "@AL_VERSION@",
	dependencies = "required-after:forge@[14.21.1.2387,)")*/
@Mod("asielib")
public class AsieLibMod extends AsieLibAPI {

	public Configuration config;
	public static Random rand = new Random();
	public static Logger log;
	//public static PacketHandler packet;

	public static boolean ENABLE_DYNAMIC_ENERGY_CALCULATION;

	@Instance(value = Mods.AsieLib)
	public static AsieLibMod instance;

	//@SidedProxy(clientSide = "org.libreflock.asielib.ClientProxy", serverSide = "org.libreflock.asielib.CommonProxy")
	//public static CommonProxy proxy;
	// yes i know its deprecated shhhhh
	public static CommonProxy proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
	public AsieLibMod() {
		// Register the setup method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
		// Register the enqueueIMC method for modloading
		//FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
		// Register the processIMC method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
		// Register the doClientStuff method for modloading
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}
	private void setup(final FMLCommonSetupEvent event) {
		AsieLibAPI.instance = this;
		ToolProviders.registerToolProviders();
		log = event.getModLog();

		config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();

		MinecraftForge.EVENT_BUS.register(new AsieLibEvents());

		ENABLE_DYNAMIC_ENERGY_CALCULATION =
			config.getBoolean("enableDynamicEnergyUsageCalculation", "general", true, "If you want to disable dynamic generation of current/peak energy usage, use this.");

		if(System.getProperty("user.dir").contains(".asielauncher")) {
			log.info("Hey, you! Yes, you! Thanks for using AsieLauncher! ~asie");
		}
	}
	@SubscribeEvent
	public void init(FMLServerStartingEvent event) { // probably the wrong event ngl

		//packet = new PacketHandler(Mods.AsieLib, new NetworkHandlerClient(), null);

		if(config.get("enchantments", "usefulBaneOfArthropods", false,
			"Might make Bane Of Arthropods actually useful (Experimental)").getBoolean(false)) {
			EnchantmentTweak.registerBaneEnchantment(config.getInt("baneEnchantmentID", "enchantments", 244, 0, 255,
				"The enchantment ID for the better Bane Of Arthropods"));
			EnchantmentTweak tweak = new EnchantmentTweak();
			MinecraftForge.EVENT_BUS.register(tweak);
		}

		if(config.get("tweaks", "dyeItemNamesInAnvil", true).getBoolean(true)) {
			MinecraftForge.EVENT_BUS.register(new AnvilDyeTweak());
		}

		Capabilities.INSTANCE.init();
	}

	@SubscribeEvent
	public void postInit(FMLClientSetupEvent event) { // i have no idea
		// config.save();
	}

	/**
	 * Call this using {@link FMLInterModComms#sendMessage}.
	 * <p/>
	 * Example:
	 * FMLInterModComms.sendMessage("asielib", "addToolProvider", "com.example.examplemod.tool.ToolProviders.register")
	 * @see IToolRegistry
	 */
	@SuppressWarnings("unchecked")
	public void processIMC(InterModProcessEvent event) {
		ImmutableList<InterModComms.IMCMessage> messages = event.getIMCStream().map(m->m.getMessageSupplier().get()).
				collect(Collectors.toList()); // htf do i do this
		for(FMLInterModComms.IMCMessage message : messages) {
			if(message.key.equalsIgnoreCase("addtoolprovider") && message.isStringMessage()) {
				try {
					String methodString = message.getStringValue();
					String[] methodParts = methodString.split("\\.");
					String methodName = methodParts[methodParts.length - 1];
					String className = methodString.substring(0, methodString.length() - methodName.length() - 1);
					try {
						Class c = Class.forName(className);
						Method method = c.getDeclaredMethod(methodName, IToolRegistry.class);
						method.invoke(null, Integration.toolRegistry);
					} catch(ClassNotFoundException e) {
						log.warn("Could not find class " + className, e);
					} catch(NoSuchMethodException e) {
						log.warn("Could not find method " + methodString, e);
					} catch(Exception e) {
						log.warn("Exception while trying to call method " + methodString, e);
					}
				} catch(Exception e) {
					log.warn("Exception while trying to register a ToolProvider", e);
				}
			}
		}
	}
}
