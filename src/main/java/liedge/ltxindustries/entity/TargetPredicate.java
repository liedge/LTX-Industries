package liedge.ltxindustries.entity;

import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jspecify.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface TargetPredicate
{
    TargetPredicate ALL = (_,_,_) -> true;

    static TargetPredicate create(Upgrades upgrades, TargetPredicate fallback)
    {
        List<LootItemCondition> conditions = upgrades.listEffectStream(LTXIUpgradeEffectComponents.TARGET_CONDITIONS).toList();
        if (conditions.isEmpty()) return fallback;

        return (sl, targetEntity, attackingEntity) ->
        {
            LootContext context = LimaLootUtil.chestLootContext(sl, targetEntity, attackingEntity);
            for (LootItemCondition condition : conditions)
            {
                if (!condition.test(context)) return false;
            }
            return true;
        };
    }

    static TargetPredicate create(Upgrades upgrades)
    {
        return create(upgrades, ALL);
    }

    boolean test(ServerLevel level, Entity targetEntity, @Nullable Entity attackingEntity);
}