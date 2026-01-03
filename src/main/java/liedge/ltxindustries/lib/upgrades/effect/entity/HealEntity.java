package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record HealEntity(UpgradeValueProvider amount) implements EntityUpgradeEffect
{
    public static final MapCodec<HealEntity> CODEC = UpgradeValueProvider.DIRECT_CODEC.fieldOf("amount").xmap(HealEntity::new, HealEntity::amount);

    public static HealEntity healFor(UpgradeValueProvider amount)
    {
        return new HealEntity(amount);
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (affectedEntity instanceof LivingEntity livingEntity)
        {
            livingEntity.heal((float) amount.get(context, upgradeRank));
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.HEAL_ENTITY.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return amount.getReferencedContextParams();
    }
}