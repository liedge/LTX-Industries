package liedge.limatech.entity;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.particle.ColorEndpointParticleOptions;
import liedge.limatech.client.particle.GrenadeExplosionParticleOptions;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrades;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.bootstrap.LimaTechDamageTypes;
import liedge.limatech.registry.game.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.List;

import static liedge.limatech.registry.game.LimaTechMobEffects.*;
import static liedge.limatech.util.config.LimaTechWeaponsConfig.*;

public class OrbGrenadeEntity extends LimaTraceableProjectile implements IEntityWithComplexSpawn
{
    private GrenadeType grenadeType = GrenadeType.EXPLOSIVE;
    private EquipmentUpgrades upgrades = EquipmentUpgrades.EMPTY;
    private float spin0;
    private float spin;

    public OrbGrenadeEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public OrbGrenadeEntity(Level level, GrenadeType grenadeType, EquipmentUpgrades upgrades)
    {
        this(LimaTechEntities.ORB_GRENADE.get(), level);
        this.grenadeType = grenadeType;
        this.upgrades = upgrades;
    }

    public GrenadeType getGrenadeType()
    {
        return grenadeType;
    }

    private void setGrenadeType(GrenadeType grenadeType)
    {
        this.grenadeType = grenadeType;
    }

    public float lerpSpinAnimation(float partialTick)
    {
        return Mth.rotLerp(partialTick, spin0, spin);
    }

    private double getBlastRadius()
    {
        return switch (getGrenadeType())
        {
            case EXPLOSIVE -> 5d;
            case FLAME -> 4.5d;
            case CRYO -> 6d;
            case ELECTRIC -> checkGrenadeInRainOrWater() ? 10d : 5d;
            case ACID, NEURO -> 2.5d;
        };
    }

    private ResourceKey<DamageType> getDamageType()
    {
        return switch (getGrenadeType())
        {
            case EXPLOSIVE -> LimaTechDamageTypes.EXPLOSIVE_GRENADE;
            case FLAME -> LimaTechDamageTypes.FLAME_GRENADE;
            case CRYO -> LimaTechDamageTypes.CRYO_GRENADE;
            case ELECTRIC -> LimaTechDamageTypes.ELECTRIC_GRENADE;
            case ACID -> LimaTechDamageTypes.ACID_GRENADE;
            case NEURO -> LimaTechDamageTypes.NEURO_GRENADE;
        };
    }

    private boolean checkGrenadeInRainOrWater()
    {
        Level level = level();
        return LimaBlockUtil.betweenClosedStreamSafe(level, getBoundingBox().inflate(0.001d)).anyMatch(pos -> level.isRainingAt(pos) || level.getFluidState(pos).is(Tags.Fluids.WATER));
    }

    private double getDamageMultiplier(Entity hitEntity)
    {
        EntityType<?> type = hitEntity.getType();
        return switch (grenadeType)
        {
            case FLAME -> type.is(LimaTechTags.EntityTypes.WEAK_TO_FLAME) ? FLAME_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case CRYO -> type.is(LimaTechTags.EntityTypes.WEAK_TO_CRYO) ? CRYO_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case ELECTRIC -> (hitEntity.isInWaterOrRain() || type.is(LimaTechTags.EntityTypes.WEAK_TO_ELECTRIC)) ? ELECTRIC_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            default -> 1d;
        };
    }

    private double getBaseDamage()
    {
        ModConfigSpec.DoubleValue configValue = switch (grenadeType)
        {
            case EXPLOSIVE -> EXPLOSIVE_GRENADE_BASE_DAMAGE;
            case FLAME -> FLAME_GRENADE_BASE_DAMAGE;
            case CRYO -> CRYO_GRENADE_BASE_DAMAGE;
            case ELECTRIC -> ELECTRIC_GRENADE_BASE_DAMAGE;
            case ACID -> ACID_GRENADE_BASE_DAMAGE;
            case NEURO -> NEURO_GRENADE_BASE_DAMAGE;
        };

        return configValue.getAsDouble();
    }

    private void applyPotionEffects(LivingEntity hitEntity)
    {
        MobEffectInstance instance = switch (grenadeType)
        {
            case CRYO -> new MobEffectInstance(FROSTBITE, 400, 2);
            case ACID -> new MobEffectInstance(CORROSIVE, 200, 2);
            case NEURO -> new MobEffectInstance(NEURO_SUPPRESSED, 600, 2);
            default -> null;
        };

        if (instance != null) hitEntity.forceAddEffect(instance, getOwner());
    }

