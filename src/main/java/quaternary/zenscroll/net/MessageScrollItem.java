package quaternary.zenscroll.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import quaternary.zenscroll.ScrollGroup;
import quaternary.zenscroll.ZenScroll;
import quaternary.zenscroll.config.ZenScrollConfig;

public class MessageScrollItem implements PacketHandler.IZenScrollMessage {
	@SuppressWarnings("unused")
	public MessageScrollItem() {}
	
	public MessageScrollItem(int slot, boolean isHotbar, boolean scrollUp) {
		this.slot = slot;
		this.isHotbar = isHotbar;
		this.scrollUp = scrollUp;
	}
	
	private int slot;
	private boolean isHotbar;
	private boolean scrollUp;
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(slot);
		buf.writeBoolean(isHotbar);
		buf.writeBoolean(scrollUp);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		slot = buf.readInt();
		isHotbar = buf.readBoolean();
		scrollUp = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageScrollItem, IMessage> {
		@Override
		public IMessage onMessage(MessageScrollItem msg, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				if(msg.isHotbar && msg.slot >= 9) {
					//Can't happen without firing off a weird packet!
					ctx.getServerHandler().disconnect(new TextComponentTranslation("zenscroll.kick.uCheatin"));
				}
				
				if(msg.isHotbar && !ZenScrollConfig.ALLOW_HOTBAR) {
					return;
				}
				
				if(!msg.isHotbar && !ZenScrollConfig.ALLOW_INVENTORY) {
					return;
				}
				
				InventoryPlayer inv = player.inventory;
				
				ItemStack stack = inv.getStackInSlot(msg.slot);
				if(stack.isEmpty()) return;
				
				for(ScrollGroup group : ZenScroll.scrollGroups) {
					if(group.checkPermission(player) && group.containsStack(stack)) {
						ItemStack replace = msg.scrollUp ? group.next(stack) : group.prev(stack);
						replace.setCount(stack.getCount()); //It's a copy, this is safe
						
						inv.setInventorySlotContents(msg.slot, replace);
						inv.markDirty();
					}
				}
			});
			
			return null;
		}
	}
}
