package liedge.limatech.entity;

import liedge.limatech.registry.LimaTechDamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class LightfragEntity extends LimaTechProjectile
{
    public LightfragEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    @Override
    public int getLifetime()
    {
        return 40;
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 impactLocation)
    {
        if (hitResult instanceof EntityHitResult entityHitResult)
        {
            Entity hit = entityHitResult.getEntity();
            // TODO Un-hardcode damage number
            hit.hurt(level.damageSources().source(LimaTechDamageTypes.WEAPON_DAMAGE, this), 11.5f);
        }

        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide) {}
}