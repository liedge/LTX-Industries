package liedge.ltxindustries.entity.damage;

import liedge.limacore.util.LimaCoreUtil;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import liedge.ltxindustries.lib.upgrades.effect.equipment.DirectDropsUpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class UpgradablePlayerDamageSource extends UpgradableDamageSource
{
    private final UpgradesContainerBase<?, ?> upgrades;

    public UpgradablePlayerDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition, UpgradesContainerBase<?, ?> upgrades)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
        this.upgrades = upgrades;
    }

    public UpgradablePlayerDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, UpgradesContainerBase<?, ?> upgrades)
    {
        super(type, directEntity, causingEntity);
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
        return player != null ? DropsRedirect.forPlayer(player, upgrades, DirectDropsUpgradeEffect.Type.ENTITY_DROPS) : null;
    }
}