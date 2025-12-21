package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.value.RankBasedAttributeModifier;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record AddItemAttributes(Holder<Attribute> attribute, RankBasedAttributeModifier modifier, EquipmentSlotGroup slots) implements AttributeModifierUpgradeEffect
{
    public static final Codec<AddItemAttributes> CODEC = RecordCodecBuilder.create(instance -> AttributeModifierUpgradeEffect.commonFields(instance)
            .and(EquipmentSlotGroup.CODEC.fieldOf("slots").forGetter(AddItemAttributes::slots))
            .apply(instance, AddItemAttributes::new));

    public static AddItemAttributes create(Holder<Attribute> attribute, ResourceLocation modifierId, LevelBasedValue amount, AttributeModifier.Operation operation, EquipmentSlotGroup slots)
    {
        return new AddItemAttributes(attribute, new RankBasedAttributeModifier(modifierId, amount, operation), slots);
    }

    @Override
    public MutableComponent tooltipPrefix()
    {
        return Component.translatable("item.modifiers." + slots.getSerializedName()).append(CommonComponents.SPACE);
    }
}