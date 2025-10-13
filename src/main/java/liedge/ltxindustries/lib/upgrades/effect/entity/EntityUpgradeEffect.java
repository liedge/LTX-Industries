package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeTooltipsProvider;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public interface EntityUpgradeEffect extends UpgradeTooltipsProvider
{
    Codec<EntityUpgradeEffect> CODEC = Codec.lazyInitialized(() -> LTXIRegistries.ENTITY_UPGRADE_EFFECT_TYPES.byNameCodec().dispatch(EntityUpgradeEffect::getType, EntityUpgradeEffectType::codec));

    void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context);

    EntityUpgradeEffectType<?> getType();
}