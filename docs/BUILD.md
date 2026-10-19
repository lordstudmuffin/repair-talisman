# Building Repair Talisman without Gradle

Built 2026-10-19 by the Repair Talisman thread. Reuse for every Fabric 26.1 mod.

## Facts that make Fabric 26.1 easy
- Minecraft 26.1 is unobfuscated: Fabric runs with Mojang names, no intermediary, no remapping, no refmap
  (fabricmc.net/2026/03/14/261.html). A jar compiled against the Mojang-named stubs loads as is.
- Players need Fabric Loader 0.18.x+ (current stable when checked: 0.18.4) and Java 25.
- Mixin ships inside Fabric Loader; Fabric's fork `net.fabricmc:sponge-mixin:0.17.5+mixin.0.8.7` is on
  repo1.maven.org (reachable from the container). MixinExtras 0.5.5 (`io.github.llamalad7:mixinextras-common`) too.
  `net.fabricmc:fabric-loader` itself is NOT on central (maven.fabricmc.net is blocked); the loader API is stubbed here.

## What is here
- `sponge-mixin-0.17.5+mixin.0.8.7.jar`: real Mixin annotations/classes for compiling mixins.
- `fabric-loader-api-stubs.jar`: compile-only `net.fabricmc.api.ModInitializer/ClientModInitializer/...` (real files),
  and hand-written `net.fabricmc.loader.api.FabricLoader/ModContainer/Version/metadata.ModMetadata` with the members
  we use (getInstance, getModContainer, getConfigDir, getRootPaths, ...). Add members from the real
  github.com/FabricMC/fabric-loader sources when needed; sources in `src/fabric-stubs-src.zip`.
- `slf4j-shim.jar`: compile-only org.slf4j Logger/LoggerFactory (the stubs jar's copies are empty shells). Put it FIRST
  on the classpath. Never ship it; slf4j is real at runtime.
- Minecraft classes come from `/mnt/project-files/mc261-toolchain/compiled-stubs-26.1.jar` (NeoForge-patched, but the
  vanilla signatures are the same; just don't touch `net.neoforged.*`).

## How to compile
```
CP=slf4j-shim.jar:fabric-loader-api-stubs.jar:sponge-mixin-0.17.5+mixin.0.8.7.jar:/mnt/project-files/mc261-toolchain/compiled-stubs-26.1.jar
javac --release 21 -proc:none -cp "$CP" -d out $(find src/main/java -name '*.java')
jar cf mymod.jar -C out . -C src/main/resources .
python3 /mnt/project-files/mc261-toolchain/jarlinkcheck.py mymod.jar /mnt/project-files/mc261-toolchain/compiled-stubs-26.1.jar
```
Expected link-check noise: nested-class constructors (Pack$Metadata, Item$Properties), org.slf4j, net.fabricmc.*.

## Standalone (no Fabric API) patterns, all proven in Repair Talisman
- Mod resources: Fabric Loader does NOT expose a mod jar's assets/ and data/ to the game; Fabric API's resource loader
  does that. Without it, mixin `PackRepository.<init>` at RETURN, replace the `sources` set with a LinkedHashSet and add
  a RepositorySource that builds a `Pack` over `FabricLoader.getModContainer(id).getRootPaths()` using
  `PathPackResources` (works on the loader's zip-filesystem path). Construct `new Pack(location, supplier,
  new Pack.Metadata(desc, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of()), new PackSelectionConfig(true,
  Pack.Position.TOP, false))` directly so no pack.mcmeta / pack_format is needed. Pick the type from the repository's
  built-in source: ServerPacksSource = data, any other BuiltInPackSource (ClientPackSource) = resources.
- Creative tab: mixin `CreativeModeTab.buildContents` at RETURN and add stacks to the shadowed `displayItems` /
  `displayItemsSearchTab` sets when `BuiltInRegistries.CREATIVE_MODE_TAB.getKey(this)` is the tab you want.
- Per-tick item logic: override `Item.inventoryTick(ItemStack, ServerLevel, Entity, EquipmentSlot)`; vanilla calls it
  for every stack in a player's inventory (main 0-35, armour 36-39, offhand 40, body 41, saddle 42). No events needed.
- Config: `FabricLoader.getInstance().getConfigDir()` + java.util.Properties.
- fabric.mod.json: `"depends": {"fabricloader": ">=0.18.0", "minecraft": "~26.1", "java": ">=21"}`; mixins config
  `"compatibilityLevel": "JAVA_21"`, no refmap.
- Registry sync caveat: without Fabric API nothing syncs numeric registry ids between client and server, so (as with any
  mod) both sides must run the same mod list. If Fabric API is present it syncs them.
