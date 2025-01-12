package org.libreflock.computronics.reference;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;
import org.libreflock.asielib.util.EnergyConverter;
// import org.libreflock.computronics.Computronics;
// import org.libreflock.asielib.util.EnergyConverter;

public class Config
{
	public static class Common
	{
		// private static final int defaultInt1 = 37;
		// private static final boolean defaultBool1 = true;

		//public final ConfigValue<Integer> Int1;
		//public final ConfigValue<Boolean> Bool1;

		public final ConfigValue<Integer> CHATBOX_DISTANCE; // = 40;
		public final ConfigValue<Boolean> CHATBOX_MAGIC; // = false;
		public final ConfigValue<Integer> CAMERA_DISTANCE;
		public final ConfigValue<Integer> TAPEDRIVE_DISTANCE; // = 24;
		public final ConfigValue<Integer> TAPEDRIVE_BUFFER_MS; // = 750;
		public final ConfigValue<Integer> PORTABLE_TAPEDRIVE_DISTANCE; // = 8;
		public final ConfigValue<Integer> RADAR_RANGE; // = 8;
		public final ConfigValue<Integer> FX_RANGE; // = 256;
		public final ConfigValue<Boolean> RADAR_ONLY_DISTANCE; // = true;
		public final ConfigValue<Boolean> CIPHER_CAN_LOCK; // = true;
		public final ConfigValue<Double> CIPHER_ENERGY_STORAGE; // = 1600.0;
		public final ConfigValue<Double> CIPHER_KEY_CONSUMPTION; // = 1600.0;
		public final ConfigValue<Double> CIPHER_WORK_CONSUMPTION; // = 16.0;
		public final ConfigValue<Double> RADAR_ENERGY_COST_OC; // = 5.0;
		// completely unused?
		//public final ConfigValue<Double> RADAR_CC_TIME; // = 0.5;
		public final ConfigValue<Double> FX_ENERGY_COST; // = 0.2;
		public final ConfigValue<Double> BEEP_ENERGY_COST; // = 1.0;
		public final ConfigValue<Double> SOUND_CARD_ENERGY_COST; // = 1.0;
		public final ConfigValue<Double> SPOOFING_ENERGY_COST; // = 0.2;
		public final ConfigValue<Double> COLORFUL_UPGRADE_COLOR_CHANGE_COST; // = 0.2;
		public final ConfigValue<Double> LIGHT_BOARD_COLOR_CHANGE_COST; // = 0.2;
		public final ConfigValue<Double> LIGHT_BOARD_COLOR_MAINTENANCE_COST; // = 0.02;
		public final ConfigValue<Double> BOOM_BOARD_MAINTENANCE_COST; // = 0.02;
		public final ConfigValue<Double> RACK_CAPACITOR_CAPACITY; // = 7500;
		public final ConfigValue<Double> SWITCH_BOARD_MAINTENANCE_COST; // = 0.02;
		public final ConfigValue<String> CHATBOX_PREFIX; // = "ChatBox";
		//public final ConfigValue<Double> LOCOMOTIVE_RELAY_RANGE; // = 128.0;
		//public final ConfigValue<Double> LOCOMOTIVE_RELAY_BASE_POWER; // = 20.0;
		//public final ConfigValue<Boolean> LOCOMOTIVE_RELAY_CONSUME_CHARGE; // = true;
		// public static boolean TICKET_MACHINE_CONSUME_RF = true;
		// public static boolean GREGTECH_RECIPES = false;
		// public static boolean NON_OC_RECIPES = false;
		// public static boolean FORESTRY_BEES = true;
		// public static boolean BUILDCRAFT_STATION = true;

		public final ConfigValue<Integer> SOUND_SAMPLE_RATE; // = 44100;
		public final ForgeConfigSpec.IntValue SOUND_VOLUME; // = 32;
		public final ConfigValue<Integer> SOUND_RADIUS; // = 24;
		// public static ConfigValue<Integer> SOUND_CARD_MAX_DELAY // = 5000; // TODO
		// public static ConfigValue<Integer> SOUND_CARD_QUEUE_SIZE // = 1024; // TODO
		//public static ConfigValue<Integer> SOUND_CARD_CHANNEL_COUNT // = 8; // TODO

		// public static boolean TTS_ENABLED;
		// public static int TTS_MAX_LENGTH = 300; // TODO

		// TERRIBLE SOLUTION FOR TODO STUFF
		public static int SOUND_CARD_MAX_DELAY = 5000;// = 5000; // TODO
		public static int SOUND_CARD_QUEUE_SIZE = 1024; // = 1024; // TODO
		public static int SOUND_CARD_CHANNEL_COUNT = 8; // = 8; // TODO


