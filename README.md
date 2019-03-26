ZenScroll
=========

You can scroll your mousewheel to cycle between different items. A little bit like Xtones.

Configure it with CraftTweaker (see below). It doesn't ship with any scroll groups by default.

# LICENSE and stuff

Mozilla Public License 2.0, or any later version. Here: https://www.mozilla.org/en-US/MPL/2.0/

## Players:

Check the config file. There are options to:

- reverse scroll direction
- enable or disable a little tooltip that appears on items you can scroll-cycle

Also check your keybindings screen. The scrolling modifier defaults to left Alt.

## Modpack authors:

You can configure this mod through CraftTweaker! Yeah!

### Class `mods.zenscroll.ZenScroll`

This class relates to registering scroll groups with the game.

#### `ScrollGroup ZenScroll.add(IIngredient... ingredients)`

Registers a scroll group consisting of the listed items. This is a varargs method; it can be called either on an array of IIngredients, or by just specifying them one by one.

Players will be able to scroll to cycle between the specified items if they hold their modifier key. (Any item stack counts will be ignored.) See below for the specific behaviors of how the ingredients list is processed.

This returns a ScrollGroup object you can play with.

This is shorthand for `ZenScroll.add(ScrollGroup.of(ingredients))`.

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
    
    //Using an ore dictionary key.
    ZenScroll.add(oreDict.get("dye"));
    ZenScroll.add(oreDict.get("fenceWood"));

#### `ScrollGroup ZenScroll.add(ScrollGroup group)`

Adds a ScrollGroup object, the "long way". Returns the same object.

#### `ScrollGroup findGroup(IIngredient ingredient)`

Returns the registered ScrollGroup that this item is in, or `null` if it's not in any registered groups.

#### `void ZenScroll.removeAll()`

Unregisters all scroll groups, as if you had never ever called `add`.

#### `void ZenScroll.removeFromAll(IIngredient... ingredients)`

Removes these items from all registered scroll groups.

#### `void ZenScroll.removeGroupsContaining(IIngredient... ingredients)`

Unregisters all scroll groups containing these items.

#### `void ZenScroll.beQuietIKnowWhatImDoing()`

Disables logs and info warnings.

### class `mods.zenscroll.ScrollGroup`

This class represents a scroll group. It's a single list of items that can be cycled by users when they hold the modifier key and scroll the mouse wheel. Call `ZenScroll.add` to register this group in-game.

#### `ScrollGroup ScrollGroup.of(IIngredient... ingredients)`

The constructor. The ingredient input is processed like so:

* Flattens ingredients into their component items.
  * For example, this flattens an `IOreDictEntry` to the items that have that ore tag.
  * Or flattens `or`-d items into their components.
  * Does nothing on things that are already items!
* Removes all empty items.
* Removes all duplicate entries.
* Flattens all wildcards.
  * Specifically, any item with a wildcard data value is replaced with the items that appear in the creative Search tab.
* Sets all stack counts to 1 just in case.

This method takes varargs, so you can either call it as if it has a million arguments, or just pass in a single array of IIngredients.

While this method is complicated internally, it's really just that way to be as flexible as possible. Just know that you can call this method on any IIngredient or array of IIngredients. A lot of things are IIngredients already, like item stacks (`<minecraft:apple>`), ore keys (`oreDict.get("dye")`), and idk probably more?

Since item stacks are also `IIngredients`, you can just call this on a list of item stacks just fine.

#### `ScrollGroup ScrollGroup#processor(ScrollProcessor func)`

Sets the "processor function" of this scroll group. The processor function is called every time the player scrolls once. The processor function takes two IItemStack arguments - the first argument is the IItemStack the player is scrolling *from*, and the second is the IItemStack the player is scrolling *to*. It returns a third itemstack, which will be the one actually set into the player's hand.

The default processor simply ignores the previous item, and always returns the next one, like this:

    function(prev as IItemStack, next as IItemStack) {
      return next;
    }

You can use this function to transform the scrolled item in various interesting ways.

For example, this looks like a group between all stained glass items, but when you scroll it actually turns into an apple:

    var glassGroup = ScrollGroup.of(<minecraft:stained_glass:*>);
    
    glassGroup.processor(
      function(prev as IItemStack, next as IItemStack) {
        return <minecraft:apple>;
      }
    );

Of course this example is quite silly; you'd probably want to return some variant of `next`.

This method returns the same scrollgroup you called it on, so you can use also just use it like this:

    var glassGroup = ScrollGroup.of(<minecraft:stained_glass:*>).processor(
      function(prev as IItemStack, next as IItemStack) {
        return <minecraft:apple>;
      }
    );

Or even like this:

    ZenScroll.add(<minecraft:stained_glass:*>).processor(...);
    
Note that my JEI handler does not take into account the processor function, so anything *really* wacky you do with this function will probably not appear correctly in JEI.

The functional interface you're implementing with this, btw, is `mods.zenscroll.ScrollProcessor`. If you want to save and reuse these processor functions in variables might want to import that.
    
#### `ScrollGroup ScrollGroup#copyTag`

Sets the processor function to this:

    function(prev as IItemStack, next as IItemStack) {
      if(prev.hasTag) {
        return next.withTag(prev.tag, false);
      } else {
        return next.withEmptyTag();
      }
    }

This processor function just copies the tag from the previous item onto the next item, i.e. when you scroll, all NBT data is preserved.

This function is just shorthand, it's exactly the same as manually setting the processor function to this. It's just a very common use case of processor functions.

#### `boolean ScrollGroup#contains(IItemStack stack)`

Returns whether this scroll group has this stack in it.

#### `boolean ScrollGroup#indexOf(IItemStack stack)`

Returns the index of this stack in the scroll group. 0 is the first item, 1 is the second, and so on.

#### `IItemStack[] ScrollGroup#items`

(to clarify, this means `myAwesomeScrollGroup.items`)

The items in this scroll group. You can change this array or whatever.

#### `ScrollGroup ScrollGroup#creativeOnly(boolean isCreativeOnly)`

Sets this scroll group as creative only. Creative only groups will not show a tooltip or respond to scrolls in non-creative game modes, and do not show in JEI. Also returns itself so you can chain function calls.

The boolean parameter is optional and defaults to `true`.

#### `boolean ScrollGroup#isCreativeOnly()`

Guess what this does.