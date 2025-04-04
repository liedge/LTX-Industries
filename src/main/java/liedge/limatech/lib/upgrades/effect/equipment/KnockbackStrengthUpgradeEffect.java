package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.game.LimaTechEquipmentUpgradeEffects;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record KnockbackStrengthUpgradeEffect(LevelBasedValue amount) implements EquipmentUpgradeEffect.DamageModification
{
    public static final MapCodec<KnockbackStrengthUpgradeEffect> CODEC = LevelBasedValue.CODEC.fieldOf("amount").xmap(KnockbackStrengthUpgradeEffect::new, KnockbackStrengthUpgradeEffect::amount);

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEquipmentUpgradeEffects.KNOCKBACK_STRENGTH_EQUIPMENT_EFFECT.get();
    }

    @Override
    public void modifyDynamicAttack(Player player, int upgradeRank, LivingEntity target, LimaDynamicDamageSource damageSource)
    {
        damageSource.setKnockbackMultiplier(amount.calculate(upgradeRank));
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        Component amtComponent = LimaTechTooltipUtil.percentageWithSign(amount.calculate(upgradeRank) - 1f, false);
        return LimaTechLang.WEAPON_KNOCKBACK_EFFECT.translateArgs(amtComponent);
    }
}