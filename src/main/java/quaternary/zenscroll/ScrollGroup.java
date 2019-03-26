package quaternary.zenscroll;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.OreDictionary;
import quaternary.zenscroll.util.Etc;
import quaternary.zenscroll.util.ScrollProcessor;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenConstructor;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;
import stanhebben.zenscript.annotations.ZenProperty;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ZenRegister
@ZenClass("mods.zenscroll.ScrollGroup")
public class ScrollGroup implements Iterable<IItemStack> {
	@ZenConstructor
	public ScrollGroup(IIngredient... ingredients) {
		this.items = Etc.flattenIIngredients(ingredients).stream()
			.flatMap(i -> {
				if(i.getMetadata() == OreDictionary.WILDCARD_VALUE) {
					//Expand wildcard items to whatever shows up in the search tab
					NonNullList<ItemStack> oof = NonNullList.create();
					CraftTweakerMC.getItemStack(i).getItem().getSubItems(CreativeTabs.SEARCH, oof);
					return oof.stream().map(CraftTweakerMC::getIItemStack);
				} else {
					return Stream.of(i);
				}
			})
			.map(i -> i.withAmount(1))
			.filter(i -> !i.isEmpty())
			.collect(Collectors.toList());
	}
	
	@ZenMethod
	@ZenDoc("Constructs a scroll group consisting of these items. Ingredients are flattened into their composite IItemStacks in order, and any wildcarded IItemStacks are expanded to what appears in the creative Search tab.")
	public static ScrollGroup of(IIngredient... ingredients) {
		return new ScrollGroup(ingredients);
	}
	
	@ZenProperty
	public List<IItemStack> items;
	
	public ScrollProcessor processor = (prev, next) -> next;
	
	public boolean creativeOnly = false;
	
	@ZenMethod("processor")
	@ZenDoc("Set the processor function. This function is called every time the player scrolls once, and it takes two arguments: the previous itemstack, and the next stack. By default, the processor function simply returns the next stack as-is, but it can be used for copying NBT data and the like.")
	public ScrollGroup setProcessor(ScrollProcessor processor) {
		this.processor = processor;
		return this;
	}
	
	@ZenMethod("copyTag")
	@ZenDoc("Sets the processor function to one that copies the tag from the previous itemstack.")
	public ScrollGroup setCopyNBTProcessor() {
		processor = (prev, next) -> {
			if(prev.hasTag()) {
				return next.withTag(prev.getTag(), false);
			} else {
				return next.withEmptyTag();
			}
		};
		
		return this;
	}
	
	@ZenMethod
	public ScrollGroup creativeOnly(@Optional(valueBoolean = true) boolean v) {
		creativeOnly = v;
		return this;
	}
	
	@ZenMethod
	public boolean isCreativeOnly() {
		return creativeOnly;
	}
	
	public boolean containsStack(ItemStack stack) {
		return indexOfStack(stack) != -1;
	}
	
	@ZenMethod("contains")
	@ZenDoc("Returns 'true' if this scroll group contains this stack.")
	public boolean containsIStack(IItemStack istack) {
		return indexOfIStack(istack) != -1;
	}
	
	public int indexOfStack(ItemStack stack) {
		return indexOfIStack(CraftTweakerMC.getIItemStack(stack));
	}
	
	@ZenMethod("indexOf")
	@ZenDoc("Returns the index of this stack in this scroll group (where 0 is the first item in the group), or -1 if it is not present.")
	public int indexOfIStack(IItemStack istack) {
		for(int i = 0; i < items.size(); i++) {
			if(items.get(i).matches(istack)) return i;
		}
		
		return -1;
	}
	
	public ItemStack next(ItemStack stack) {
		return rotate(stack, 1);
	}
	
	public ItemStack prev(ItemStack stack) {
		return rotate(stack, -1);
	}
	
	private ItemStack rotate(ItemStack stack, int off) {
		IItemStack curr = CraftTweakerMC.getIItemStack(stack);
		
		int i = indexOfIStack(curr);
		if(i == -1) return stack; //not found!
		
		i += off;
		if(i >= items.size()) i = 0;
		if(i <= -1) i = items.size() - 1;
		
		IItemStack next = items.get(i);
		return CraftTweakerMC.getItemStack(processor.apply(curr, next));
	}
	
	public void removeIf(Predicate<IItemStack> condition) {
		items.removeIf(condition);
	}
	
	@Override
	@Nonnull
	public Iterator<IItemStack> iterator() {
		return items.iterator();
	}
	
	@Override
	public String toString() {
		return Etc.ingredientListToString(items);
	}
	
	public boolean checkPermission(EntityPlayer player) {
		if(creativeOnly) return player.isCreative();
		else return true;
	}
}
