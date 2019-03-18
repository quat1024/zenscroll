package quaternary.zenscroll.net;

import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.zenscroll.ZenScroll;

public class PacketHandler {
	private static SimpleNetworkWrapper NET;
	
	public static void preinit() {
		NET = new SimpleNetworkWrapper(ZenScroll.MODID);
		NET.registerMessage(MessageScrollItem.Handler.class, MessageScrollItem.class, 0, Side.SERVER);
	}
	
	public static void sendToServer(IZenScrollMessage message) {
		NET.sendToServer(message);
	}
	
	public interface IZenScrollMessage extends IMessage {}
}
