package com.kyle.repairtalisman;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Repair Talisman: one craftable item that slowly repairs everything in the player's
 * inventory, hotbar, armor and offhand. Standalone: needs only Fabric Loader (no Fabric API).
 */
public final class RepairTalismanMod implements ModInitializer {
    public static final String MOD_ID = "repairtalisman";
    public static final Logger LOGGER = LoggerFactory.getLogger("Repair Talisman");

    public static final ResourceKey<Item> TALISMAN_KEY =
            ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "repair_talisman"));
    public static Item TALISMAN;

    /**
     * Registers the talisman once. Called from {@code BuiltInRegistriesMixin} right before vanilla
     * freezes the registries (the only window that exists without Fabric API), and again from
     * {@link #onInitialize} for the Fabric API case, where the entrypoint runs before the freeze.
     */
    public static synchronized void registerItems() {
        if (TALISMAN != null) {
            return;
        }
        TALISMAN = Registry.register(BuiltInRegistries.ITEM, TALISMAN_KEY,
                new RepairTalismanItem(new Item.Properties().setId(TALISMAN_KEY).stacksTo(1).rarity(Rarity.RARE).fireResistant()));
    }

    @Override
    public void onInitialize() {
        RepairTalismanConfig.load(FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".properties"));
        registerItems();
        LOGGER.info("Repair Talisman loaded: repairs {} durability every {} ticks",
                RepairTalismanConfig.repairAmount, RepairTalismanConfig.intervalTicks);
    }
}
