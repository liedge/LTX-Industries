package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreObjects;
import liedge.ltxindustries.lib.upgrades.DropsCapture;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class EffectDamageSource extends UpgradesAwareDamageSource
{
    private final Upgrades upgrades;

    public EffectDamageSource(Holder<DamageType> damageType, @Nullable LivingEntity source, Upgrades upgrades)
    {
        super(damageType, source, source);
        this.upgrades = upgrades;
    }

    @Override
    public Upgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable DropsCapture getDropsCapture()
    {
        Player player = LimaCoreObjects.tryCast(Player.class, getEntity());
        return player != null ? DropsCapture.mobDropsToPlayer(player, upgrades) : null;
    }

    @Override
    public @Nullable ItemStack getWeaponItem()
    {
        return null;
    }

    @Override
    public boolean canApplyEffects()
    {
        return false;
    }
}