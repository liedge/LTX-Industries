package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;
import java.util.Map;

public class ItemAttributeModifiersUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<ItemAttributeModifiersUpgradeEffect> CODEC = ModifierEntry.CODEC.listOf(1, 5).xmap(ItemAttributeModifiersUpgradeEffect::new, o -> o.entries).fieldOf("item_modifiers");

    public static Builder builder()
    {
        return new Builder();
    }

    private final List<ModifierEntry> entries;

    private ItemAttributeModifiersUpgradeEffect(List<ModifierEntry> entries)
    {
        this.entries = entries;
    }

    @Override
    public void postUpgradeInstall(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        rebuildModifiers(upgrades, equipmentItem);
    }

    @Override
    public void postUpgradeRemoved(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem, int upgradeRank)
    {
        rebuildModifiers(upgrades, equipmentItem);
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.ITEM_ATTRIBUTE_MODIFIERS.get();
    }

    private void rebuildModifiers(ItemEquipmentUpgrades upgrades, ItemStack equipmentItem)
    {
        Map<ResourceLocation, ItemAttributeModifiers.Entry> map = new Object2ObjectOpenHashMap<>();

        upgrades.forEachEffect(ItemAttributeModifiersUpgradeEffect.class, (effect, rank) ->
        {
            for (ModifierEntry entry : effect.entries)
            {
                if (!map.containsKey(entry.id))
                {
                    map.put(entry.id, entry.build(rank));
                }
            }
        });

        ItemAttributeModifiers modifiers = map.isEmpty() ? ItemAttributeModifiers.EMPTY : new ItemAttributeModifiers(List.copyOf(map.values()), false);
        equipmentItem.set(DataComponents.ATTRIBUTE_MODIFIERS, modifiers);
    }

    public static class Builder
    {
        private final List<ModifierEntry> entries = new ObjectArrayList<>();

        private Builder() {}

        public Builder add(Holder<Attribute> attribute, ResourceLocation id, LevelBasedValue amount, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup)
        {
            entries.add(new ModifierEntry(attribute, id, amount, operation, slotGroup));
            return this;
        }

        public ItemAttributeModifiersUpgradeEffect build()
        {
            return new ItemAttributeModifiersUpgradeEffect(entries);
        }
    }

    private record ModifierEntry(Holder<Attribute> attribute, ResourceLocation id, LevelBasedValue amount, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup)
    {
        public static final Codec<ModifierEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Attribute.CODEC.fieldOf("type").forGetter(ModifierEntry::attribute),
                ResourceLocation.CODEC.fieldOf("id").forGetter(ModifierEntry::id),
                LevelBasedValue.CODEC.fieldOf("amount").forGetter(ModifierEntry::amount),
                AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(ModifierEntry::operation),
                EquipmentSlotGroup.CODEC.optionalFieldOf("slot", EquipmentSlotGroup.ANY).forGetter(ModifierEntry::slotGroup))
                .apply(instance, ModifierEntry::new));

        private ItemAttributeModifiers.Entry build(int upgradeRank)
        {
            return new ItemAttributeModifiers.Entry(attribute, new AttributeModifier(id, amount.calculate(upgradeRank), operation), slotGroup);
        }
    }
}