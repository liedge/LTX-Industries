package liedge.ltxindustries.item;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface UpgradableEquipmentItem extends ItemLike
{
    static EquipmentUpgrades getEquipmentUpgradesFromStack(ItemStack stack)
    {
        return stack.getOrDefault(LTXIDataComponents.EQUIPMENT_UPGRADES, EquipmentUpgrades.EMPTY);
    }

    default @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return null;
    }

    default ItemStack createStackWithDefaultUpgrades(HolderLookup.Provider registries)
    {
        ItemStack stack = new ItemStack(this);
        Optional.ofNullable(getDefaultUpgradeKey()).flatMap(registries::holder).ifPresent(holder -> setUpgrades(stack, EquipmentUpgrades.builder().set(holder).toImmutable()));
        return stack;
    }

    default void onUpgradeRefresh(LootContext context, ItemStack stack, EquipmentUpgrades upgrades)
    {
        // Refresh enchantments
        upgrades.applyEnchantments(stack);

        // Refresh attribute modifiers
        List<ItemAttributeModifiers.Entry> modifierEntries = new ObjectArrayList<>();
        modifierEntries.addAll(this.asItem().getDefaultAttributeModifiers(stack).modifiers());
        upgrades.forEachEffect(LTXIUpgradeEffectComponents.ADD_ITEM_ATTRIBUTES, (effect, rank) -> modifierEntries.add(effect.createModifierEntry(rank)));
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, new ItemAttributeModifiers(modifierEntries, true));
    }

    default EquipmentUpgrades getUpgrades(ItemStack stack)
    {
        return getEquipmentUpgradesFromStack(stack);
    }

    default void setUpgrades(ItemStack stack, EquipmentUpgrades upgrades)
    {
        stack.set(LTXIDataComponents.EQUIPMENT_UPGRADES, upgrades);
    }

    default double getUpgradedDamage(UpgradesContainerBase<?, ?> upgrades, LootContext context, double baseDamage)
    {
        return upgrades.runConditionalValueOps(LTXIUpgradeEffectComponents.EQUIPMENT_DAMAGE, context, baseDamage);
    }
}