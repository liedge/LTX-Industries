package liedge.ltxindustries.lib.upgrades;

import liedge.limacore.util.LimaLootUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public final class UpgradeContexts
{
    private UpgradeContexts() {}

    // Sets
    public static final LootContextParamSet UPGRADED_ENTITY = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .build();

    public static final LootContextParamSet UPGRADED_DAMAGE = LootContextParamSet.builder()
            .required(LootContextParams.THIS_ENTITY)
            .required(LootContextParams.ORIGIN)
            .required(LootContextParams.DAMAGE_SOURCE)
            .required(UpgradeContextKeys.DAMAGE)
            .optional(LootContextParams.ATTACKING_ENTITY)
            .optional(LootContextParams.DIRECT_ATTACKING_ENTITY)
            .build();

    // Factories
    private static LootParams.Builder paramsBuilder(ServerLevel level)
    {
        return new LootParams.Builder(level);
    }

    public static LootContext entityContext(ServerLevel level, Entity thisEntity, Vec3 origin)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, thisEntity)
                .withParameter(LootContextParams.ORIGIN, origin)
                .create(UPGRADED_ENTITY);

        return LimaLootUtil.contextOf(params);
    }

    public static LootContext entityContext(ServerLevel level, Entity thisEntity)
    {
        return entityContext(level, thisEntity, thisEntity.position());
    }

    public static LootContext damageContext(ServerLevel level, Entity thisEntity, DamageSource damageSource, float damage)
    {
        LootParams params = paramsBuilder(level)
                .withParameter(LootContextParams.THIS_ENTITY, thisEntity)
                .withParameter(LootContextParams.ORIGIN, thisEntity.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(UpgradeContextKeys.DAMAGE, damage)
                .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, damageSource.getEntity())
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(UPGRADED_DAMAGE);

        return LimaLootUtil.contextOf(params);
    }
}