    private void spawnHitEntityParticles(Level level, Vec3 hitLocation, Entity hitEntity)
    {
        // Add electric bolt particles between impact and targets if electric
        if (grenadeType == GrenadeType.ELECTRIC)
        {
            ColorEndpointParticleOptions options = new ColorEndpointParticleOptions(LimaTechParticles.FIXED_ELECTRIC_BOLT, grenadeType.getColor(), hitEntity.getEyePosition());
            LimaNetworkUtil.sendSingleParticle(level, options, null, true, LimaNetworkUtil.NORMAL_PARTICLE_DIST, hitLocation);
        }
    }

    @Override
    public int getLifetime()
    {
        return 200;
    }

    @Override
    protected float getProjectileGravity()
    {
        if (grenadeType == GrenadeType.ELECTRIC && isInWater())
        {
            return 0f;
        }
        else
        {
            return 0.0325f;
        }
    }

    @Override
    protected void onProjectileHit(Level level, HitResult hitResult, Vec3 hitLocation)
    {
        LivingEntity owner = getOwner();

        double blastRadius = getBlastRadius();
        List<Entity> hits = getEntitiesInAOE(level, hitLocation, blastRadius, owner); // Use new helper

        // Spawn AOE entity
        if (grenadeType == GrenadeType.FLAME)
        {
            for (int i = 0; i < 9; i++)
            {
                int offsetX = (i % 3) - 1;
                int offsetZ = (i / 3) - 1;

                StickyFlameEntity flames = new StickyFlameEntity(level, upgrades);
                flames.setOwner(owner);
                flames.setPos(hitLocation.x + 2f * offsetX, hitLocation.y, hitLocation.z + 2f * offsetZ);
                level.addFreshEntity(flames);
            }
        }

        hits.forEach(hitEntity -> {
            // Apply potion effects
            if (hitEntity instanceof LivingEntity living)
            {
                applyPotionEffects(living);
            }

            // Deal damage to entities
            final double baseDamage = getBaseDamage() * getDamageMultiplier(hitEntity);
            LimaTechItems.GRENADE_LAUNCHER.get().causeProjectileDamage(upgrades, this, owner, getDamageType(), hitEntity, baseDamage);

            // Spawn any additional particle effects on hit entities (if applicable)
            spawnHitEntityParticles(level, hitLocation, hitEntity);
        });

        // Fire the sculk event if no suppression upgrade is present
        boolean fireEvent = upgrades.noneMatch(LimaTechUpgradeEffectComponents.PREVENT_VIBRATION.get(), (effect, $) -> effect.apply(null, LimaTechGameEvents.PROJECTILE_EXPLODED));
        if (fireEvent) level.gameEvent(owner, LimaTechGameEvents.PROJECTILE_EXPLODED, hitLocation);

        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LimaTechSounds.GRENADE_EXPLOSIONS.get(grenadeType).get(), SoundSource.PLAYERS, 2.5f, Mth.randomBetween(random, 0.77f, 0.9f));
        LimaNetworkUtil.sendSingleParticle(level, new GrenadeExplosionParticleOptions(grenadeType, blastRadius * 2d), null, true, LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);
        discard();
    }

    @Override
    protected void tickProjectile(Level level, boolean isClientSide)
    {
        if (isClientSide)
        {
            float speed = (float) getDeltaMovement().length();
            spin0 = spin;
            spin = (spin - (60 * speed)) % 360;

            if (tickCount % 2 == 0)
            {
                double dx = getX() + (random.nextDouble() - random.nextDouble()) * 0.2d;
                double dz = getZ() + (random.nextDouble() - random.nextDouble()) * 0.2d;
                level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LimaTechParticles.COLOR_GLITTER, grenadeType.getColor(), 1.125f), true, dx, getY(0.5d), dz, 0, 0, 0);
            }
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        upgrades = readEquipmentUpgrades(tag);
        setGrenadeType(GrenadeType.CODEC.byNameOrElse(tag.getString("grenade_type"), GrenadeType.EXPLOSIVE));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        writeEquipmentUpgrades(upgrades, tag);
        tag.putString("grenade_type", grenadeType.getSerializedName());
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer)
    {
        GrenadeType.STREAM_CODEC.encode(buffer, grenadeType);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData)
    {
        setGrenadeType(GrenadeType.STREAM_CODEC.decode(additionalData));
    }
}