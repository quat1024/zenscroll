package quaternary.zenscroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import quaternary.zenscroll.config.ZenScrollConfig;
import quaternary.zenscroll.net.MessageScrollItem;
import quaternary.zenscroll.net.PacketHandler;
import quaternary.zenscroll.proxy.ClientProxy;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ZenScroll.MODID)
public class ClientEvents {
	@SubscribeEvent
	public static void mouseInput(MouseEvent e) {
		if(ClientProxy.SCROLL_MOD.getKeyCode() == 0) return; //unbound
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if(player != null && player.world != null && e.getDwheel() != 0 && ClientProxy.SCROLL_MOD.isKeyDown()) {
			ItemStack sel = player.inventory.getCurrentItem/*Stack*/();
			
			for(ScrollGroup group : ZenScroll.scrollGroups) {
				if(group.containsStack(sel)) {
					PacketHandler.sendToServer(new MessageScrollItem(ZenScrollConfig.REVERSED ^ (e.getDwheel() <= 0)));
					e.setCanceled(true);
					return;
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void tootip(ItemTooltipEvent e) {
		if(!ZenScrollConfig.TOOLTIP || ClientProxy.SCROLL_MOD.getKeyCode() == 0 || e.getEntityPlayer() == null || e.getEntityPlayer().world == null) return;
		
		for(ScrollGroup group : ZenScroll.scrollGroups) {
			if(group.containsStack(e.getItemStack())) {
				e.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("zenscroll.tooltip", ClientProxy.SCROLL_MOD.getDisplayName()));
			}
		}
	}
}
