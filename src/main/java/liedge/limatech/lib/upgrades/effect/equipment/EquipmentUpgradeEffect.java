package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.lib.upgrades.effect.UpgradeEffect;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface EquipmentUpgradeEffect extends UpgradeEffect
{
    Codec<EquipmentUpgradeEffect> CODEC = Codec.lazyInitialized(() -> LimaTechRegistries.PLAYER_UPGRADE_EFFECT_TYPE.byNameCodec().dispatch(EquipmentUpgradeEffect::codec, Function.identity()));

    void activateEquipmentEffect(ServerLevel level, int upgradeRank, Player player, ItemStack stack, @Nullable Entity targetedEntity, @Nullable DamageSource damageSource);

    default void deactivateEquipmentEffect(ServerLevel level, int upgradeRank, Player player, ItemStack stack, EquipmentUpgrades newUpgrades) {}

    MapCodec<? extends EquipmentUpgradeEffect> codec();

    interface DamageModification extends EquipmentUpgradeEffect
    {
        @Override
        default void activateEquipmentEffect(ServerLevel level, int upgradeRank, Player player, ItemStack stack, @Nullable Entity targetedEntity, @Nullable DamageSource damageSource)
        {
            if (damageSource instanceof LimaDynamicDamageSource dynamicSource && targetedEntity instanceof LivingEntity livingTarget)
            {
                modifyDynamicAttack(level, upgradeRank, player, livingTarget, dynamicSource);
            }
        }

        void modifyDynamicAttack(ServerLevel level, int upgradeRank, Player player, LivingEntity livingTarget, LimaDynamicDamageSource damageSource);
    }
}