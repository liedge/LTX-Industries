package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

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
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        for (EntityUpgradeEffect sub : effects)
        {
            sub.applyEntityEffect(level, entity, upgradeRank, context);
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.ALL_OF_EFFECT.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return effects.stream().flatMap(e -> e.getReferencedContextParams().stream()).collect(Collectors.toSet());
    }
}