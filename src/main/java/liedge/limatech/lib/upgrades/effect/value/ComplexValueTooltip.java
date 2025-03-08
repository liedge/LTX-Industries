package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

public interface ComplexValueTooltip
{
    Codec<ComplexValueTooltip> CODEC = Type.CODEC.flatDispatch(SimpleTooltip.class, SimpleTooltip.FLAT_CODEC, ComplexValueTooltip::getType, Type::getCodec);

    static TargetedAttributeAmountTooltip attributeValueTooltip(LootContext.EntityTarget target, Holder<Attribute> attribute, LevelBasedValue amount)
    {
        return new TargetedAttributeAmountTooltip(target, attribute, amount);
    }

    Component getValueTooltip(int upgradeRank, CompoundValueOperation operation, boolean beneficial);

    Type getType();

    record SimpleTooltip(LevelBasedValue tooltipValue) implements ComplexValueTooltip
    {
        private static final Codec<SimpleTooltip> FLAT_CODEC = LevelBasedValue.CODEC.xmap(SimpleTooltip::new, SimpleTooltip::tooltipValue);
        private static final MapCodec<SimpleTooltip> CODEC = FLAT_CODEC.fieldOf("tooltip_value");

        @Override
        public Component getValueTooltip(int upgradeRank, CompoundValueOperation operation, boolean beneficial)
        {
            return operation.toValueComponent(tooltipValue.calculate(upgradeRank), beneficial);
        }

        @Override
        public Type getType()
        {
            return Type.SIMPLE;
        }
    }

    record TargetedAttributeAmountTooltip(LootContext.EntityTarget target, Holder<Attribute> attribute, LevelBasedValue amount) implements ComplexValueTooltip
    {
        private static final MapCodec<TargetedAttributeAmountTooltip> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                LootContext.EntityTarget.CODEC.fieldOf("target").forGetter(TargetedAttributeAmountTooltip::target),
                Attribute.CODEC.fieldOf("attribute").forGetter(TargetedAttributeAmountTooltip::attribute),
                LevelBasedValue.CODEC.fieldOf("amount").forGetter(TargetedAttributeAmountTooltip::amount))
                .apply(instance, TargetedAttributeAmountTooltip::new));

        @Override
        public Component getValueTooltip(int upgradeRank, CompoundValueOperation operation, boolean beneficial)
        {
            return LimaTechLang.ATTRIBUTE_AMOUNT_VALUE_TOOLTIP.translateArgs(LimaTechTooltipUtil.percentageWithSign(amount.calculate(upgradeRank), !beneficial), LimaTechLang.makeEntityTargetComponent(target), Component.translatable(attribute.value().getDescriptionId()).withStyle(ChatFormatting.DARK_AQUA));
        }

        @Override
        public Type getType()
        {
            return Type.TARGETED_ATTRIBUTE;
        }
    }

    enum Type implements StringRepresentable
    {
        SIMPLE("simple", SimpleTooltip.CODEC),
        TARGETED_ATTRIBUTE("targeted_attribute", TargetedAttributeAmountTooltip.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.create(Type.class);

        private final String name;
        private final MapCodec<? extends ComplexValueTooltip> codec;

        Type(String name, MapCodec<? extends ComplexValueTooltip> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public MapCodec<? extends ComplexValueTooltip> getCodec()
        {
            return codec;
        }
    }
}