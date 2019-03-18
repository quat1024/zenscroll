ZenScroll
=========

You can scroll your mousewheel to cycle between different items. A little bit like Xtones.

Configure it with CraftTweaker (see below). It doesn't ship with any scroll groups by default.

## Players:

Check the config file. There are options to:

- change the modifier key you need to press to enable scroll-cycling
- reverse scroll direction
- enable or disable a little tooltip that appears on items you can scroll-cycle
- disable the effect entirely for your client

## Modpack authors:

You can configure this mod through CraftTweaker!

First, import ZenScroll to your script: `import mods.zenscroll.ZenScroll;`

Here are the functions you can call:

**`ZenScroll.add(IItemStack... group)`**: Adds a scroll group consisting of the listed items.

Players will be able to scroll to cycle between the specified items if they hold their modifier key. Any item stack counts will be ignored.

Examples:

    //Just a list of items.
    ZenScroll.add(<minecraft:apple>, <minecraft:gravel>, <minecraft:dirt>);
    
    //Adding some data on items.
    ZenScroll.add(<minecraft:apple>, <minecraft:sand>.withLore(["Very suspicious..."]));
    //Players will be able to scroll apples into suspicions sand, and vice versa.
    //However they will not be able to scroll regular sand into apples.
    
    //Using wildcards.
    ZenScroll.add(<minecraft:wool:*>);
    //Any wildcarded items will be expanded out into whatever shows up in the SEARCH creative tab.

**`ZenScroll.removeAll()`**: Clear all scroll groups.

Example:

    ZenScroll.removeAll();
    //Where'd they all go?

**`ZenScroll.remove(IItemStack stack)`**: Remove all scroll groups containing this item.

Example:

    ZenScroll.add(<minecraft:apple>, <minecraft:gravel>, <minecraft:dirt>);
    ZenScroll.remove(<minecraft:gravel>);
    //Now there's no scroll groups!

Let me know if this API sucks. I'm not familiar with what you ZenScript people like. :)

# LICENSE and stuff

Mozilla Public License 2.0, or any later version. Here: https://www.mozilla.org/en-US/MPL/2.0/