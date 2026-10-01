package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;

import java.util.function.Function;

public interface EntityUpgradeEffect extends LootContextUser
{
    Codec<EntityUpgradeEffect> DIRECT_CODEC = Codec.lazyInitialized(() -> LTXIRegistries.ENTITY_UPGRADE_EFFECTS.byNameCodec().dispatch(EntityUpgradeEffect::codec, Function.identity()));

    void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse);

    MapCodec<? extends EntityUpgradeEffect> codec();
}