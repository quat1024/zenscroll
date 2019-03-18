package quaternary.zenscroll.util;

import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ScrollGroup extends ArrayList<IItemStack> {
	public ScrollGroup(List<IItemStack> in) {
		super(in);
	}
	
	public boolean containsStack(ItemStack stack) {
		return indexOfStack(stack) != -1;
	}
	
	public boolean containsStack(IItemStack istack) {
		return indexOfStack(istack) != -1;
	}
	
	public int indexOfStack(ItemStack stack) {
		return indexOfStack(CraftTweakerMC.getIItemStack(stack));
	}
	
	public int indexOfStack(IItemStack istack) {
		for(int i = 0; i < size(); i++) {
			if(get(i).matches(istack)) return i;
		}
		
		return -1;
	}
	
	public ItemStack next(ItemStack stack) {
		int i = indexOfStack(stack);
		if(i == -1) return stack; //Not found = no change
		
		i++;
		if(i == size()) i = 0;
		return CraftTweakerMC.getItemStack(get(i)).copy();
	}
	
	public ItemStack prev(ItemStack stack) {
		int i = indexOfStack(stack);
		if(i == -1) return stack;
		
		i--;
		if(i == -1) i = size() - 1;
		return CraftTweakerMC.getItemStack(get(i)).copy();
	}
}
