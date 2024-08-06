package liedge.limatech.upgradesystem.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.LimaTechCapabilities;
import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.registry.LimaTechEquipmentUpgrades;
import liedge.limatech.lib.weapons.WeaponDamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public class BubbleShieldUpgradeEffect implements EquipmentUpgradeEffect
{
    public static final MapCodec<BubbleShieldUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("shield_amount").forGetter(o -> o.shieldAmount),
            LevelBasedValue.CODEC.fieldOf("max_shield").forGetter(o -> o.maxShield))
            .apply(instance, BubbleShieldUpgradeEffect::new));

    private final LevelBasedValue shieldAmount;
    private final LevelBasedValue maxShield;

    public BubbleShieldUpgradeEffect(LevelBasedValue shieldAmount, LevelBasedValue maxShield)
    {
        this.shieldAmount = shieldAmount;
        this.maxShield = maxShield;
    }

    @Override
    public UpgradeEffectType<?> getType()
    {
        return LimaTechEquipmentUpgrades.BUBBLE_SHIELD_RESTORE.get();
    }

    @Override
    public void onWeaponPlayerKill(WeaponDamageSource damageSource, Player player, LivingEntity targetEntity, int upgradeRank)
    {
        if (!player.level().isClientSide())
        {
            BubbleShieldUser shieldUser = player.getCapability(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD);
            if (shieldUser != null) shieldUser.restoreShieldHealth(shieldAmount.calculate(upgradeRank), maxShield.calculate(upgradeRank));
        }
    }
}