		public final ConfigValue<Boolean> OC_UPGRADE_CAMERA;
		public final ConfigValue<Boolean> OC_UPGRADE_CHATBOX;
		public final ConfigValue<Boolean> OC_UPGRADE_RADAR;
		public final ConfigValue<Boolean> OC_CARD_FX;
		public final ConfigValue<Boolean> OC_CARD_SPOOF;
		public final ConfigValue<Boolean> OC_CARD_BEEP;
		public final ConfigValue<Boolean> OC_CARD_BOOM;
		public final ConfigValue<Boolean> OC_UPGRADE_COLORFUL;
		public final ConfigValue<Boolean> OC_CARD_NOISE;
		public final ConfigValue<Boolean> OC_CARD_SOUND;
		public final ConfigValue<Boolean> OC_BOARD_LIGHT;
		public final ConfigValue<Boolean> OC_BOARD_BOOM;
		public final ConfigValue<Boolean> OC_BOARD_CAPACITOR;
		public final ConfigValue<Boolean> OC_BOARD_SWITCH;
		public final ConfigValue<Boolean> OC_UPGRADE_SPEECH;

		public final ConfigValue<Boolean> OC_MAGICAL_MEMORY;

		// public static boolean CC_OPEN_MULTI_PERIPHERAL = true;
		// public static boolean CC_ALL_MULTI_PERIPHERALS = true;
		// public static boolean CC_ALWAYS_FIRST = true;

		// public static boolean TIS3D_MODULE_COLORFUL = true;
		// public static boolean TIS3D_MODULE_TAPE_READER = true;
		// public static boolean TIS3D_MODULE_BOOM = true;

		public final ConfigValue<String> TAPE_LENGTHS;
		public final ConfigValue<Boolean> REDSTONE_REFRESH, CHATBOX_CREATIVE;
		// where the fuck did this variable come from
		//public final ConfigValue<Boolean> MUST_UPDATE_TILE_ENTITIES; // = false;

