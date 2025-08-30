package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.effect.RankBasedAttributeModifier;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeTooltipsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.function.Consumer;

public record AttributeModifierUpgradeEffect(Holder<Attribute> attribute, RankBasedAttributeModifier modifier, EquipmentSlotGroup slotGroup) implements UpgradeTooltipsProvider
{
    public static final Codec<AttributeModifierUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    Attribute.CODEC.fieldOf("attribute").forGetter(AttributeModifierUpgradeEffect::attribute),
                    RankBasedAttributeModifier.CODEC.fieldOf("modifier").forGetter(AttributeModifierUpgradeEffect::modifier),
                    EquipmentSlotGroup.CODEC.optionalFieldOf("slot_group", EquipmentSlotGroup.MAINHAND).forGetter(AttributeModifierUpgradeEffect::slotGroup))
            .apply(instance, AttributeModifierUpgradeEffect::new));

    public static AttributeModifierUpgradeEffect constantMainHand(Holder<Attribute> attribute, ResourceLocation id, float value, AttributeModifier.Operation operation)
    {
        return new AttributeModifierUpgradeEffect(attribute, new RankBasedAttributeModifier(id, LevelBasedValue.constant(value), operation), EquipmentSlotGroup.MAINHAND);
    }

    public static AttributeModifierUpgradeEffect rankBasedMainHand(Holder<Attribute> attribute, ResourceLocation id, LevelBasedValue value, AttributeModifier.Operation operation)
    {
        return new AttributeModifierUpgradeEffect(attribute, new RankBasedAttributeModifier(id, value, operation), EquipmentSlotGroup.MAINHAND);
    }

    public ItemAttributeModifiers.Entry makeModifierEntry(int upgradeRank)
    {
        return new ItemAttributeModifiers.Entry(attribute, modifier.get(upgradeRank), slotGroup);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(attribute.value().toComponent(modifier.get(upgradeRank), TooltipFlag.NORMAL).withStyle(ChatFormatting.DARK_GREEN));
    }
}