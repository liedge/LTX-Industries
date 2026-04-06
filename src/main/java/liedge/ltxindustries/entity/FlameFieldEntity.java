package liedge.ltxindustries.entity;

import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class FlameFieldEntity extends UpgradesAwareEntity
{
    public FlameFieldEntity(EntityType<?> entityType, Level level)
    {
        super(entityType, level);
    }

    public FlameFieldEntity(Level level)
    {
        this(LTXIEntities.FLAME_FIELD.get(), level);
    }

    @Override
    public void tick()
    {
        super.tick();

        Level level = level();

        // 5 second lifetime
        if (tickCount > 100)
        {
            discard();
        }

        if (isInWaterOrRain())
        {
            discard();
            level.playSound(null, getX(), getY(), getZ(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL);
        }

        if (level instanceof ServerLevel serverLevel && tickCount % 4 == 0)
        {
            LivingEntity owner = getOwner();
            getEntitiesInAOE(serverLevel, getBoundingBox(), owner, null).forEach(hit ->
            {
                if (LTXIItems.HANABI.get().causeProjectileDamage(serverLevel, hit, this, owner, LTXIDamageTypes.STICKY_FLAME, getWeaponItem(), 4d))
                    hit.setRemainingFireTicks(400);
            });
        }
        else
        {
            // Particle layout: flame particle 'carpet', floating flames, and smokes

            // Flame particles
            for (int i = 0; i < 12; i++)
            {
                level.addParticle(ParticleTypes.FLAME, getRandomX(0.5d), getY() + 0.125d, getRandomZ(0.5d), 0d, 0.19d, 0d);
            }

            // Lava or smoke particles
            if (random.nextBoolean())
            {
                level.addParticle(ParticleTypes.LAVA, getRandomX(0.5d), getRandomY(), getRandomZ(0.5d), Mth.randomBetween(random, -0.25f, 0.25f), 0.25d, Mth.randomBetween(random, -0.25f, 0.25f));
            }
            else
            {
                level.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE, getRandomX(0.5f), getRandomY(), getRandomZ(0.5f), 0, 0.2d, 0);
            }
        }
    }
}