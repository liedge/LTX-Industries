package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.function.Consumer3;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.List;

public final class ItemAttributeModifierUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<ItemAttributeModifierUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(o -> o.attribute),
            ResourceLocation.CODEC.fieldOf("modifier_id").forGetter(o -> o.modifierId),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(o -> o.amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(o -> o.operation),
            EquipmentSlotGroup.CODEC.optionalFieldOf("slot_group", EquipmentSlotGroup.MAINHAND).forGetter(o -> o.slotGroup))
            .apply(instance, ItemAttributeModifierUpgradeEffect::new));

    public static ItemAttributeModifierUpgradeEffect create(Holder<Attribute> attribute, ResourceLocation modifierId, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return new ItemAttributeModifierUpgradeEffect(attribute, modifierId, amount, operation, EquipmentSlotGroup.MAINHAND);
    }

    public static ItemAttributeModifierUpgradeEffect create(Holder<Attribute> attribute, ResourceLocation modifierId, float constantAmount, AttributeModifier.Operation operation)
    {
        return create(attribute, modifierId, LevelBasedValue.constant(constantAmount), operation);
    }

    private final Holder<Attribute> attribute;
    private final ResourceLocation modifierId;
    private final LevelBasedValue amount;
    private final AttributeModifier.Operation operation;
    private final EquipmentSlotGroup slotGroup;

    public ItemAttributeModifierUpgradeEffect(Holder<Attribute> attribute, ResourceLocation modifierId, LevelBasedValue amount, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup)
    {
        this.attribute = attribute;
        this.modifierId = modifierId;
        this.amount = amount;
        this.operation = operation;
        this.slotGroup = slotGroup;
    }

    private AttributeModifier makeModifier(int upgradeRank)
    {
        return new AttributeModifier(modifierId, amount.calculate(upgradeRank), operation);
    }

    public void addModifierToItem(int upgradeRank, Consumer3<Holder<Attribute>, AttributeModifier, EquipmentSlotGroup> consumer)
    {
        consumer.accept(attribute, makeModifier(upgradeRank), slotGroup);
    }

    @Override
    public EquipmentUpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.ITEM_ATTRIBUTE_MODIFIER.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        lines.add(attribute.value().toComponent(makeModifier(upgradeRank), TooltipFlag.NORMAL));
    }
}