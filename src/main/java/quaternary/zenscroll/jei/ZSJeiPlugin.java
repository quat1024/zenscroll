package quaternary.zenscroll.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import quaternary.zenscroll.ScrollGroup;
import quaternary.zenscroll.ZenScroll;

import java.util.stream.Collectors;

@JEIPlugin
public class ZSJeiPlugin implements IModPlugin {
	@Override
	public void registerCategories(IRecipeCategoryRegistration reg) {
		reg.addRecipeCategories(
			new ScrollGroupRecipeCategory(reg.getJeiHelpers())
		);
	}
	
	@Override
	public void register(IModRegistry reg) {
		reg.handleRecipes(ScrollGroup.class, ScrollGroupRecipeWrapper::new, ScrollGroupRecipeCategory.UID);
		
		reg.addRecipes(
			ZenScroll.scrollGroups.stream()
				.filter(g -> !g.isCreativeOnly())
				.collect(Collectors.toList()),
			ScrollGroupRecipeCategory.UID
		);
	}
}
