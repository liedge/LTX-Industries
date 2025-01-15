package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.LimaTechEntityUpgradeEffects;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record KnockbackStrengthUpgradeEffect(LevelBasedValue amount) implements EquipmentUpgradeEffect.DamageModification
{
    public static final MapCodec<KnockbackStrengthUpgradeEffect> CODEC = LevelBasedValue.CODEC.fieldOf("amount").xmap(KnockbackStrengthUpgradeEffect::new, KnockbackStrengthUpgradeEffect::amount);

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEntityUpgradeEffects.KNOCKBACK_STRENGTH_ENTITY_EFFECT.get();
    }

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        Component amtComponent = LimaTechTooltipUtil.percentageWithSign(amount.calculate(upgradeRank) - 1f, false);
        return LimaTechLang.WEAPON_KNOCKBACK_EFFECT.translateArgs(amtComponent);
    }

    @Override
    public void modifyDynamicAttack(ServerLevel level, int upgradeRank, Player player, LivingEntity livingTarget, LimaDynamicDamageSource damageSource)
    {
        damageSource.setKnockbackMultiplier(amount.calculate(upgradeRank));
    }
}