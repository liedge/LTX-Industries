package liedge.limatech.advancement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.advancement.LimaAdvancementUtil;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import liedge.limatech.registry.LimaTechTriggerTypes;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class KilledWithWeaponTrigger extends SimpleCriterionTrigger<KilledWithWeaponTrigger.TriggerInstance>
{
    private static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player),
            EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity),
            LimaCoreCodecs.classCastRegistryCodec(BuiltInRegistries.ITEM, WeaponItem.class).fieldOf("weapon").forGetter(TriggerInstance::weaponItem))
            .apply(instance, TriggerInstance::new));

    public static Criterion<TriggerInstance> killedWithWeapon(Supplier<? extends WeaponItem> weaponItem, @Nullable ContextAwarePredicate playerPredicate, @Nullable ContextAwarePredicate entityPredicate)
    {
        return LimaTechTriggerTypes.KILLED_WITH_WEAPON.get().createCriterion(new TriggerInstance(Optional.ofNullable(playerPredicate), Optional.ofNullable(entityPredicate), weaponItem.get()));
    }

    public static Criterion<TriggerInstance> killedWithWeaponByAnyPlayer(Supplier<? extends WeaponItem> weaponItem, EntityPredicate.Builder builder)
    {
        return LimaTechTriggerTypes.KILLED_WITH_WEAPON.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.of(EntityPredicate.wrap(builder)), weaponItem.get()));
    }

    public static Criterion<TriggerInstance> killedAnyWithWeapon(Supplier<? extends WeaponItem> weaponItem)
    {
        return LimaTechTriggerTypes.KILLED_WITH_WEAPON.get().createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), weaponItem.get()));
    }

    public KilledWithWeaponTrigger() {}

    public void trigger(ServerPlayer player, LivingEntity killedEntity, WeaponDamageSource damageSource)
    {
        trigger(player, instance -> LimaAdvancementUtil.testEntityPredicate(instance.entity, player, killedEntity) && damageSource.getKillerWeapon() == instance.weaponItem);
    }

    @Override
    public Codec<TriggerInstance> codec()
    {
        return CODEC;
    }

    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> entity, WeaponItem weaponItem) implements SimpleInstance
    { }
}