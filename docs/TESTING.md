# Repair Talisman 1.0.1: test plan (Fabric 26.1.2)

Instance: a NEW CurseForge custom profile, Minecraft 26.1.2, loader Fabric (latest 0.18.x). The "Test" instance is
NeoForge 1.21.1 and cannot load this jar. Only `repairtalisman-1.0.1.jar` goes in `mods`; no Fabric API needed
(adding it later is fine).

1. Launch. Log should contain "Repair Talisman loaded: repairs 1 durability every 60 ticks" and no mixin errors
   (search `latest.log` for `repairtalisman`, `Mixin apply failed` and `already frozen`). `config/repairtalisman.properties` exists.
2. Creative: the talisman is in Tools & Utilities and in search; it has a texture, name "Repair Talisman", a glint and
   a grey tooltip line with the rate.
3. Survival crafting: 4 amethyst shards + 4 gold ingots + 1 diamond in the AGA/GDG/AGA shape gives one talisman. Pick
   up a diamond first: the recipe book unlocks it.
4. Repair: damage a pickaxe, a chestplate (wear it), a shield (offhand) and a spare sword in the backpack rows. With the
   talisman anywhere in the inventory each gains 1 durability every 3 seconds (hover to watch the bar). Remove the
   talisman: repairs stop. Two talismans: same speed, not double.
5. Edge cases: an Unbreakable item (give with the component) is untouched; a Mending item still mends from XP as well;
   the talisman itself never takes damage and survives lava.
6. Multiplayer (optional): same jar on a Fabric 26.1.2 server and client; repair happens server side and the client bar
   updates.
7. Optional: add Fabric API to `mods` and launch again; everything above still works, with no duplicate pack warnings.
If step 1 shows a mixin failure, send `latest.log` back to the thread.
