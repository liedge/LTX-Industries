package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;

public record DamageReduction(ContextlessValue amount, ContextlessValue energyActions)
{
    public static final Codec<DamageReduction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ContextlessValue.CODEC.fieldOf("amount").forGetter(DamageReduction::amount),
            ContextlessValue.CODEC.fieldOf("energy_actions").forGetter(DamageReduction::energyActions))
            .apply(instance, DamageReduction::new));

    public static DamageReduction blockDamage(ContextlessValue amount, ContextlessValue energyActions)
    {
        return new DamageReduction(amount, energyActions);
    }
}