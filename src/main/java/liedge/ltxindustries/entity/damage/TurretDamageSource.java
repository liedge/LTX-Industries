package liedge.ltxindustries.entity.damage;

import com.google.common.base.Preconditions;
import liedge.ltxindustries.blockentity.BaseTurretBlockEntity;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurretDamageSource extends UpgradableDamageSource
{
    public static TurretDamageSource create(Level level, ResourceKey<DamageType> damageTypeKey, BaseTurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity owner, @Nullable Vec3 location)
    {
        Preconditions.checkArgument(!(directEntity == null && location == null), "Turret damage must have either a direct projectile entity or a location");
        return new TurretDamageSource(level.registryAccess().holderOrThrow(damageTypeKey), blockEntity, directEntity, owner, location);
    }

    private final BaseTurretBlockEntity blockEntity;

    private TurretDamageSource(Holder<DamageType> type, BaseTurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 location)
    {
        super(type, directEntity, causingEntity, location);
        this.blockEntity = blockEntity;
    }

    @Override
    public UpgradesContainerBase<?, ?> getUpgrades()
    {
        return blockEntity.getUpgrades();
    }

    @Override
    public @Nullable DropsRedirect createDropsRedirect()
    {
        return DropsRedirect.forMobDrops(blockEntity.getOutputInventory(), blockEntity.getProjectileStart(), getUpgrades());
    }
}