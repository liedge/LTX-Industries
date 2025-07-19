package liedge.ltxindustries.util;

import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.registries.holdersets.OrHolderSet;

import java.util.List;
import java.util.Optional;

public final class LTXIUtil
{
    private LTXIUtil() {}

    public static LootContext emptyLootContext(ServerLevel level)
    {
        LootParams params = new LootParams.Builder(level).create(LootContextParamSets.EMPTY);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    public static LootContext entityLootContext(ServerLevel level, Entity target, DamageSource damageSource, LivingEntity attacker)
    {
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, target)
                .withParameter(LootContextParams.ORIGIN, target.position())
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.ATTACKING_ENTITY, attacker)
                .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity())
                .create(LootContextParamSets.ENTITY);
        return new LootContext.Builder(params).create(Optional.empty());
    }

    public static <T> HolderSet<T> mergeHolderSets(List<HolderSet<T>> sets)
    {
        return switch (sets.size())
        {
            case 0 -> HolderSet.empty();
            case 1 -> sets.getFirst();
            default -> new OrHolderSet<>(sets);
        };
    }
}