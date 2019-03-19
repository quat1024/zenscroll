package quaternary.zenscroll;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import quaternary.zenscroll.config.ZenScrollConfig;
import quaternary.zenscroll.net.PacketHandler;
import quaternary.zenscroll.util.Etc;
import quaternary.zenscroll.util.ScrollGroup;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod(
	modid = ZenScroll.MODID,
	name = ZenScroll.NAME,
	version = ZenScroll.VERSION,
	guiFactory = "quaternary.zenscroll.config.ConfigGuiBoilerplateA"
)
public class ZenScroll {
	public static final String MODID = "zenscroll";
	public static final String NAME = "ZenScroll";
	public static final String VERSION = "GRADLE:VERSION";
	
	public static final Logger LOG = LogManager.getLogger(NAME);
	
	public static final List<ScrollGroup> scrollGroups = new ArrayList<>();
	
	@Mod.EventHandler
	public static void preinit(FMLPreInitializationEvent e) {
		ZenScrollConfig.preinit(e);
		PacketHandler.preinit();
	}
	
	@Mod.EventHandler
	public static void postinit(FMLPostInitializationEvent e) {
		ZSHandler.ACTIONS.forEach(IAction::apply);
	}
	
	@ZenClass("mods.zenscroll.ZenScroll")
	@ZenRegister
	public static class ZSHandler {
		public static final List<IAction> ACTIONS = new ArrayList<>();
		
		@ZenMethod
		public static void add(IIngredient... ingredients) {
			ACTIONS.add(new IAction() {
				private String stacksString;
				
				@Override
				public void apply() {
					if(ingredients == null) {
						CraftTweakerAPI.logWarning("[ZenScroll] add() called with null ingredients!");
						return;
					}
					
					//Flatten the list
					List<IItemStack> stacks = Etc.flattenIIngredients(ingredients).stream()
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
					
					stacksString = Etc.ingredientListToString(stacks);
					
					for(IItemStack istack : stacks) {
						for(ScrollGroup group : scrollGroups) {
							if(group.containsStack(istack)) {
								CraftTweakerAPI.logWarning("[ZenScroll] Skipping group '" + stacksString + "', since " + istack.toCommandString() + " is already in a scroll group.");
								return;
							}
						}
					}
					
					scrollGroups.add(new ScrollGroup(stacks));
				}
				
				@Override
				public String describe() {
					return "[ZenScroll] Adding scroll group with items :" + stacksString;
				}
			});
		}
		
		@ZenMethod
		public static void removeAll() {
			ACTIONS.add(new IAction() {
				@Override
				public void apply() {
					scrollGroups.clear();
				}
				
				@Override
				public String describe() {
					return "[ZenScroll] Removing all scroll groups";
				}
			});
		}
		
		@ZenMethod
		public static void removeGroupsContaining(IIngredient... ingredients) {
			ACTIONS.add(new IAction() {
				private String logString;
				
				@Override
				public void apply() {
					if(ingredients == null) {
						CraftTweakerAPI.logWarning("[ZenScroll] removeGroupsContaining() called with null ingredients!");
						return;
					}
					
					List<IItemStack> stacks = Etc.flattenIIngredients(ingredients);
					stacks.forEach(stack -> scrollGroups.removeIf(group -> group.containsStack(stack)));
					logString = "[ZenScroll] Removing any groups that contain " + Etc.ingredientListToString(stacks);
				}
				
				@Override
				public String describe() {
					return logString;
				}
			});
		}
		
		@ZenMethod
		public static void remove(IIngredient... ingredients) {
			ACTIONS.add(new IAction() {
				private String logString;
				
				@Override
				public void apply() {
					if(ingredients == null) {
						CraftTweakerAPI.logWarning("[ZenScroll] remove() called with null ingredients!");
						return;
					}
					
					List<IItemStack> stacks = Etc.flattenIIngredients(ingredients);
					
					for(IItemStack stack : stacks) {
						scrollGroups.forEach(group -> group.removeIf(gstack -> gstack.matches(stack)));
					}
					
					logString = "[ZenScroll] Removing '" + Etc.ingredientListToString(stacks) + "' from any groups it's in";
				}
				
				@Override
				public String describe() {
					return logString;
				}
			});
		}
	}
}