package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public final class RankBasedAttributeModifier
{
    public static final Codec<RankBasedAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(o -> o.id),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(o -> o.amount),
            AttributeModifier.Operation.CODEC.fieldOf("operation").forGetter(o -> o.operation))
            .apply(instance, RankBasedAttributeModifier::new));

    private final Identifier id;
    private final LevelBasedValue amount;
    private final AttributeModifier.Operation operation;

    // Limited to [1,10] - enforced by upgrade definition codec
    private final Function<Integer, AttributeModifier> memoizedModifiers;

    public RankBasedAttributeModifier(Identifier id, LevelBasedValue amount, AttributeModifier.Operation operation)
    {
        this.id = id;
        this.amount = amount;
        this.operation = operation;
        this.memoizedModifiers = Util.memoize(rank -> new AttributeModifier(id, amount.calculate(rank), operation));
    }

    public AttributeModifier get(int upgradeRank, @Nullable EquipmentSlot slot)
    {
        AttributeModifier modifier = memoizedModifiers.apply(upgradeRank);
        if (slot == null) return modifier;

        Identifier prefixedID = id.withPrefix(slot.getSerializedName() + "/");
        return new AttributeModifier(prefixedID, amount.calculate(upgradeRank), operation);
    }
}