package com.kyle.repairtalisman.mixin;

import com.kyle.repairtalisman.TalismanPackSource;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.repository.ServerPacksSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds this mod's bundled resources next to the vanilla built-in packs. The game's own
 * {@link BuiltInPackSource} subclasses are {@link ServerPacksSource} (data packs) and the
 * client's ClientPackSource (resource packs); we mirror whichever one the repository holds.
 */
@Mixin(PackRepository.class)
public abstract class PackRepositoryMixin {
    @Shadow @Final @Mutable
    private Set<RepositorySource> sources;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void repairtalisman$addModPack(RepositorySource[] providers, CallbackInfo ci) {
        PackType type = null;
        for (RepositorySource source : this.sources) {
            if (source instanceof ServerPacksSource) {
                type = PackType.SERVER_DATA;
                break;
            }
            if (source instanceof BuiltInPackSource) {
                type = PackType.CLIENT_RESOURCES;
                break;
            }
        }
        if (type == null) {
            return;
        }
        Set<RepositorySource> copy = new LinkedHashSet<>(this.sources);
        copy.add(new TalismanPackSource(type));
        this.sources = copy;
    }
}
