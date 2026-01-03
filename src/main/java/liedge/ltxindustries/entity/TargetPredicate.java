package liedge.ltxindustries.entity;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface TargetPredicate
{
    Codec<LootItemCondition> CONDITIONS_CODEC = LimaLootUtil.conditionsCodec(LootContextParamSets.CHEST, "target predicate");
    TargetPredicate NO_CHECK = (a, b) -> TriState.DEFAULT;

    static TriState testSingle(Level level, Entity targetEntity, @Nullable Entity attackingEntity, UpgradesContainerBase<?, ?> upgrades)
    {
        if (!(level instanceof ServerLevel serverLevel)) throw new IllegalArgumentException("Created target predicate on client");

        List<LootItemCondition> conditions = upgrades.listEffectStream(LTXIUpgradeEffectComponents.TARGET_CONDITIONS).toList();
        if (conditions.isEmpty()) return TriState.DEFAULT;

        LootContext context = LimaLootUtil.chestLootContext(serverLevel, targetEntity, attackingEntity);
        for (LootItemCondition condition : conditions)
        {
            if (!condition.test(context)) return TriState.FALSE;
        }

        return TriState.TRUE;
    }

    static TargetPredicate create(Level level, UpgradesContainerBase<?, ?> upgrades)
    {
        if (!(level instanceof ServerLevel serverLevel)) throw new IllegalArgumentException("Created target predicate on client");

        List<LootItemCondition> conditions = upgrades.listEffectStream(LTXIUpgradeEffectComponents.TARGET_CONDITIONS).toList();
        if (conditions.isEmpty()) return NO_CHECK;

        return (targetEntity, attackingEntity) ->
        {
            LootContext context = LimaLootUtil.chestLootContext(serverLevel, targetEntity, attackingEntity);

            for (LootItemCondition condition : conditions)
            {
                if (!condition.test(context)) return TriState.FALSE;
            }

            return TriState.TRUE;
        };
    }

    TriState test(Entity targetEntity, @Nullable Entity attackingEntity);
}