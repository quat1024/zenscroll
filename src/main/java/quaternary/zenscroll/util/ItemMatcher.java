package quaternary.zenscroll.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.zenscroll.ItemMatcher")
@ZenRegister
public interface ItemMatcher {
	@ZenMethod
	boolean matches(IItemStack group, IItemStack scrolled);
	
	@ZenProperty("LENIENT")
	ItemMatcher LENIENT_TAG = IIngredient::matches;
	
	@ZenProperty("STRICT")
	ItemMatcher STRICT_TAG = IIngredient::matchesExact;
	
	@ZenProperty("VERY_LENIENT")
	ItemMatcher IGNORE_TAG = (group, scrolled) -> ItemStack.areItemsEqual(
		CraftTweakerMC.getItemStack(group),
		CraftTweakerMC.getItemStack(scrolled)
	);
}
