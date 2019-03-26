package quaternary.zenscroll.net;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import quaternary.zenscroll.ScrollGroup;
import quaternary.zenscroll.ZenScroll;

public class MessageScrollItem implements PacketHandler.IZenScrollMessage {
	@SuppressWarnings("unused")
	public MessageScrollItem() {}
	
	public MessageScrollItem(boolean scrollUp) {
		this.scrollUp = scrollUp;
	}
	
	private boolean scrollUp;
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(scrollUp);
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		scrollUp = buf.readBoolean();
	}
	
	public static class Handler implements IMessageHandler<MessageScrollItem, IMessage> {
		@Override
		public IMessage onMessage(MessageScrollItem msg, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			player.getServerWorld().addScheduledTask(() -> {
				InventoryPlayer inv = player.inventory;
				ItemStack stack = inv.getCurrentItem/*Stack*/();
				
				for(ScrollGroup group : ZenScroll.scrollGroups) {
					if(group.checkPermission(player) && group.containsStack(stack)) {
						ItemStack replace = msg.scrollUp ? group.next(stack) : group.prev(stack);
						replace.setCount(stack.getCount()); //It's a copy, this is safe
						
						inv.setInventorySlotContents(inv.currentItem, replace);
						inv.markDirty();
					}
				}
			});
			
			return null;
		}
	}
}
