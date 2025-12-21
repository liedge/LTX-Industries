package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.value.RankBasedAttributeModifier;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record AddDamageAttributes(Holder<Attribute> attribute, RankBasedAttributeModifier modifier) implements AttributeModifierUpgradeEffect
{
    public static final Codec<AddDamageAttributes> CODEC = RecordCodecBuilder.create(instance -> AttributeModifierUpgradeEffect.commonFields(instance).apply(instance, AddDamageAttributes::new));

    public static AddDamageAttributes create(Holder<Attribute> attribute, ResourceLocation modifierId, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return new AddDamageAttributes(attribute, new RankBasedAttributeModifier(modifierId, amount, operation));
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