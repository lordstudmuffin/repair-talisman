# Repair Talisman

**One craftable talisman. Everything you carry repairs itself.**

Repair Talisman adds a single item: a gold-and-amethyst charm with a diamond at its heart. Keep it anywhere in your inventory and every few seconds it restores one point of durability on every damaged item you have on you: the tools in your hotbar, the armor you are wearing, the spare gear in your inventory and whatever is in your offhand.

The old repair-charm mods stopped at 1.21.1. This one is written fresh for Minecraft 26.1 on Fabric.

## Features

- **Standalone.** Needs Fabric Loader only. No Fabric API, no Trinkets, no library mods.
- **Lightweight.** One item, one recipe, one small config file. No commands, no keybinds, no network packets.
- **Repairs everything you carry.** Hotbar, main inventory, all four armor slots and the offhand. Tools, weapons, armor, elytra, shields, fishing rods, flint and steel, shears and any modded item with durability.
- **Plays nice.** Stacks with Mending and Unbreaking, skips Unbreakable items, and carrying two talismans does not double the effect.
- **Survival friendly.** Craftable in a normal crafting table, unlocks in the recipe book when you pick up a diamond, shows up in EMI / REI / JEI.
- **Configurable.** Repair rate, amount per pulse, hotbar-only mode and armor / offhand toggles.
- Unbreakable, fire resistant (survives lava) and has an enchanted glint so you can spot it.

## Crafting

Amethyst Shards in the corners, Gold Ingots on the edges, a Diamond in the middle.

![Repair Talisman recipe](../images/recipe.png)

Text version: `A G A / G D G / A G A` with A = Amethyst Shard, G = Gold Ingot, D = Diamond.

## Config

`config/repairtalisman.properties` is created on first launch:

- `intervalTicks` (default 60): ticks between repair pulses, 20 ticks = 1 second
- `repairAmount` (default 1): durability restored per damaged item per pulse
- `onlyInHotbar` (default false): if true, the talisman only works while it sits in the hotbar
- `repairArmor` (default true) and `repairOffhand` (default true)

Edit the file and restart the game. The talisman's tooltip shows the current rate.

## Installation

1. Install Fabric Loader (0.18 or newer) for Minecraft 26.1.x. Java 25 comes with the launcher.
2. Put `repairtalisman-1.0.1.jar` in your `mods` folder. That is all.

For servers, install it on both the server and every client, as with any item mod.

## FAQ

**Does it need Fabric API?** No. It works with or without it.

**Does it work from a Trinkets or Accessories slot?** No. Those are not inventory slots. Keep it in your inventory; any slot is fine.

**Does it repair items in chests or backpacks?** No, only what the player is carrying.

**Is it balanced?** The default rate is one durability point per item every three seconds, slower than Mending with a steady XP supply, but it runs on everything at once. Server owners can slow it down or restrict it to the hotbar in the config.

**Modpacks?** Yes, go ahead. Credit is appreciated but not required.

License: MIT. Source and issues: https://github.com/lordstudmuffin/repair-talisman
