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

## Quick Start
```zenscript
import mods.zenscroll.ZenScroll;
import mods.zenscroll.ScrollGroup;

//Adding a simple scroll group between 2 items.
ZenScroll.add(<minecraft:apple>, <minecraft:gravel>);

//Adding a scroll group with some data - here, it's lore.
ZenScroll.add(<minecraft:grass>, <minecraft:stone>.withLore(["Smells like grass."]);
//You can scroll grass into stone with lore, and stone with lore into grass.
//But you can't scroll regular stone into grass.
//The tag has to match.

//Adding a scroll group using wildcarded items.
ZenScroll.add(<minecraft:glass>, <minecraft:stained_glass:*>);
//Expands to a scroll group with 17 items - regular glass and all 16 stained glasses.

//Adding a scroll group from an ore dictionary key.
//You can scroll all dyes into each other, and also into all sticks. Reverse is true as well.
ZenScroll.add(oreDict.get("dye"), oreDict.get("stickWood"));

//Adding a scroll group using a mod.
ZenScroll.add(loadedMods["minecraft"].items);
//Now you can scroll literally every item from Minecraft into every other item from Minecraft.

//Adding a scroll group that works only in creative.
ZenScroll.add(<minecraft:wool:*>).creativeOnly();

//Adding a scroll group that preserves NBT, the "easy way".
ZenScroll.add(<minecraft:wool:*>).copyTag();

//Why_cant_we_have_both.gif
ZenScroll.add(<minecraft:wool:*>).copyTag().creativeOnly();
ZenScroll.add(<minecraft:wool:*>).creativeOnly().copyTag();

//Adding a scroll group that preserves NBT, "the hard way".
ZenScroll.add(<minecraft:wool:*>).processor(
  if(prev.hasTag) {
    return next.withTag(prev.tag, false);
  } else {
    return next.withEmptyTag();
  }
);
//copyTag is an alias to that processor function.

//Using a standalone ScrollGroup object.
var coolGroup = ScrollGroup.of(<minecraft:stained_hardened_clay:*>, <minecraft:stick>);
coolGroup.copyTag();
coolGroup.creativeOnly();

for item in coolGroup.items {
  //Do something???
}

ZenScroll.add(coolGroup);
//ZenScroll.add(IIngredient...) is an alias for ZenScroll.add(ScrollGroup.of(IIngredient...)).
```
## Full docs

Throughout this document:

* `SomeClass.someMethod` with a *dot* refers to a *static* method, that you call directly on the class, such as `ScrollGroup.of(...)`
* `SomeClass#someOtherMethod` with a *pound sign* refers to an *instance* method, that you call on an *instance* of that class, like `myScrollGroup.creativeOnly();`

Got it? Ok, let's get started.

### Class `mods.zenscroll.ZenScroll`

This class relates to registering scroll groups to the game.

#### `ScrollGroup ZenScroll.add(IIngredient... ingredients)`

Registers a scroll group consisting of the listed items. This is a varargs method; it can be called either on an array of IIngredients, or by just specifying them one by one.

Players will be able to scroll to cycle between the specified items if they hold their modifier key. (Any item stack counts will be ignored.) See below for the specific behaviors of how the ingredients list is processed.

This returns a ScrollGroup object you can play with.

This is shorthand for `ZenScroll.add(ScrollGroup.of(ingredients))`.

Examples:

```zenscript
//Just a list of items.
ZenScroll.add(<minecraft:apple>, <minecraft:gravel>, <minecraft:dirt>);

//Adding some data on items.
ZenScroll.add(<minecraft:apple>, <minecraft:sand>.withLore(["Very suspicious..."]));
//Players will be able to scroll apples into suspicious sand, and vice versa.
//However they will not be able to scroll regular sand into apples.

//Using wildcards.
ZenScroll.add(<minecraft:wool:*>);
//Any wildcarded items will be expanded out into whatever shows up in the SEARCH creative tab.

//Using an ore dictionary key.
ZenScroll.add(oreDict.get("dye"));
ZenScroll.add(oreDict.get("fenceWood"));
```

#### `ScrollGroup ZenScroll.add(ScrollGroup group)`

Adds a ScrollGroup object, the "long way". Returns the same object.

#### `ScrollGroup ZenScroll.findGroup(IIngredient ingredient)`

Returns the registered ScrollGroup that this item is in, or `null` if it's not in any registered groups.

#### `ScrollGroup[] ZenScroll.all()`

Returns all the registered scrollgroups.

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

While this method is complicated internally, it's really just that way to be as flexible as possible. Just know that you can call this method on any IIngredient or array of IIngredients. A lot of things are IIngredients already, like item stacks (`<minecraft:apple>`), ore keys (`oreDict.get("dye")`), and more!

Since item stacks are also `IIngredients`, you can just call this on a list of item stacks just fine.

#### `ScrollGroup ScrollGroup#processor(ScrollProcessor func)`

Sets the "processor function" of this scroll group. The processor function is called every time the player scrolls once. The processor function takes two IItemStack arguments - the first argument is the IItemStack the player is scrolling *from*, and the second is the IItemStack the player is scrolling *to*. It returns a third itemstack, which will be the one actually set into the player's hand.

The default processor simply ignores the previous item, and always returns the next one, like this:

```zenscript
function(prev as IItemStack, next as IItemStack) {
  return next;
}
```

You can use this function to transform the scrolled item in various interesting ways.

For example, this looks like a group between all stained glass items, but when you scroll it actually turns into an apple:

```zenscript
var glassGroup = ScrollGroup.of(<minecraft:stained_glass:*>);

glassGroup.processor(
  function(prev as IItemStack, next as IItemStack) {
    return <minecraft:apple>;
  }
);
```