		public Common(ForgeConfigSpec.Builder builder)
		{
			/*builder.push("category1");
			this.Int1 = builder.comment("This is a nice description of your option. Make it a lot longer than this. Max is 60, default is 37. Enjoy...")
					.worldRestart()
					.defineInRange("Short but readable name", defaultInt1, 1, 60);
			this.Bool1 = builder.comment("asdasd as asd asd asd asdas aasd as asd asd. asd as asd asd. asdasdad asd.")
					.define("Short but readable name 2", defaultBool1);*/
			builder.push("Camera");
			this.CAMERA_DISTANCE = builder.comment("The maximum camera distance, in blocks.")
							.defineInRange("Distance", 32, 16, 256);

			builder.pop();

			builder.push("Chat Box");
			this.CHATBOX_CREATIVE = builder.comment("Enable Creative Chat Boxes.")
					.define("Chatbox Creative", true);
			this.CHATBOX_DISTANCE = builder.comment("The maximum chat box distance, in blocks.")
					.defineInRange("Chatbox Distance", 40, 4, 32767);
			this.CHATBOX_MAGIC = builder.comment("Make the normal Chat Box have no range limit and work interdimensionally.")
					.define("Chatbox make Magical", false);
			this.CHATBOX_PREFIX = builder.comment("The Chat Box's default prefix.")
					.define("Chatbox Prefix", "ChatBox");
			builder.pop();

			builder.push("Cipher Block");
			this.CIPHER_CAN_LOCK = builder.comment("Decides whether Cipher Blocks can or cannot be locked.")
					.define("Can lock", true);
			builder.pop();

			builder.push("Sound (client)");
			this.SOUND_SAMPLE_RATE = builder.comment("The sample rate used for generating sounds. Modify at your own risk.")
					.defineInRange("Sound sample rate", 44100, 0, Integer.MAX_VALUE); // wait why is the minimum 0
			this.SOUND_VOLUME = builder.comment("The base volume of generated sounds.") // sacrifices had to be made, type changed to Int from Byte
					.defineInRange("Sound volume", 127, 0, Byte.MAX_VALUE);
			this.SOUND_RADIUS = builder.comment("The radius in which generated sounds can be heard.")
					.defineInRange("Sound radius", 24, 0, 64);
			builder.pop();

			builder.push("Advanced Cipher Block"); // i have no idea how to implement the energy conversion properly
			this.CIPHER_ENERGY_STORAGE = builder.comment("How much energy the Advanced Chipher Block can store (in OC energy units)")
					.defineInRange("Cipher Energy Storage", convertRFtoOC(16000.0f), 0.0f, convertRFtoOC(100000.0F));
			this.CIPHER_KEY_CONSUMPTION = builder.comment("How much energy the Advanced Cipher Block should consume for creating a key set (in OC energy units)")
					.defineInRange("Cipher Key Consumption", convertRFtoOC(16000.0f), 0.0f, convertRFtoOC(100000.0F));
			this.CIPHER_WORK_CONSUMPTION = builder.comment("How much base energy the Advanced Cipher Block should consume per encryption/decryption task. It will consume this value + 2*(number of characters in message) (in OC energy units)")
					.defineInRange("Cipher Work Consumption", convertRFtoOC(160.0f), 0.0f, convertRFtoOC(100000.0f));
			builder.pop();

			builder.push("Enabled OpenComputers items");
			this.OC_UPGRADE_CAMERA = builder.comment("Enable Camera Upgrade")
					.define("Camera Upgrade", true);
			this.OC_UPGRADE_CHATBOX = builder.comment("Enable Chatbox Upgrade")
					.define("Chatbox Upgrade", true);
			this.OC_UPGRADE_RADAR = builder.comment("Enable Radar Upgrade")
					.define("Radar Upgrade", true);
			this.OC_CARD_FX = builder.comment("Enable Particle Card")
					.define("Particle Card", true);
			this.OC_CARD_SPOOF = builder.comment("Enable Spoofing Card")
					.define("Spoofing Card", true);
			this.OC_CARD_BEEP = builder.comment("Enable Beep Card")
					.define("Beep Card", true);
			this.OC_CARD_BOOM = builder.comment("Enable Boom Card")
					.define("Boom Card", true); // i assume this is the self-destructor card?
			this.OC_UPGRADE_COLORFUL = builder.comment("Enable Colorful Upgrade")
					.define("Colorful Upgrade", true);
			this.OC_CARD_NOISE = builder.comment("Enable Noise Card")
					.define("Noise Card", true);
			this.OC_CARD_SOUND = builder.comment("Enable Sound Card")
					.define("Sound Card", true);
			this.OC_BOARD_LIGHT = builder.comment("Enable Light Board")
					.define("Light Board", true);
			this.OC_BOARD_BOOM = builder.comment("Enable Boom Board")
					.define("Boom Board", true);
			this.OC_BOARD_CAPACITOR = builder.comment("Enable Rack Capacitor")
					.define("Rack Capacitor", true);
			this.OC_BOARD_SWITCH = builder.comment("Enable Switch Board")
					.define("Switch Board", true);
			this.OC_UPGRADE_SPEECH = builder.comment("Enable Speech Upgrade")
					.define("Speech Upgrade", true);

			this.OC_MAGICAL_MEMORY = builder.comment("Enable Magical Memory")
					.define("Magical Memory", true);
			builder.pop();

			builder.push("Power");
			this.FX_ENERGY_COST = builder.comment("How much energy 1 particle emission should take. Multiplied by the distance to the target.")
					.defineInRange("Particle Card cost per particle", convertRFtoOC(2.0f), 0.0f, convertRFtoOC(10000.0f));
			this.SPOOFING_ENERGY_COST = builder.comment("How much energy sending one spoofed message should take")
					.defineInRange("Spoofing Card cost per message", convertRFtoOC(2.0f), 0.0f, convertRFtoOC(10000.0f));
			this.BEEP_ENERGY_COST = builder.comment("How much energy a single beep will cost for 1 second")
					.defineInRange("Beep Card cost per sound", convertRFtoOC(10.0f), 0.0f, convertRFtoOC(10000.0f));
			this.SOUND_CARD_ENERGY_COST = builder.comment("How much energy the sound card will consume per second of processed sound.")
					.defineInRange("Sound Card cost per second", convertRFtoOC(10.0f), 0.0f, convertRFtoOC(10000.0f));
			this.COLORFUL_UPGRADE_COLOR_CHANGE_COST = builder.comment("How much energy changing the color of the Colorful Upgrade will cost")
					.defineInRange("Colorful Upgrade color change cost", convertRFtoOC(2.0f), 0.0f, convertRFtoOC(10000.0f));
			this.LIGHT_BOARD_COLOR_CHANGE_COST = builder.comment("How much energy changing the color or state of a Light Board's light will cost")
					.defineInRange("Light Board color change cost", convertRFtoOC(2.0f), 0.0f, convertRFtoOC(10000.0f));
			this.LIGHT_BOARD_COLOR_MAINTENANCE_COST = builder.comment("How much energy will be consumed per tick to keep a Light Board's light running. Note that this value is consumed for each active light on the board.")
					.defineInRange("Light Board color maintenance cost", convertRFtoOC(0.2f), 0.0f, convertRFtoOC(10000.0f));
			this.BOOM_BOARD_MAINTENANCE_COST = builder.comment("How much energy will be consumed per tick to keep a Server Self-Destructor active.") // yep i was right
					.defineInRange("SSD Maintenance cost", convertRFtoOC(0.2f), 0.0f, convertRFtoOC(10000.0f));
			this.RACK_CAPACITOR_CAPACITY = builder.comment("How much energy a Rack Capacitor can store.")
					.defineInRange("Rack Capacitor Capacity", convertRFtoOC(7500f), 0.0f, convertRFtoOC(10000.0f));
			this.SWITCH_BOARD_MAINTENANCE_COST = builder.comment("How much energy will be consumed per tick to keep a Switch Board's switch active. Note that this value is consumed for each active switch on the board.")
					.defineInRange("Switch Board Maintenance Cost", convertRFtoOC(0.2f), 0.0f, convertRFtoOC(10000.0f));
			this.RADAR_ENERGY_COST_OC = builder.comment("How much energy each 1-block distance takes by OpenComputers radars.")
					.defineInRange("Radar Energy Cost", convertRFtoOC(50.0f), 0.0f, convertRFtoOC(10000.0f));
			builder.pop();

			builder.push("Radar");
			this.RADAR_RANGE = builder.comment("The maximum range of the Radar.")
					.defineInRange("Radar Range", 8, 0, 256);
			this.RADAR_ONLY_DISTANCE = builder.comment("Stop Radars from outputting X/Y/Z coordinates and instead only output the distance from an entity.")
					.define("Only output distance", true);
			builder.pop();

			builder.push("Particles");
			this.FX_RANGE = builder.comment("The maximum range of particle-emitting devices. Set to -1 to make it work over any distance.")
					.defineInRange("Particles Range", 256, -1, 65536);
			builder.pop();

			builder.push("Tape Drive");
			this.TAPEDRIVE_BUFFER_MS = builder.comment("The amount of time (in milliseconds) used for pre-buffering the tape for audio playback. If you get audio playback glitches in SMP/your TPS is under 20, RAISE THIS VALUE!")
					.defineInRange("Tape Driver Buffer (ms)", 750, 500, 10000);
			this.TAPEDRIVE_DISTANCE = builder.comment("The distance up to which Tape Drives can be heard.")
					.defineInRange("Tape Drive Distance", 24, 0, 64);
			this.TAPE_LENGTHS = builder.comment("The lengths of the computronics tapes. Should be 10 numbers separated by commas")
					.define("Tape Lengths", "4,8,16,32,64,2,6,16,128,128");

			this.PORTABLE_TAPEDRIVE_DISTANCE = builder.comment("The distance up to which Portable Tape Drives can be heard.")
					.defineInRange("PTD Hearing Distance", 8, 0, 64);
			builder.pop();

			builder.push("General");
			this.REDSTONE_REFRESH = builder.comment("Set whether some machines should stop being tickless in exchange for redstone output support.")
					.define("Enable ticking redstone support", true);
			builder.pop();
		}
	}

