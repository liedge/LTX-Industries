package liedge.ltxindustries.lib.upgrades;

import liedge.limacore.util.LimaLootUtil;
import net.minecraft.server.level.ServerLevel;
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

    // Factories
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
}