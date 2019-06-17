package quaternary.zenscroll.util;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

@ZenClass("mods.zenscroll.ScrollProcessor")
@ZenRegister
public interface ScrollProcessor {
	@ZenMethod
	IItemStack apply(IItemStack prev, IItemStack next);
	
	@ZenProperty("ERASE_TAG")
	ScrollProcessor ERASE_NBT = (prev, next) -> next;
	
	@ZenProperty("COPY_TAG")
	ScrollProcessor COPY_NBT = (prev, next) -> {
		if(prev.hasTag()) {
			return next.withTag(prev.getTag(), false);
		} else {
			return next.withEmptyTag();
		}
	};
}
