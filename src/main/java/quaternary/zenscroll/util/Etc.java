package quaternary.zenscroll.util;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Etc {
	public static List<IItemStack> flattenIIngredients(IIngredient... in) {
		return Arrays.stream(in)
			.flatMap(ing -> ing.getItems().stream())
			.filter(item -> !item.isEmpty())
			.distinct()
			.collect(Collectors.toList());
	}
	
	public static String ingredientListToString(List<? extends IIngredient> stacks) {
		return stacks.stream().map(IIngredient::toCommandString).collect(Collectors.joining(", "));
	}
}
