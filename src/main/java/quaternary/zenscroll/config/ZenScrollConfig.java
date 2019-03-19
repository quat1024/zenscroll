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
	
	public static boolean ENABLED;
	public static boolean REVERSED;
	public static boolean TOOLTIP;
	
	public static void preinit(FMLPreInitializationEvent e) {
		config = new Configuration(e.getSuggestedConfigurationFile(), String.valueOf(CONFIG_VERSION));
		config.load();
		
		int currentConfigVersion = Integer.parseInt(config.getLoadedConfigVersion());
		if(currentConfigVersion == 1) {
			config.getCategory("client").remove("modifierKey");
		}
		
		readConfig();
	}
	
	public static void readConfig() {
		REVERSED = config.getBoolean("reversed", "client", false, "Should the scroll direction be reversed?");
		
		ENABLED = config.getBoolean("enabled", "client", true, "Should scrolling behavior be enabled for your client?");
		
		TOOLTIP = config.getBoolean("tooltip", "client", true, "Should a small tooltip be added on items that can be scrolled?");
		
		if(config.hasChanged()) config.save();
	}
	
	@SubscribeEvent
	public static void configChanged(ConfigChangedEvent.OnConfigChangedEvent e) {
		if(e.getModID().equals(ZenScroll.MODID)) {
			readConfig();
		}
	}
}
