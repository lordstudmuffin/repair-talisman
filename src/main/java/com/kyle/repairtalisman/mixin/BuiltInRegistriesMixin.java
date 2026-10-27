package com.kyle.repairtalisman.mixin;

import com.kyle.repairtalisman.RepairTalismanMod;
import net.minecraft.core.registries.BuiltInRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Vanilla freezes every built-in registry inside {@code BuiltInRegistries.bootStrap()}, which runs
 * before Fabric Loader calls any mod's {@code onInitialize}. Fabric API delays that freeze; without
 * it a plain {@code Registry.register} from the entrypoint throws "Registry is already frozen".
 * So the talisman is registered here, just before the freeze. {@link RepairTalismanMod#registerItems}
 * is idempotent, so when Fabric API is present and the entrypoint ran first this is a no-op.
 */
@Mixin(BuiltInRegistries.class)
public abstract class BuiltInRegistriesMixin {
    @Inject(method = "bootStrap", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/core/registries/BuiltInRegistries;freeze()V"))
    private static void repairtalisman$registerBeforeFreeze(CallbackInfo ci) {
        RepairTalismanMod.registerItems();
    }
}
