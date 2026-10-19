package com.kyle.repairtalisman.mixin;

import com.kyle.repairtalisman.RepairTalismanMod;
import java.util.Collection;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Puts the talisman in the Tools & Utilities creative tab and the search tab. */
@Mixin(CreativeModeTab.class)
public abstract class CreativeModeTabMixin {
    @Shadow private Collection<ItemStack> displayItems;
    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @Inject(method = "buildContents", at = @At("RETURN"))
    private void repairtalisman$addTalisman(CreativeModeTab.ItemDisplayParameters parameters, CallbackInfo ci) {
        if (RepairTalismanMod.TALISMAN == null) {
            return;
        }
        CreativeModeTab self = (CreativeModeTab) (Object) this;
        Identifier id = BuiltInRegistries.CREATIVE_MODE_TAB.getKey(self);
        if (id == null || !id.getNamespace().equals("minecraft") || !id.getPath().equals("tools_and_utilities")) {
            return;
        }
        ItemStack stack = new ItemStack(RepairTalismanMod.TALISMAN);
        this.displayItems.add(stack);
        this.displayItemsSearchTab.add(stack);
    }
}
