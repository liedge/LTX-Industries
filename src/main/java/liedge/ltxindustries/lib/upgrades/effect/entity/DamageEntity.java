package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.entity.damage.LTXIDamageSource;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

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
            Entity directEntity = context.getOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY);
            DamageSource source = new LTXIDamageSource(damageType, directEntity, equipmentInUse.owner(), equipmentInUse.upgrades(), equipmentInUse.stack(), false);
            float damage = (float) amount.get(context, upgradeRank);
            affectedEntity.hurtServer(level, source, damage);
        }
    }

    @Override
    public MapCodec<? extends EntityUpgradeEffect> codec()
    {
        return CODEC;
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.DIRECT_ATTACKING_ENTITY);
    }

    @Override
    public void validate(ValidationContext context)
    {
        EntityUpgradeEffect.super.validate(context);
        Validatable.validate(context, "amount", amount);
    }
}