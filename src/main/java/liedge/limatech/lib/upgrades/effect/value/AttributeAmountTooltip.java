package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

public record AttributeAmountTooltip(LootContext.EntityTarget target, Holder<Attribute> attribute, LevelBasedValue amount, ValueSentiment sentiment) implements ValueEffectTooltip
{
    public static final MapCodec<AttributeAmountTooltip> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(AttributeAmountTooltip::target),
            Attribute.CODEC.fieldOf("attribute").forGetter(AttributeAmountTooltip::attribute),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(AttributeAmountTooltip::amount),
            ValueSentiment.CODEC.optionalFieldOf("sentiment", ValueSentiment.POSITIVE).forGetter(AttributeAmountTooltip::sentiment))
            .apply(instance, AttributeAmountTooltip::new));

    public AttributeAmountTooltip(LootContext.EntityTarget target, Holder<Attribute> attribute, LevelBasedValue amount)
    {
        this(target, attribute, amount, ValueSentiment.POSITIVE);
    }

    @Override
    public Component get(int upgradeRank, CompoundValueOperation operation)
    {
        Attribute attribute = this.attribute.value();
        float amt = amount.calculate(upgradeRank);

        return LimaTechLang.ATTRIBUTE_AMOUNT_VALUE_TOOLTIP.translateArgs(LimaTechTooltipUtil.percentageWithSign(amt).withStyle(sentiment.get(amt)), LimaTechLang.makeEntityTargetComponent(target), Component.translatable(attribute.getDescriptionId()));
    }

    @Override
    public Type getType()
    {
        return Type.ATTRIBUTE_AMOUNT;
    }
}