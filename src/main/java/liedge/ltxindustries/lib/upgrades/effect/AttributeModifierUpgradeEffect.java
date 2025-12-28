package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.datafixers.Products;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.lib.upgrades.value.RankBasedAttributeModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.function.Consumer;

public interface AttributeModifierUpgradeEffect extends UpgradeTooltipsProvider
{
    static <T extends AttributeModifierUpgradeEffect> Products.P2<RecordCodecBuilder.Mu<T>, Holder<Attribute>, RankBasedAttributeModifier> commonFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                Attribute.CODEC.fieldOf("attribute").forGetter(T::attribute),
                RankBasedAttributeModifier.CODEC.fieldOf("modifier").forGetter(T::modifier));
    }

    Holder<Attribute> attribute();

    RankBasedAttributeModifier modifier();

    EquipmentSlotGroup slots();

    MutableComponent tooltipPrefix();

    default AttributeModifier createModifier(int rank)
    {
        return modifier().get(rank);
    }

    default ItemAttributeModifiers.Entry createModifierEntry(int rank)
    {
        return new ItemAttributeModifiers.Entry(attribute(), createModifier(rank), slots());
    }

    @Override
    default void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component attributeText = attribute().value().toComponent(createModifier(upgradeRank), TooltipFlag.NORMAL).withStyle(ChatFormatting.GRAY);
        lines.accept(tooltipPrefix().append(attributeText));
    }
}