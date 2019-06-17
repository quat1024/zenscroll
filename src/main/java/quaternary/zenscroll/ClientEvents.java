package quaternary.zenscroll;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import quaternary.zenscroll.config.ZenScrollConfig;
import quaternary.zenscroll.net.MessageScrollItem;
import quaternary.zenscroll.net.PacketHandler;
import quaternary.zenscroll.proxy.ClientProxy;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = ZenScroll.MODID)
public class ClientEvents {
	@SubscribeEvent
	public static void mouseInput(MouseEvent e) {
		if(!ZenScrollConfig.SEND_HOTBAR || ClientProxy.SCROLL_MOD.getKeyCode() == 0) return; //unbound
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		if(player != null && player.world != null && e.getDwheel() != 0 && ClientProxy.SCROLL_MOD.isKeyDown()) {
			ItemStack sel = player.inventory.getCurrentItem/*Stack*/();
			
			for(ScrollGroup group : ZenScroll.scrollGroups) {
				if(group.checkPermission(player) && group.containsStack(sel)) {
					PacketHandler.sendToServer(new MessageScrollItem(player.inventory.currentItem, true, ZenScrollConfig.REVERSED ^ (e.getDwheel() <= 0)));
					e.setCanceled(true);
					return;
				}
			}
		}
	}
	
	//Hattip to Item Scroller for pointing me towards this event.
	@SubscribeEvent
	public static void guiMouseInput(GuiScreenEvent.MouseInputEvent.Pre e) {
		if(!ZenScrollConfig.SEND_INVENTORY || Mouse.getEventDWheel() == 0 || !(e.getGui() instanceof GuiContainer)) return;
		GuiContainer inv = (GuiContainer) e.getGui(); 
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		
		//Using funky method of determining key press since KeyBinding#isKeyDown doesn't seem to work?
		if(player != null && player.world != null && isKeyDownFunky(ClientProxy.SCROLL_MOD)) {
			Slot slot = inv.getSlotUnderMouse();
			if(slot == null || !(slot.inventory instanceof InventoryPlayer)) return;
			
			ItemStack stackIn = slot.getStack();
			if(stackIn.isEmpty()) return;
			
			//epic copy paste coding right here
			for(ScrollGroup group : ZenScroll.scrollGroups) {
				if(group.checkPermission(player) && group.containsStack(stackIn)) {
					PacketHandler.sendToServer(new MessageScrollItem(slot.getSlotIndex(), false, ZenScrollConfig.REVERSED ^ (Mouse.getEventDWheel() <= 0)));
					e.setCanceled(true);
					return;
				}
			}
		}
	}
	
	private static boolean isKeyDownFunky(KeyBinding ok) {
		if(ok.getKeyModifier() != KeyModifier.NONE) {
			if(!ok.getKeyModifier().isActive(KeyConflictContext.GUI)) return false;
		}
		
		return Keyboard.isKeyDown(ok.getKeyCode());
	}
	
	@SubscribeEvent
	public static void tootip(ItemTooltipEvent e) {
		if(!ZenScrollConfig.TOOLTIP || ClientProxy.SCROLL_MOD.getKeyCode() == 0) return;
		
		EntityPlayer player = e.getEntityPlayer();
		if(player == null || player.world == null) return;
		
		//No access to the hovered Slot in this event, so let's forgo the event and do it ourselves.
		GuiScreen currentScreen = Minecraft.getMinecraft().currentScreen;
		
		if(currentScreen instanceof GuiContainer) {
			Slot slot = ((GuiContainer) currentScreen).getSlotUnderMouse();
			if(slot == null) return;
			if(!(slot.inventory instanceof InventoryPlayer)) return;
			
			ItemStack stackIn = slot.getStack();
			if(stackIn.isEmpty()) return;
			
			for(ScrollGroup group : ZenScroll.scrollGroups) {
				if(group.checkPermission(player) && group.containsStack(stackIn)) {
					e.getToolTip().add(TextFormatting.LIGHT_PURPLE + I18n.format("zenscroll.tooltip", ClientProxy.SCROLL_MOD.getDisplayName()));
				}
			}
		}
	}
}