	public static final Common COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	private static double convertRFtoOC(double v) {
		return EnergyConverter.convertEnergy(v, "RF", "OC");
	}
	static //constructor
	{
		Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON = commonSpecPair.getLeft();
		COMMON_SPEC = commonSpecPair.getRight();
	}
}


// /**
//  * @author Vexatos
//  */
// public class Config {

// 	public final Configuration config;

// 	public static int CHATBOX_DISTANCE = 40;
// 	public static boolean CHATBOX_MAGIC = false;
// 	public static int CAMERA_DISTANCE = 32;
// 	public static int TAPEDRIVE_DISTANCE = 24;
// 	public static int TAPEDRIVE_BUFFER_MS = 750;
// 	public static int PORTABLE_TAPEDRIVE_DISTANCE = 8;
// 	public static int RADAR_RANGE = 8;
// 	public static int FX_RANGE = 256;
// 	public static boolean RADAR_ONLY_DISTANCE = true;
// 	public static boolean CIPHER_CAN_LOCK = true;
// 	public static double CIPHER_ENERGY_STORAGE = 1600.0;
// 	public static double CIPHER_KEY_CONSUMPTION = 1600.0;
// 	public static double CIPHER_WORK_CONSUMPTION = 16.0;
// 	public static double RADAR_ENERGY_COST_OC = 5.0;
// 	public static double RADAR_CC_TIME = 0.5;
// 	public static double FX_ENERGY_COST = 0.2;
// 	public static double BEEP_ENERGY_COST = 1.0;
// 	public static double SOUND_CARD_ENERGY_COST = 1.0;
// 	public static double SPOOFING_ENERGY_COST = 0.2;
// 	public static double COLORFUL_UPGRADE_COLOR_CHANGE_COST = 0.2;
// 	public static double LIGHT_BOARD_COLOR_CHANGE_COST = 0.2;
// 	public static double LIGHT_BOARD_COLOR_MAINTENANCE_COST = 0.02;
// 	public static double BOOM_BOARD_MAINTENANCE_COST = 0.02;
// 	public static double RACK_CAPACITOR_CAPACITY = 7500;
// 	public static double SWITCH_BOARD_MAINTENANCE_COST = 0.02;
// 	public static String CHATBOX_PREFIX = "ChatBox";
// 	public static double LOCOMOTIVE_RELAY_RANGE = 128.0;
// 	public static double LOCOMOTIVE_RELAY_BASE_POWER = 20.0;
// 	public static boolean LOCOMOTIVE_RELAY_CONSUME_CHARGE = true;
// 	public static boolean TICKET_MACHINE_CONSUME_RF = true;
// 	public static boolean GREGTECH_RECIPES = false;
// 	public static boolean NON_OC_RECIPES = false;
// 	public static boolean FORESTRY_BEES = true;
// 	public static boolean BUILDCRAFT_STATION = true;

