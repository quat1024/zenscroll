package quaternary.zenscroll;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import quaternary.zenscroll.util.Etc;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;

@ZenClass("mods.zenscroll.ZenScroll")
@ZenRegister
//The zs doesn't stand for zenscroll it stands for zenscript.
public class ZSHandler {
	public static boolean ENABLE_LOGGING = true;
	
	@ZenMethod
	public static void beQuietIKnowWhatImDoing() {
		ENABLE_LOGGING = false;
	}
	
	@ZenMethod
	@ZenDoc("Registers a scroll group consisting of the specified ingredients. Shorthand for 'ZenScroll.add(ScrollGroup.of(...))'.")
	public static ScrollGroup add(IIngredient... ingredients) {
		if(nullCheck(ingredients, "add", "ingredients")) return null;
		return add(ScrollGroup.of(ingredients));
	}
	
	@ZenMethod
	@ZenDoc("Registers a scroll group. You cannot register two scroll groups that share any items.")
	public static ScrollGroup add(ScrollGroup group) {
		if(nullCheck(group, "add", "group")) return null;
		
		if(!containsDuplicate(group)) {
			info("Adding scroll group with items %s", group.toString());
			ZenScroll.scrollGroups.add(group);
		}
		
		return group;
	}
	
	private static boolean containsDuplicate(ScrollGroup group) {
		for(IItemStack istack : group) {
			for(ScrollGroup otherGroup : ZenScroll.scrollGroups) {
				if(otherGroup.containsIStack(istack)) {
					warn("[ZenScroll] Skipping adding group '%s', since %s is already in a scroll group", group.toString(), istack.toCommandString());
					return true;
				}
			}
		}
		
		return false;
	}
	
	@ZenMethod
	@ZenDoc("Returns the scroll group that contains this ingredient, or 'null' if none do.")
	public static ScrollGroup findGroup(IIngredient ingredient) {
		if(nullCheck(ingredient, "find", "ingredient")) return null;
		
		List<IItemStack> istacks = Etc.flattenIIngredients(ingredient);
		
		for(ScrollGroup group : ZenScroll.scrollGroups) {
			for(IItemStack istack : istacks) {
				if(group.containsIStack(istack)) return group;
			}
		}
		
		return null;
	}
	
	@ZenMethod
	public static List<ScrollGroup> all() {
		return ZenScroll.scrollGroups;
	}
	
	@ZenMethod
	@ZenDoc("Clears all scroll groups, as if you had never registered any at all.")
	public static void removeAll() {
		info("Clearing all scroll groups");
		ZenScroll.scrollGroups.clear();
	}
	
	@ZenMethod
	@ZenDoc("Removes all scroll groups containing this ingredient.")
	public static void removeGroupsContaining(IIngredient... ingredients) {
		if(nullCheck(ingredients, "removeGroupsContaining", "ingredients")) return;
		
		List<IItemStack> istacks = Etc.flattenIIngredients(ingredients);
		String msg = Etc.ingredientListToString(istacks);
		
		istacks.forEach(istack -> {
			info("Removing any groups that contain %s", istack.toCommandString());
			ZenScroll.scrollGroups.removeIf(group -> group.containsIStack(istack));
		});
		
	}
	
	@ZenMethod
	@ZenDoc("Removes this ingredient from all scroll groups containing it.")
	public static void removeFromAll(IIngredient... ingredients) {
		if(nullCheck(ingredients, "remove", "ingredients")) return;
		
		for(IItemStack istack : Etc.flattenIIngredients(ingredients)) {
			info("Removing %s from any groups it's in", istack.toCommandString());
			ZenScroll.scrollGroups.forEach(group -> group.removeIf(groupistack -> groupistack.matches(istack)));
		}
	}
	
	//returns true if it's null for ez mode early-exiting from functions
	private static boolean nullCheck(Object thing, String func, String arg) {
		if(thing == null) {
			warn(func + " called with null argument " + arg);
			return true;
		}
		
		return false;
	}
	
	private static void info(String message, Object... fmt) {
		if(ENABLE_LOGGING)	CraftTweakerAPI.logInfo(logFmt(message, fmt));
	}
	
	private static void warn(String message, Object... fmt) {
		if(ENABLE_LOGGING) CraftTweakerAPI.logWarning(logFmt(message, fmt));
	}
	
	private static String logFmt(String message, Object... fmt) {
		return String.format(
			"[%s] %s %s",
			ZenScroll.NAME,
			CraftTweakerAPI.getScriptFileAndLine(),
			String.format(message, fmt)
		);
	}
}
