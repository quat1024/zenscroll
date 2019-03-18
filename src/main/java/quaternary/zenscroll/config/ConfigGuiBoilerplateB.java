package quaternary.zenscroll.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;
import quaternary.zenscroll.ZenScroll;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigGuiBoilerplateB extends GuiConfig {
	public ConfigGuiBoilerplateB(GuiScreen parent) {
		super(parent, getConfigElements(), ZenScroll.MODID, false, false, ZenScroll.NAME + " Config!");
	}
	
	//Adapted from Choonster's TestMod3. They say they adapted it from EnderIO "a while back".
	//http://www.minecraftforge.net/forum/topic/39880-110solved-make-config-options-show-up-in-gui/
	private static List<IConfigElement> getConfigElements() {
		Configuration c = ZenScrollConfig.config;
		//Don't look!
		return c.getCategoryNames().stream().filter(name -> !c.getCategory(name).isChild()).map(name -> new ConfigElement(c.getCategory(name).setLanguageKey(ZenScroll.MODID + ".config." + name))).collect(Collectors.toList());
	}
}
