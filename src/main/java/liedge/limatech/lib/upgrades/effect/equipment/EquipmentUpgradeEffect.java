package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Function;

public interface EquipmentUpgradeEffect extends EffectTooltipProvider
{
    Codec<EquipmentUpgradeEffect> CODEC = Codec.lazyInitialized(() -> LimaTechRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPES.byNameCodec().dispatch(EquipmentUpgradeEffect::codec, Function.identity()));

    void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context);

    MapCodec<? extends EquipmentUpgradeEffect> codec();

    interface DamageModification extends EquipmentUpgradeEffect
    {
        @Override
        default void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
        {
            LimaDynamicDamageSource damageSource = LimaCoreUtil.castOrNull(LimaDynamicDamageSource.class, context.getParamOrNull(LootContextParams.DAMAGE_SOURCE));
            if (damageSource != null) modifyDynamicDamageSource(level, entity, upgradeRank, damageSource);
        }

        void modifyDynamicDamageSource(ServerLevel level, Entity entity, int upgradeRank, LimaDynamicDamageSource damageSource);
    }
}