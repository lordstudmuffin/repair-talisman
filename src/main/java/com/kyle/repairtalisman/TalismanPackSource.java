package com.kyle.repairtalisman;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

/**
 * Exposes this mod jar's assets/ and data/ folders to the game as a built-in, always-on pack.
 * Fabric API's resource loader normally does this for every mod; without Fabric API we do it
 * ourselves (see {@link com.kyle.repairtalisman.mixin.PackRepositoryMixin}).
 */
public final class TalismanPackSource implements RepositorySource {
    private static final String PACK_ID = RepairTalismanMod.MOD_ID;
    private final PackType type;

    public TalismanPackSource(PackType type) {
        this.type = type;
    }

    @Override
    public void loadPacks(Consumer<Pack> consumer) {
        FabricLoader loader = FabricLoader.getInstance();
        if (loader.isModLoaded("fabric-resource-loader-v1") || loader.isModLoaded("fabric-resource-loader-v0")) {
            return; // Fabric API already exposes every mod jar's resources; don't add a second copy
        }
        Optional<ModContainer> container = loader.getModContainer(RepairTalismanMod.MOD_ID);
        if (container.isEmpty()) {
            RepairTalismanMod.LOGGER.error("Own mod container not found; recipe and textures will be missing");
            return;
        }
        Path root = null;
        for (Path p : container.get().getRootPaths()) {
            if (Files.isDirectory(p.resolve(type.getDirectory()))) {
                root = p;
                break;
            }
        }
        if (root == null) {
            return; // nothing of this type in the jar
        }
        final Path packRoot = root;
        PackLocationInfo location = new PackLocationInfo(PACK_ID, Component.literal("Repair Talisman"),
                PackSource.BUILT_IN, Optional.empty());
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo info) {
                return new PathPackResources(info, packRoot);
            }

            @Override
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return new PathPackResources(info, packRoot);
            }
        };
        Pack.Metadata metadata = new Pack.Metadata(Component.literal("Repair Talisman resources"),
                PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), List.of());
        PackSelectionConfig selection = new PackSelectionConfig(true, Pack.Position.TOP, false);
        consumer.accept(new Pack(location, supplier, metadata, selection));
    }
}