// 	public static int SOUND_SAMPLE_RATE = 44100;
// 	public static byte SOUND_VOLUME = 32;
// 	public static int SOUND_RADIUS = 24;
// 	public static int SOUND_CARD_MAX_DELAY = 5000; // TODO
// 	public static int SOUND_CARD_QUEUE_SIZE = 1024; // TODO
// 	public static int SOUND_CARD_CHANNEL_COUNT = 8; // TODO

// 	public static boolean TTS_ENABLED;
// 	public static int TTS_MAX_LENGTH = 300; // TODO

// 	public static boolean OC_UPGRADE_CAMERA;
// 	public static boolean OC_UPGRADE_CHATBOX;
// 	public static boolean OC_UPGRADE_RADAR;
// 	public static boolean OC_CARD_FX;
// 	public static boolean OC_CARD_SPOOF;
// 	public static boolean OC_CARD_BEEP;
// 	public static boolean OC_CARD_BOOM;
// 	public static boolean OC_UPGRADE_COLORFUL;
// 	public static boolean OC_CARD_NOISE;
// 	public static boolean OC_CARD_SOUND;
// 	public static boolean OC_BOARD_LIGHT;
// 	public static boolean OC_BOARD_BOOM;
// 	public static boolean OC_BOARD_CAPACITOR;
// 	public static boolean OC_BOARD_SWITCH;
// 	public static boolean OC_UPGRADE_SPEECH;

// 	public static boolean OC_MAGICAL_MEMORY;

// 	public static boolean CC_OPEN_MULTI_PERIPHERAL = true;
// 	public static boolean CC_ALL_MULTI_PERIPHERALS = true;
// 	public static boolean CC_ALWAYS_FIRST = true;

// 	public static boolean TIS3D_MODULE_COLORFUL = true;
// 	public static boolean TIS3D_MODULE_TAPE_READER = true;
// 	public static boolean TIS3D_MODULE_BOOM = true;

// 	public static String TAPE_LENGTHS;
// 	public static boolean REDSTONE_REFRESH, CHATBOX_CREATIVE;

// 	public static boolean MUST_UPDATE_TILE_ENTITIES = false;

// 	public Config(FMLCommonSetupEvent event) {
// 		config = new ModConfig(event.getSuggestedConfigurationFile());
// 		config.load();
// 	}

// 	public boolean isEnabled(String name, boolean def) {
// 		return config.get("enable", name, def).getBoolean(def);
// 	}

// 	private double convertRFtoOC(double v) {
// 		return EnergyConverter.convertEnergy(v, "RF", "OC");
// 	}

// 	public void preInit() {
// 		// Configs

// 		// Camera
// 		CAMERA_DISTANCE = config.getInt("maxDistance", "camera", 32, 16, 256, "The maximum camera distance, in blocks.");

// 		// Chat Box
// 		CHATBOX_CREATIVE = config.getBoolean("enableCreative", "chatbox", true, "Enable Creative Chat Boxes.");
// 		CHATBOX_DISTANCE = config.getInt("maxDistance", "chatbox", 40, 4, 32767, "The maximum chat box distance, in blocks.");
// 		CHATBOX_MAGIC = config.getBoolean("makeMagical", "chatbox", false, "Make the normal Chat Box have no range limit and work interdimensionally.");
// 		CHATBOX_PREFIX = config.getString("prefix", "chatbox", "ChatBox", "The Chat Box's default prefix.");

// 		// Cipher Block
// 		CIPHER_CAN_LOCK = config.getBoolean("canLock", "cipherblock", true, "Decides whether Cipher Blocks can or cannot be locked.");

