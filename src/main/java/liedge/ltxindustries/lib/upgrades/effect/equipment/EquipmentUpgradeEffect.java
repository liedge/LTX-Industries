package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.effect.EffectTooltipProvider;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.function.Function;

public interface EquipmentUpgradeEffect extends EffectTooltipProvider.SingleLine
{
    Codec<EquipmentUpgradeEffect> CODEC = Codec.lazyInitialized(() -> LTXIRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPES.byNameCodec().dispatch(EquipmentUpgradeEffect::codec, Function.identity()));

    void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context);

    MapCodec<? extends EquipmentUpgradeEffect> codec();
}