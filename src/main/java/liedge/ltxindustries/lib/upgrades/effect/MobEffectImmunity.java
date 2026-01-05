package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
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

public record MobEffectImmunity(HolderSet<MobEffect> effects, Optional<MobEffectsPredicate.MobEffectInstancePredicate> predicate, ContextlessValue energyActions)
    implements Predicate<MobEffectInstance>
{
    public static final Codec<MobEffectImmunity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.MOB_EFFECT).fieldOf("effects").forGetter(MobEffectImmunity::effects),
            MobEffectsPredicate.MobEffectInstancePredicate.CODEC.optionalFieldOf("predicate").forGetter(MobEffectImmunity::predicate),
            ContextlessValue.CODEC.fieldOf("energy_actions").forGetter(MobEffectImmunity::energyActions))
            .apply(instance, MobEffectImmunity::new));

    public static MobEffectImmunity immuneTo(HolderSet<MobEffect> effects, ContextlessValue energyActions)
    {
        return new MobEffectImmunity(effects, Optional.empty(), energyActions);
    }

    public static MobEffectImmunity immuneTo(Holder<MobEffect> effect, ContextlessValue energyActions)
    {
        return immuneTo(HolderSet.direct(effect), energyActions);
    }

    public static MobEffectImmunity immuneTo(HolderGetter<MobEffect> holders, TagKey<MobEffect> tagKey, ContextlessValue energyActions)
    {
        return immuneTo(holders.getOrThrow(tagKey), energyActions);
    }

    @Override
    public boolean test(MobEffectInstance effectInstance)
    {
        return effects.contains(effectInstance.getEffect()) && (predicate.isEmpty() || predicate.get().matches(effectInstance));
    }

    public boolean blockEffect(MobEffectInstance effectInstance, int upgradeRank, UpgradedEquipmentInUse equipmentInUse)
    {
        return test(effectInstance) && equipmentInUse.useEnergyActions(energyActions.calculateInt(upgradeRank));
    }
}