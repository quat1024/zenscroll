package quaternary.zenscroll;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quaternary.zenscroll.config.ZenScrollConfig;
import quaternary.zenscroll.net.PacketHandler;
import quaternary.zenscroll.proxy.CommonProxy;

import java.util.ArrayList;
import java.util.List;

@Mod(
	modid = ZenScroll.MODID,
	name = ZenScroll.NAME,
	version = ZenScroll.VERSION,
	guiFactory = "quaternary.zenscroll.config.ConfigGuiBoilerplateA"
)
public class ZenScroll {
	public static final String MODID = "zenscroll";
	public static final String NAME = "ZenScroll";
	public static final String VERSION = "GRADLE:VERSION";
	
	public static final Logger LOG = LogManager.getLogger(NAME);
	
	public static final List<ScrollGroup> scrollGroups = new ArrayList<>();
	
	@SidedProxy(clientSide = "quaternary.zenscroll.proxy.ClientProxy", serverSide = "quaternary.zenscroll.proxy.CommonProxy")
	public static CommonProxy PROXY;
	
	@Mod.EventHandler
	public static void preinit(FMLPreInitializationEvent e) {
		PROXY.preinit(e);
		ZenScrollConfig.preinit(e);
		PacketHandler.preinit();
	}
}