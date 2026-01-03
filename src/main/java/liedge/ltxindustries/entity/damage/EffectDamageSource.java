package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class EffectDamageSource extends UpgradesAwareDamageSource
{
    private final UpgradesContainerBase<?, ?> upgrades;

    public EffectDamageSource(Holder<DamageType> damageType, @Nullable LivingEntity source, UpgradesContainerBase<?, ?> upgrades)
    {
        super(damageType, source, source);
        this.upgrades = upgrades;
    }

    @Override
    public UpgradesContainerBase<?, ?> getUpgrades()
    {
        return upgrades;
    }

    @Override
    public @Nullable DropsRedirect createDropsRedirect()
    {
        Player player = LimaCoreUtil.castOrNull(Player.class, getEntity());
        return player != null ? DropsRedirect.forMobDrops(player, upgrades) : null;
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