package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Optional;
import java.util.function.Predicate;

public record MobEffectImmunity(HolderSet<MobEffect> effects, Optional<MobEffectsPredicate.MobEffectInstancePredicate> predicate, boolean useEnergy)
    implements Predicate<MobEffectInstance>
{
    public static final Codec<MobEffectImmunity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("effects").forGetter(MobEffectImmunity::effects),
            MobEffectsPredicate.MobEffectInstancePredicate.CODEC.optionalFieldOf("predicate").forGetter(MobEffectImmunity::predicate),
            Codec.BOOL.optionalFieldOf("use_energy", true).forGetter(MobEffectImmunity::useEnergy))
            .apply(instance, MobEffectImmunity::new));

    public static MobEffectImmunity immuneTo(HolderSet<MobEffect> effects, boolean useEnergy)
    {
        return new MobEffectImmunity(effects, Optional.empty(), useEnergy);
    }

    public static MobEffectImmunity immuneTo(Holder<MobEffect> effect, boolean useEnergy)
    {
        return immuneTo(HolderSet.direct(effect), useEnergy);
    }

    public static MobEffectImmunity immuneTo(HolderGetter<MobEffect> holders, TagKey<MobEffect> tagKey, boolean useEnergy)
    {
        return immuneTo(holders.getOrThrow(tagKey), useEnergy);
    }

    @Override
    public boolean test(MobEffectInstance effectInstance)
    {
        return effects.contains(effectInstance.getEffect()) && (predicate.isEmpty() || predicate.get().matches(effectInstance));
    }
}