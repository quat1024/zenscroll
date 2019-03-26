package quaternary.zenscroll.jei;

import crafttweaker.api.minecraft.CraftTweakerMC;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import quaternary.zenscroll.ZenScroll;

import javax.annotation.Nullable;
import java.util.List;

public class ScrollGroupRecipeCategory implements IRecipeCategory<ScrollGroupRecipeWrapper> {
	public ScrollGroupRecipeCategory(IJeiHelpers dorp) {
		gui = dorp.getGuiHelper();
		
		bg = gui.createDrawable(new ResourceLocation(ZenScroll.MODID, "textures/jei/bg.png"), 0, 0, WIDTH, HEIGHT);
		//Idk why i have to do this too it's being weird. Only displays the upper left pixel if I dont
		icon = gui.drawableBuilder(new ResourceLocation(ZenScroll.MODID, "textures/jei/icon.png"), 0, 0, 16, 16).setTextureSize(16, 16).build();
		
		highlight = gui.drawableBuilder(new ResourceLocation(ZenScroll.MODID, "textures/jei/highlight.png"), 0, 0, 18, 18).setTextureSize(32, 32).build();
	}
	
	public static final String UID = "zenscroll.scroll";
	
	private static final int WIDTH = 180;
	private static final int HEIGHT = 90;
	
	private final IGuiHelper gui;
	private final IDrawable bg;
	private final IDrawable icon;
	private final IDrawable highlight;
	
	@Override
	public String getUid() {
		return UID;
	}
	
	@Override
	public String getTitle() {
		return I18n.format("zenscroll.jei.scroll.title");
	}
	
	@Override
	public String getModName() {
		return ZenScroll.NAME;
	}
	
	@Override
	public IDrawable getBackground() {
		return bg;
	}
	
	@Nullable
	@Override
	public IDrawable getIcon() {
		return icon;
	}
	
	@Override
	public void setRecipe(IRecipeLayout layout, ScrollGroupRecipeWrapper wrapper, IIngredients ing) {
		IGuiItemStackGroup stacks = layout.getItemStacks();
		
		List<List<ItemStack>> scrollGroup = ing.getOutputs(VanillaTypes.ITEM);
		if(scrollGroup.isEmpty()) return;
		
		IFocus<?> focus = layout.getFocus();
		ItemStack focusedStack = ItemStack.EMPTY;
		if(focus != null && focus.getValue() instanceof ItemStack) {
			focusedStack = (ItemStack) focus.getValue();
		}
		
		for(int i = 0; i < Math.min(36, scrollGroup.size()); i++) {
			int x = 9 + ((i % 9) * 18);
			int y = 9 + ((i / 9) * 18) + 4; //image is shifted down 4 pixels lol
			
			stacks.init(i, true, x, y);
			stacks.set(i, scrollGroup.get(i));
			
			if(!focusedStack.isEmpty() && scrollGroup.get(i).get(0).isItemEqual(focusedStack)) {
				stacks.setBackground(i, highlight);
			}
		}
	}
}
