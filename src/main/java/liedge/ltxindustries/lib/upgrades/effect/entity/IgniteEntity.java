package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

public record IgniteEntity(LevelBasedValue duration) implements EntityUpgradeEffect
{
    public static final MapCodec<IgniteEntity> CODEC = LevelBasedValue.CODEC.fieldOf("duration").xmap(IgniteEntity::new, IgniteEntity::duration);

    public static IgniteEntity igniteForSeconds(LevelBasedValue duration)
    {
        return new IgniteEntity(duration);
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (equipmentInUse.canAttack(affectedEntity)) affectedEntity.igniteForSeconds(duration.calculate(upgradeRank));
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.IGNITE_ENTITY.get();
    }
}