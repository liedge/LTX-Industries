package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.entity.damage.EffectDamageSource;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record DamageEntity(Holder<DamageType> damageType, UpgradeValueProvider amount) implements EntityUpgradeEffect
{
    public static final MapCodec<DamageEntity> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageType.CODEC.fieldOf("damage_type").forGetter(DamageEntity::damageType),
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("amount").forGetter(DamageEntity::amount))
            .apply(instance, DamageEntity::new));

    public static DamageEntity hurtEntity(Holder<DamageType> damageType, UpgradeValueProvider amount)
    {
        return new DamageEntity(damageType, amount);
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (equipmentInUse.canAttack(affectedEntity))
        {
            DamageSource source = new EffectDamageSource(damageType, equipmentInUse.owner(), equipmentInUse.upgrades());
            float damage = (float) amount.get(context, upgradeRank);
            affectedEntity.hurt(source, damage);
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.DAMAGE_ENTITY.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return amount.getReferencedContextParams();
    }
}