// 		SOUND_SAMPLE_RATE = config.getInt("soundSampleRate", "sound.client", 44100, 0, Integer.MAX_VALUE, "The sample rate used for generating sounds. Modify at your own risk.");
// 		SOUND_VOLUME = (byte) config.getInt("soundVolume", "sound.client", 127, 0, Byte.MAX_VALUE, "The base volume of generated sounds.");
// 		SOUND_RADIUS = config.getInt("soundRadius", "sound.client", 24, 0, 64, "The radius in which generated sounds can be heard.");

// 		TTS_ENABLED = config.getBoolean("enableTextToSpeech", "tts", true, "Enable Text To Speech. To use it, install MaryTTS, a language and a corresponding voice into the marytts directory of your minecraft instance. For installation instructions, see http://wiki.vex.tty.sh/wiki:computronics:mary");
// 		TTS_MAX_LENGTH = config.getInt("maxPhraseLength", "tts", 300, 0, 100000, "The maximum number of text bytes the speech box can process at a time.");

// 		if(Mods.isLoaded(Mods.OpenComputers)) {
// 			//Advanced Cipher Block
// 			CIPHER_ENERGY_STORAGE = convertRFtoOC(
// 				config.getFloat("cipherEnergyStorage", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Chipher Block can store"));
// 			CIPHER_KEY_CONSUMPTION = convertRFtoOC(
// 				config.getFloat("cipherKeyConsumption", "power", 16000.0f, 0.0f, 100000.0F, "How much energy the Advanced Cipher Block should consume for creating a key set"));
// 			CIPHER_WORK_CONSUMPTION = convertRFtoOC(
// 				config.getFloat("cipherWorkConsumption", "power", 160.0f, 0.0f, 100000.0f, "How much base energy the Advanced Cipher Block should consume per encryption/decryption task. It will consume this value + 2*(number of characters in message)"));

// 			OC_UPGRADE_CAMERA = config.get("enable.opencomputers", "cameraUpgrade", true).getBoolean(true);
// 			OC_UPGRADE_CHATBOX = config.get("enable.opencomputers", "chatboxUpgrade", true).getBoolean(true);
// 			OC_UPGRADE_RADAR = config.get("enable.opencomputers", "radarUpgrade", true).getBoolean(true);
// 			OC_CARD_FX = config.get("enable.opencomputers", "particleCard", true).getBoolean(true);
// 			OC_CARD_SPOOF = config.get("enable.opencomputers", "spoofingCard", true).getBoolean(true);
// 			OC_CARD_BEEP = config.get("enable.opencomputers", "beepCard", true).getBoolean(true);
// 			OC_CARD_BOOM = config.get("enable.opencomputers", "boomCard", true).getBoolean(true);
// 			OC_UPGRADE_COLORFUL = config.get("enable.opencomputers", "colorfulUpgrade", true).getBoolean(true);
// 			OC_CARD_NOISE = config.get("enable.opencomputers", "noiseCard", true).getBoolean(true);
// 			OC_CARD_SOUND = config.get("enable.opencomputers", "soundCard", true).getBoolean(true);
// 			OC_BOARD_LIGHT = config.get("enable.opencomputers", "lightBoard", true).getBoolean(true);
// 			OC_BOARD_BOOM = config.get("enable.opencomputers", "boomBoard", true).getBoolean(true);
// 			OC_BOARD_CAPACITOR = config.get("enable.opencomputers", "rackCapacitor", true).getBoolean(true);
// 			OC_BOARD_SWITCH = config.get("enable.opencomputers", "switchBoard", true).getBoolean(true);
// 			OC_UPGRADE_SPEECH = config.get("enable.opencomputers", "speechUpgrade", true).getBoolean(true);

// 			OC_MAGICAL_MEMORY = config.get("enable.opencomputers", "magicalMemory", true).getBoolean(true);

// 			if(OC_CARD_SOUND) {
// 				SOUND_CARD_MAX_DELAY = config.getInt("ocSoundCardMaxDelay", "sound", SOUND_CARD_MAX_DELAY, 0, Integer.MAX_VALUE, "Maximum delay allowed in a sound card's instruction queue, in milliseconds");
// 				SOUND_CARD_QUEUE_SIZE = config.getInt("ocSoundCardQueueSize", "sound", SOUND_CARD_QUEUE_SIZE, 0, Integer.MAX_VALUE, "Maximum  number of instructons allowed in a sound cards instruction queue. This directly affects the maximum size of the packets sent to the client.");
// 				SOUND_CARD_CHANNEL_COUNT = config.getInt("ocSoundCardChannelCount", "sound", SOUND_CARD_CHANNEL_COUNT, 1, 65536, "The number of audio channels each sound card has.");
// 			}

