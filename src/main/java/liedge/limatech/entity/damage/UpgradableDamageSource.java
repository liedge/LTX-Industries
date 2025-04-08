package liedge.limatech.entity.damage;

import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public abstract class UpgradableDamageSource extends DamageSource
{
    protected UpgradableDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 damageSourcePosition)
    {
        super(type, directEntity, causingEntity, damageSourcePosition);
    }

    protected UpgradableDamageSource(Holder<DamageType> type, @Nullable Entity directEntity, @Nullable Entity causingEntity)
    {
        super(type, directEntity, causingEntity);
    }

    public abstract UpgradesContainerBase<?, ?> getUpgrades();

    public abstract @Nullable DropsRedirect createDropsRedirect();
}