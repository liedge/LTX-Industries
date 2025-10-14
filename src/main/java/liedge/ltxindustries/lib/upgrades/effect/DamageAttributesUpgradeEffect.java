package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.value.RankBasedAttributeModifier;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record DamageAttributesUpgradeEffect(Holder<Attribute> attribute, RankBasedAttributeModifier modifier) implements AttributeModifierUpgradeEffect
{
    public static final Codec<DamageAttributesUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> AttributeModifierUpgradeEffect.commonFields(instance).apply(instance, DamageAttributesUpgradeEffect::new));

    public static DamageAttributesUpgradeEffect create(Holder<Attribute> attribute, ResourceLocation modifierId, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return new DamageAttributesUpgradeEffect(attribute, new RankBasedAttributeModifier(modifierId, amount, operation));
    }

    @Override
    public EquipmentSlotGroup slots()
    {
        return EquipmentSlotGroup.ANY;
    }

    @Override
    public MutableComponent tooltipPrefix()
    {
        return LTXILangKeys.DAMAGE_ATTRIBUTES_EFFECT_PREFIX.translate();
    }
}