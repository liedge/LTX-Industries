package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;

import java.util.function.Function;

public final class RankBasedAttributeModifier
{
    public static final Codec<RankBasedAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(o -> o.id),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(o -> o.amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(o -> o.operation))
            .apply(instance, RankBasedAttributeModifier::new));

    private final ResourceLocation id;
    private final LevelBasedValue amount;
    private final AttributeModifier.Operation operation;

    // Limited to [1,10] - enforced by upgrade definition codec
    private final Function<Integer, AttributeModifier> memoizedModifiers;

    public RankBasedAttributeModifier(ResourceLocation id, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        this.id = id;
        this.amount = amount;
        this.operation = operation;
        this.memoizedModifiers = Util.memoize(rank -> new AttributeModifier(id, amount.calculate(rank), operation));
    }

    public AttributeModifier get(int upgradeRank)
    {
        return memoizedModifiers.apply(upgradeRank);
    }
}