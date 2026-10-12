package liedge.ltxindustries.entity.damage;

import com.google.common.base.Preconditions;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.turret.TurretBlockEntity;
import liedge.ltxindustries.lib.upgrades.DropsCapture;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TurretDamageSource extends LTXIDamageSource
{
    public static TurretDamageSource create(Level level, ResourceKey<DamageType> damageTypeKey, TurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity owner, @Nullable Vec3 location)
    {
        Preconditions.checkArgument(!(directEntity == null && location == null), "Turret damage must have either a direct projectile entity or a location");
        return new TurretDamageSource(level.registryAccess().holderOrThrow(damageTypeKey), blockEntity, directEntity, owner, location);
    }

    private final TurretBlockEntity blockEntity;

    private TurretDamageSource(Holder<DamageType> type, TurretBlockEntity blockEntity, @Nullable Entity directEntity, @Nullable Entity causingEntity, @Nullable Vec3 pos)
    {
        super(type, directEntity, causingEntity, pos, blockEntity.getUpgrades(), null, true);
        this.blockEntity = blockEntity;
    }

    @Override
    public @Nullable DropsCapture getDropsCapture()
    {
        return DropsCapture.mobDropsToContainer(blockEntity.getItemsOrThrow(BlockContentsType.OUTPUT), blockEntity.getTraceStart(), getUpgrades());
    }
}