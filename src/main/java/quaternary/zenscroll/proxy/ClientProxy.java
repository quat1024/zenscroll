package quaternary.zenscroll.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;
import quaternary.zenscroll.ZenScroll;

public class ClientProxy extends CommonProxy {
	public static final KeyBinding SCROLL_MOD = new KeyBinding(
		"zenscroll.options.modifier_key",
		KeyConflictContext.IN_GAME,
		Keyboard.KEY_LMENU,
		ZenScroll.NAME
	);
	
	@Override
	public void preinit(FMLPreInitializationEvent e) {
		super.preinit(e);
		
		ClientRegistry.registerKeyBinding(SCROLL_MOD);
	}
}
