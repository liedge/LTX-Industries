package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record CompoundEntityEffect(List<EntityUpgradeEffect> effects) implements EntityUpgradeEffect
{
    public static final MapCodec<CompoundEntityEffect> CODEC = EntityUpgradeEffect.DIRECT_CODEC.listOf().xmap(CompoundEntityEffect::new, CompoundEntityEffect::effects).fieldOf("effects");

    public static CompoundEntityEffect allOf(EntityUpgradeEffect... effects)
    {
        return new CompoundEntityEffect(List.of(effects));
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        for (EntityUpgradeEffect sub : effects)
        {
            sub.apply(level, context, upgradeRank, affectedEntity, equipmentInUse);
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.ALL_OF_EFFECT.get();
    }

    @Override
    public Set<ContextKey<?>> getReferencedContextParams()
    {
        return effects.stream().flatMap(e -> e.getReferencedContextParams().stream()).collect(Collectors.toSet());
    }
}