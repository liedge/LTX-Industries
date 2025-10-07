package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.effect.RankBasedAttributeModifier;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeTooltipsProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.function.Consumer;

public record DamageAttributesUpgradeEffect(Holder<Attribute> attribute, RankBasedAttributeModifier modifier) implements UpgradeTooltipsProvider
{
    public static final Codec<DamageAttributesUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(DamageAttributesUpgradeEffect::attribute),
            RankBasedAttributeModifier.CODEC.fieldOf("modifier").forGetter(DamageAttributesUpgradeEffect::modifier))
            .apply(instance, DamageAttributesUpgradeEffect::new));

    public static DamageAttributesUpgradeEffect of(Holder<Attribute> attribute, ResourceLocation id, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return new DamageAttributesUpgradeEffect(attribute, new RankBasedAttributeModifier(id, amount, operation));
    }

    public static DamageAttributesUpgradeEffect of (Holder<Attribute> attribute, ResourceKey<? extends UpgradeBase<?, ?>> upgradeKey, String suffix, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        return of(attribute, upgradeKey.location().withSuffix("." + suffix), amount, operation);
    }

    public AttributeModifier createModifier(int rank)
    {
        return modifier.get(rank);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component tooltip = attribute.value().toComponent(createModifier(upgradeRank), TooltipFlag.NORMAL).withStyle(ChatFormatting.GRAY);
        lines.accept(tooltip);
    }
}