Of course this example is quite silly; you'd probably want to return some variant of `next`.

`processor` itself returns the same scrollgroup you called it on, so you can also chain calls like this:

```zenscript
var glassGroup = ScrollGroup.of(<minecraft:stained_glass:*>).processor(
  function(prev as IItemStack, next as IItemStack) {
    return <minecraft:apple>;
  }
);
```

Or even like this:

```zenscript
ZenScroll.add(<minecraft:stained_glass:*>).processor(...);
```
    
Note that my JEI handler does not take into account the processor function, so anything *really* off-the-wall you do with this function will not appear correctly in JEI.

The functional interface you're implementing by writing processor functions, btw, is `mods.zenscroll.ScrollProcessor`. If you want to save and reuse these processor functions in variables might want to import that.
    
#### `ScrollGroup ScrollGroup#copyTag`

Sets the processor function to this:

```zenscript
function(prev as IItemStack, next as IItemStack) {
  if(prev.hasTag) {
    return next.withTag(prev.tag, false);
  } else {
    return next.withEmptyTag();
  }
}
```

This processor function just copies the tag from the previous item onto the next item, i.e. when you scroll, all NBT data is preserved.

This function is just shorthand, it's exactly the same as manually setting the processor function to this. It's just a very common use case of processor functions.

#### `ScrollGroup ScrollGroup#eraseTag`

Resets the processor function to its default value of:

```zenscript
function(prev as IItemStack, next as IItemStack) {
  return next;
}
```

#### `ScrollGroup ScrollGroup#matcher(ItemMatcher func)`

**A word of warning: This is probably the hardest to understand function in ZenScroll. Thankfully, you don't need it that often, but it's there if you do.**

Sets the "item matcher" of this scroll group. When a player performs a scroll operation on one of their `IItemStack`s, ZenScroll first needs to detect which group their stack is a part of, and which stack in the group corresponds to their stack. Up to once for every item in every scroll group, ZenScroll will check if the items are the "same". If the items are the "same", the scroll operation will continue from that point. The purpose of the item matcher function is to define this concept of "sameness". It can be configured differently for each scroll group.

The item matcher function is a function that takes two IItemStacks and returns a boolean. The first argument is one of the members of this scroll group, and the second argument is the stack the player is attempting to scroll. 

Here is the default matcher function:

```zenscript
function(group as IItemStack, scrolled as IItemStack) {
  return group.matches(scrolled);
}
```

This performs the same sort of logic that crafting table recipe matching does: if the stack the player is attempting to scroll has *at least* all of the NBT tags as the stack in the scroll group, they are considered to be a match.

`matcher` returns the same `ScrollGroup` you called it on, for easy chaining.

Note that my JEI handler does not take into account the matcher function, so anything *really* off-the-wall you do with this function will not appear correctly in JEI.

The functional interface you're implementing by writing matcher functions, btw, is `mods.zenscroll.ItemMatcher`. If you want to save and reuse these matcher functions in variables might want to import that.

#### `ScrollGroup ScrollGroup#strictMatch()`

Sets the matcher function to this:

```zenscript
function(group as IItemStack, scrolled as IItemStack) {
  return group.matchesExact(scrolled)
}
```

This matcher function only matches if the stack in the group and the player's scrolled stack have *exactly the same* NBT tag; *any* additional NBT tags on the player's scrolled stack will cause a mismatch.

#### `ScrollGroup ScrollGroup#lenientMatch()`

Resets the matcher function to its default value of:

```zenscript
function(group as IItemStack, scrolled as IItemStack) {
  return group.matches(scrolled);
}
```

#### `ScrollGroup ScrollGroup#veryLenientMatch()`

Sets the matcher function to this:

```zenscript
function(group as IItemStack, scrolled as IItemStack) {
  return group.definiton.id == scrolled.definition.id && group.metadata == scrolled.metadata;
}
```

This matcher function effectively completely ignores the NBT tags on both the group's stack and the player's scrolled stack for purposes of matching. Note that specifying NBT on items in the scroll group still has a purpose even on a `veryLenientMatch`-ed group: scrolling *to* an item with NBT will still create an item with that NBT tag.

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

### class `mods.zenscroll.ScrollProcessor`

This is the functional interface used for processor functions.

#### `IItemStack ScrollProcessor#apply(IItemStack prev, IItemStack next)`

Run the scroll processor.

---

This interface has a few static fields of its own, used to reference the default scroll processor functions, if you want to use them (potentially inside another processor):

#### `ScrollProcessor.ERASE_TAG`

The default scroll processor, that replaces the scrolled stack's tag completely.

#### `ScrollProcessor.COPY_TAG`

The scroll processor you can set via `ScrollGroup#copyTag()`, that copies the source tag to the destination.

### class `mods.zenscroll.ItemMatcher`

This is the functional interface used for matcher functions.

#### `boolean matches(IItemStack group, IItemStack scrolled)`

Run the matcher function.

---

This interface has a few static fields of its own, used to reference the built-in matcher functions, if you want to use them (potentially inside another matcher):

#### `ItemMatcher.LENIENT`

The default matcher, that checks whether the scrolled stack's tag is a superset of the one in the group.

#### `ItemMatcher.STRICT`

The matcher accessed via `ScrollGroup#strictMatch()`, that checks whether the scrolled stack's tag and the group's stack's tag are completely identical.

#### `ItemMatcher.VERY_LENIENT`

The matcher accessed via `ScrollGroup#veryLenientMatch()`, that checks only whether the scrolled stack and the group's stack have the same item definition and metadata.