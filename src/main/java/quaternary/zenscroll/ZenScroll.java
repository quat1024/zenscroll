package quaternary.zenscroll;

import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
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
import quaternary.zenscroll.util.ScrollGroup;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.Arrays;
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
		public static void add(IItemStack... stacks) {
			//Flatten the list
			List<IItemStack> stacksList = Arrays.stream(stacks)
				.flatMap(i -> i.getItems().stream())
				.map(i -> i.withAmount(1))
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
				.collect(Collectors.toList());
			
			ACTIONS.add(new IAction() {
				@Override
				public void apply() {
					scrollGroups.add(new ScrollGroup(stacksList));
				}
				
				@Override
				public String describe() {
					return "Adding scroll group with items :" + stacksList.stream().map(i -> CraftTweakerMC.getItemStack(i).toString()).collect(Collectors.joining(", "));
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
					return "Removing all scroll groups";
				}
			});
		}
		
		@ZenMethod
		public static void remove(IItemStack stack) {
			ItemStack stack2 = CraftTweakerMC.getItemStack(stack);
			
			ACTIONS.add(new IAction() {
				@Override
				public void apply() {
					scrollGroups.removeIf(group -> group.containsStack(stack2));
				}
				
				@Override
				public String describe() {
					return "Removing all scroll groups that contain itemstack " + stack2.toString();
				}
			});
		}
	}
}