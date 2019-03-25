package quaternary.zenscroll.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("mods.zenscroll.ScrollProcessor")
@ZenRegister
public interface ScrollProcessor {
	IItemStack apply(IItemStack prev, IItemStack next);
}