// 			// Particle Card
// 			FX_ENERGY_COST = convertRFtoOC(
// 				config.getFloat("ocParticleCardCostPerParticle", "power", 2.0f, 0.0f, 10000.0f, "How much energy 1 particle emission should take. Multiplied by the distance to the target."));
// 			//Spoofing Card
// 			SPOOFING_ENERGY_COST = convertRFtoOC(
// 				config.getFloat("ocSpoofingCardCostPerMessage", "power", 2.0f, 0.0f, 10000.0f, "How much energy sending one spoofed message should take"));
// 			// Beep Card
// 			BEEP_ENERGY_COST = convertRFtoOC(
// 				config.getFloat("ocBeepCardCostPerSound", "power", 10.0f, 0.0f, 10000.0f, "How much energy a single beep will cost for 1 second"));
// 			// Sound Card
// 			SOUND_CARD_ENERGY_COST = convertRFtoOC(
// 				config.getFloat("ocSoundCardCostPerSecond", "power", 10.0f, 0.0f, 10000.0f, "How much energy the sound card will consume per second of processed sound."));
// 			// Colorful Upgrade
// 			COLORFUL_UPGRADE_COLOR_CHANGE_COST = convertRFtoOC(
// 				config.getFloat("ocColorfulUpgradeColorChangeCost", "power", 2.0f, 0.0f, 10000.0f, "How much energy changing the color of the Colorful Upgrade will cost"));

// 			// Rack Mountables
// 			LIGHT_BOARD_COLOR_CHANGE_COST = convertRFtoOC(
// 				config.getFloat("ocLightBoardColorChangeCost", "power", 2.0f, 0.0f, 10000.0f, "How much energy changing the color or state of a Light Board's light will cost"));
// 			LIGHT_BOARD_COLOR_MAINTENANCE_COST = convertRFtoOC(
// 				config.getFloat("ocLightBoardColorMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Light Board's light running. Note that this value is consumed for each active light on the board."));
// 			BOOM_BOARD_MAINTENANCE_COST = convertRFtoOC(
// 				config.getFloat("ocBoomBoardMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Server Self-Destructor active."));
// 			RACK_CAPACITOR_CAPACITY = convertRFtoOC(
// 				config.getFloat("ocRackCapacitorCapacity", "power", 7500f, 0.0f, 10000.0f, "How much energy a Rack Capacitor can store."));
// 			SWITCH_BOARD_MAINTENANCE_COST = convertRFtoOC(
// 				config.getFloat("ocSwitchBoardMaintenanceCost", "power", 0.2f, 0.0f, 10000.0f, "How much energy will be consumed per tick to keep a Switch Board's switch active. Note that this value is consumed for each active switch on the board."));

// 			if(Mods.isLoaded(Mods.Railcraft)) {
// 				LOCOMOTIVE_RELAY_BASE_POWER = convertRFtoOC(
// 					config.getFloat("locomotiveRelayBasePower", "power.railcraft", 20.0f, 0.0f, 10000.0f, "How much base energy the Locomotive Relay consumes per operation"));
// 			}

// 			NON_OC_RECIPES = config.getBoolean("easyRecipeMode", "recipes", false, "Set this to true to make some recipes not require OpenComputers blocks and items");

// 			if(Mods.hasVersion(Mods.Forestry, Mods.Versions.Forestry)) {
// 				FORESTRY_BEES = config.getBoolean("opencomputersBees", "enable.forestry", true, "Set this to false to disable Forestry bee species for OpenComputers");
// 			}
// 			if(Mods.isLoaded(Mods.BuildCraftTransport) && Mods.isLoaded(Mods.BuildCraftCore)) {
// 				BUILDCRAFT_STATION = config.getBoolean("droneDockingStation", "enable.buildcraft", true, "Set this to false to disable the Drone Docking Station for OpenComputers");
// 			}
// 		}

// 		if(Mods.isLoaded(Mods.ComputerCraft)) {
// 			if(Mods.isLoaded(Mods.OpenPeripheral)) {
// 				CC_OPEN_MULTI_PERIPHERAL = config.getBoolean("openMultiPeripheral", "computercraft.multiperipheral", true, "Set this to false to disable MultiPeripheral compatibility with OpenPeripheral peripherals");
// 			}
// 			CC_ALL_MULTI_PERIPHERALS = config.getBoolean("allMultiPeripherals", "computercraft.multiperipheral", true, "Set this to true to fix multiple mods adding peripherals to the same block not working");
// 			CC_ALWAYS_FIRST = config.getBoolean("alwaysFirstPeripheral", "computercraft.multiperipheral", true, "If this is true, the Computronics MultiPeripheral system will almost always be the one recognized by ComputerCraft");
// 			config.setCategoryComment("computercraft.multiperipheral", "If all of these options are set to true, Computronics will fix almost every conflict with multiple mods adding peripherals to the same block");
// 			if(CC_OPEN_MULTI_PERIPHERAL && CC_ALL_MULTI_PERIPHERALS && CC_ALWAYS_FIRST) {
// 				Computronics.log.info("Multiperipheral system for ComputerCraft engaged. Hooray!");
// 				Computronics.log.info("Multiple mods registering peripherals for the same block now won't be a problem anymore.");
// 			}
// 		}

