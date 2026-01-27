package liedge.ltxindustries.entity;

import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface TargetPredicate
{
    TargetPredicate ALWAYS_TRUE = ($1, $2) -> true;

    static boolean testSingle(Level level, Entity targetEntity, @Nullable Entity attackingEntity, UpgradesContainerBase<?, ?> upgrades)
    {
        if (!(level instanceof ServerLevel serverLevel)) throw new IllegalArgumentException("Created target predicate on client");

        List<LootItemCondition> conditions = upgrades.listEffectStream(LTXIUpgradeEffectComponents.TARGET_CONDITIONS).toList();
        if (conditions.isEmpty()) return true;

        LootContext context = LimaLootUtil.chestLootContext(serverLevel, targetEntity, attackingEntity);
        for (LootItemCondition condition : conditions)
        {
            if (!condition.test(context)) return false;
        }

        return true;
    }

    static TargetPredicate create(Level level, UpgradesContainerBase<?, ?> upgrades, TargetPredicate fallback)
    {
        if (!(level instanceof ServerLevel serverLevel)) throw new IllegalArgumentException("Created target predicate on client");

        List<LootItemCondition> conditions = upgrades.listEffectStream(LTXIUpgradeEffectComponents.TARGET_CONDITIONS).toList();
        if (conditions.isEmpty()) return fallback;

        return (targetEntity, attackingEntity) ->
        {
            LootContext context = LimaLootUtil.chestLootContext(serverLevel, targetEntity, attackingEntity);

            for (LootItemCondition condition : conditions)
            {
                if (!condition.test(context)) return false;
            }

            return true;
        };
    }

    static TargetPredicate create(Level level, UpgradesContainerBase<?, ?> upgrades)
    {
        return create(level, upgrades, ALWAYS_TRUE);
    }

    boolean test(Entity targetEntity, @Nullable Entity attackingEntity);
}