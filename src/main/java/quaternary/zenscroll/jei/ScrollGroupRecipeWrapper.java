package quaternary.zenscroll.jei;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import quaternary.zenscroll.ScrollGroup;
import quaternary.zenscroll.proxy.ClientProxy;

import java.util.List;
import java.util.stream.Collectors;

public class ScrollGroupRecipeWrapper implements IRecipeWrapper {
	public ScrollGroupRecipeWrapper(ScrollGroup group) {
		this.group = group;
	}
	
	public final ScrollGroup group;
	
	@Override
	public void getIngredients(IIngredients ing) {
		List<ItemStack> stacks = group.items.stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
		
		ing.setInputs(VanillaTypes.ITEM, stacks);
		ing.setOutputs(VanillaTypes.ITEM, stacks);
	}
	
	@Override
	public void drawInfo(Minecraft mc, int width, int height, int mouseX, int mouseY) {
		String msg;
		int color;
		
		if(ClientProxy.SCROLL_MOD.getKeyCode() == 0) {
			msg = "zenscroll.jei.notBound";
			color = 0xc25151;
		} else {
			msg = "zenscroll.tooltip";
			color = 0xa7a7a7;
		}
		
		msg = I18n.format(msg, ClientProxy.SCROLL_MOD.getDisplayName());
		
		drawCenteredString(mc.fontRenderer, msg, width / 2, 2, color);
	}
	
	private static void drawCenteredString(FontRenderer font, String s, int x, int y, int color) {
		font.drawString(s, x - font.getStringWidth(s) / 2, y, color);
	}
}
