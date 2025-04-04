package liedge.limatech.lib;

import com.google.common.base.Preconditions;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.blockentity.BaseTurretBlockEntity;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurretDamageSource extends LimaDynamicDamageSource
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

    public BaseTurretBlockEntity getBlockEntity()
    {
        return blockEntity;
    }
}