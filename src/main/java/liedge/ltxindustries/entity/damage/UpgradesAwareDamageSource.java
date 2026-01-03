package liedge.ltxindustries.entity.damage;

import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradesAwareDamageSource extends DamageSource
{
    protected UpgradesAwareDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    protected UpgradesAwareDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity)
    {
        super(type, directEntity, causingEntity);
    }

    public abstract UpgradesContainerBase<?, ?> getUpgrades();

    public abstract @Nullable DropsRedirect createDropsRedirect();

    public boolean canApplyEffects()
    {
        return true;
    }
}