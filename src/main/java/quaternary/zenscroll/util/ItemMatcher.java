package quaternary.zenscroll.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.zenscroll.ItemMatcher")
@ZenRegister
public interface ItemMatcher {
	boolean matches(IItemStack group, IItemStack scrolled);
	
	ItemMatcher LENIENT_TAG = IIngredient::matches;
	ItemMatcher STRICT_TAG = IIngredient::matchesExact;
	ItemMatcher IGNORE_TAG = (group, scrolled) -> ItemStack.areItemsEqual(
		CraftTweakerMC.getItemStack(group),
		CraftTweakerMC.getItemStack(scrolled)
	);
}
