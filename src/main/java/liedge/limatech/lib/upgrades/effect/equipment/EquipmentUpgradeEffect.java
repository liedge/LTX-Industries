package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.function.Function;

public interface EquipmentUpgradeEffect extends EffectTooltipProvider
{
    Codec<EquipmentUpgradeEffect> CODEC = Codec.lazyInitialized(() -> LimaTechRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPES.byNameCodec().dispatch(EquipmentUpgradeEffect::codec, Function.identity()));

    void applyEquipmentEffect(Player player, int upgradeRank, ItemStack stack, LootContext context);

    MapCodec<? extends EquipmentUpgradeEffect> codec();

    interface DamageModification extends EquipmentUpgradeEffect
    {
        @Override
        default void applyEquipmentEffect(Player player, int upgradeRank, ItemStack stack, LootContext context)
        {
            LimaDynamicDamageSource damageSource = LimaCoreUtil.castOrNull(LimaDynamicDamageSource.class, context.getParamOrNull(LootContextParams.DAMAGE_SOURCE));
            LivingEntity target = LimaCoreUtil.castOrNull(LivingEntity.class, context.getParamOrNull(LootContextParams.THIS_ENTITY));

            if (damageSource != null && target != null)
            {
                modifyDynamicAttack(player, upgradeRank, target, damageSource);
            }
        }

        void modifyDynamicAttack(Player player, int upgradeRank, LivingEntity target, LimaDynamicDamageSource damageSource);
    }
}