package com.kyle.repairtalisman;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;


/**
 * The talisman itself. Vanilla calls {@link #inventoryTick} every server tick for every stack in a
 * player's inventory, so no events or mixins are needed for the repair logic.
 */
public class RepairTalismanItem extends Item {
    public RepairTalismanItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, EquipmentSlot slot) {
        if (!(owner instanceof Player player) || player.isSpectator()) {
            return;
        }
        int interval = RepairTalismanConfig.intervalTicks;
        if (player.tickCount % interval != 0) {
            return;
        }
        Inventory inv = player.getInventory();
        // Several talismans in one inventory must not stack: only the first one found does the work.
        int mine = -1;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if (s.getItem() == this) {
                if (s == stack) {
                    mine = i;
                }
                if (mine != i) {
                    return; // an earlier talisman (or one in a slot we are not) handles this pulse
                }
                break;
            }
        }
        if (mine < 0) {
            return; // not in the player's own inventory (e.g. a crafting grid); do nothing
        }
        if (RepairTalismanConfig.onlyInHotbar && mine >= Inventory.SELECTION_SIZE) {
            return;
        }
        int amount = RepairTalismanConfig.repairAmount;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            if (i >= Inventory.INVENTORY_SIZE) {
                if (i == Inventory.SLOT_OFFHAND) {
                    if (!RepairTalismanConfig.repairOffhand) continue;
                } else if (!RepairTalismanConfig.repairArmor) {
                    continue;
                }
            }
            ItemStack target = inv.getItem(i);
            if (target.isDamaged()) {
                target.setDamageValue(target.getDamageValue() - amount);
            }
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        int interval = RepairTalismanConfig.intervalTicks;
        String seconds = interval % 20 == 0 ? Integer.toString(interval / 20) : String.format(java.util.Locale.ROOT, "%.1f", interval / 20.0);
        tooltip.accept(Component.translatable("item.repairtalisman.repair_talisman.tooltip",
                RepairTalismanConfig.repairAmount, seconds).withStyle(ChatFormatting.GRAY));
        if (RepairTalismanConfig.onlyInHotbar) {
            tooltip.accept(Component.translatable("item.repairtalisman.repair_talisman.tooltip.hotbar").withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
