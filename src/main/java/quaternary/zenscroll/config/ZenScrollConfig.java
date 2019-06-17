package quaternary.zenscroll.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import quaternary.zenscroll.ZenScroll;

@Mod.EventBusSubscriber(modid = ZenScroll.MODID)
public class ZenScrollConfig {
	public static Configuration config;
	public static int CONFIG_VERSION = 2;
	
	public static boolean REVERSED;
	public static boolean TOOLTIP;
	public static boolean JEI_CREATIVE;
	
	public static boolean SEND_HOTBAR;
	public static boolean SEND_INVENTORY;
	
	//server side
	public static boolean ALLOW_HOTBAR;
	public static boolean ALLOW_INVENTORY;
	
	public static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), String.valueOf(CONFIG_VERSION));
		config.load();
		
		int currentConfigVersion = Integer.parseInt(config.getLoadedConfigVersion());
		if(currentConfigVersion == 1) {
			config.getCategory("client").remove("modifierKey");
			config.getCategory("client").remove("enabled");
		}
		
		readConfig();
	}
	
	public static void readConfig() {
		REVERSED = config.getBoolean("reversed", "client", false, "Should the scroll direction be reversed?");
		TOOLTIP = config.getBoolean("tooltip", "client", true, "Should a small tooltip be added on items that can be scrolled?");
		JEI_CREATIVE = config.get("client", "jeiCreative", false, "Should creative-only scroll groups appear in JEI?").setRequiresMcRestart(true).getBoolean();
		SEND_HOTBAR = config.getBoolean("enableHotbar", "client", true, "Should scrolling be enabled for you outside of your inventory?");
		SEND_INVENTORY = config.getBoolean("enableInventory", "client", true, "Should scrolling be enabled for you inside of your inventory?");
		
		ALLOW_HOTBAR = config.getBoolean("allowHotbar", "general", true, "Should the server allow players to scroll items from outside their inventory?");
		ALLOW_INVENTORY = config.getBoolean("allowInventory", "general", true, "Should the server allow players to scroll items from inside their inventory?");
		
		if(config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(ZenScroll.MODID)) {
			readConfig();
		}
	}
}