// 		if(Mods.isLoaded(Mods.TIS3D)) {
// 			TIS3D_MODULE_COLORFUL = config.get("enable.tis3d", "colorfulModule", true).getBoolean(true);
// 			TIS3D_MODULE_TAPE_READER = config.get("enable.tis3d", "tapeReaderModule", true).getBoolean(true);
// 			TIS3D_MODULE_BOOM = config.get("enable.tis3d", "boomModule", true).getBoolean(true);
// 		}

// 		// Radar
// 		RADAR_RANGE = config.getInt("maxRange", "radar", 8, 0, 256, "The maximum range of the Radar.");
// 		RADAR_ONLY_DISTANCE = config.getBoolean("onlyOutputDistance", "radar", true, "Stop Radars from outputting X/Y/Z coordinates and instead only output the distance from an entity.");

// 		// Particles
// 		FX_RANGE = config.getInt("particleRange", "particles", FX_RANGE, -1,65536, "The maximum range of particle-emitting devices. Set to -1 to make it work over any distance.");

// 		// Tape Drive
// 		TAPEDRIVE_BUFFER_MS = config.getInt("audioPreloadMs", "tapedrive", 750, 500, 10000, "The amount of time (in milliseconds) used for pre-buffering the tape for audio playback. If you get audio playback glitches in SMP/your TPS is under 20, RAISE THIS VALUE!");
// 		TAPEDRIVE_DISTANCE = config.getInt("hearingDistance", "tapedrive", 24, 0, 64, "The distance up to which Tape Drives can be heard.");
// 		TAPE_LENGTHS = config.getString("tapeLengths", "tapedrive", "4,8,16,32,64,2,6,16,128,128", "The lengths of the computronics tapes. Should be 10 numbers separated by commas");

// 		PORTABLE_TAPEDRIVE_DISTANCE= config.getInt("hearingDistance", "tapedrive.portable", 8, 0, 64, "The distance up to which Portable Tape Drives can be heard.");

// 		// General
// 		REDSTONE_REFRESH = config.getBoolean("enableTickingRedstoneSupport", "general", true, "Set whether some machines should stop being tickless in exchange for redstone output support.");

// 		// Power
// 		RADAR_ENERGY_COST_OC = convertRFtoOC(
// 			config.getFloat("radarCostPerBlock", "power", 50.0f, 0.0f, 10000.0f, "How much energy each 1-block distance takes by OpenComputers radars."));

// 		// Railcraft integration
// 		if(Mods.isLoaded(Mods.Railcraft)) {
// 			LOCOMOTIVE_RELAY_RANGE = (double) config.getInt("locomotiveRelayRange", "railcraft", 128, 0, 512, "The range of Locomotive Relays in Blocks.");
// 			LOCOMOTIVE_RELAY_CONSUME_CHARGE = config.getBoolean("locomotiveRelayConsumeCharge", "railcraft", true, "If true, the Locomotive Relay will consume"
// 				+ "a little bit of Railcraft charge in the locomotive everytime it is accessing the locomotive");
// 			TICKET_MACHINE_CONSUME_RF = config.getBoolean("ticketMachineConsumeCharge", "railcraft", true, "If true, the Ticket Machine will"
// 				+ "require a little bit of RF to print tickets");
// 		}

// 		// GregTech recipe mode
// 		if(Mods.isLoaded(Mods.GregTech)) {
// 			GREGTECH_RECIPES = config.getBoolean("gtRecipeMode", "recipes", true, "Set this to true to enable GregTech-style recipes");
// 		}

// 		config.setCategoryComment("power", "Every value related to energy in this section uses RF as the base power unit.");
// 		config.setCategoryComment("sound", "Configs for sounds generated by devices like the Beep Card.");
// 		if(Mods.isLoaded(Mods.OpenComputers) || Mods.isLoaded(Mods.ComputerCraft)) {
// 			config.setCategoryComment(Compat.Compatibility, "Set anything here to false to prevent Computronics from adding the respective Peripherals and Drivers");
// 		}
// 	}

// 	public void save() {
// 		config.save();
// 	}
// }
