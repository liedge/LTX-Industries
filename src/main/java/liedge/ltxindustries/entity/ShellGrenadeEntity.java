package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.particle.GrenadeExplosionParticleOptions;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static liedge.ltxindustries.registry.game.LTXIMobEffects.*;
import static liedge.ltxindustries.util.config.LTXIWeaponsConfig.*;

public class ShellGrenadeEntity extends LTXIProjectileEntity implements IEntityWithComplexSpawn
{
    private GrenadeType grenadeType = GrenadeType.EXPLOSIVE;

    public ShellGrenadeEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public ShellGrenadeEntity(Level level, GrenadeType grenadeType, ItemStack launcherItem)
    {
        this(LTXIEntities.SHELL_GRENADE.get(), level);
        this.grenadeType = grenadeType;
        setWeaponItem(launcherItem.copy());
    }

    public GrenadeType getGrenadeType()
    {
        return grenadeType;
    }

    private void setGrenadeType(GrenadeType grenadeType)
    {
        this.grenadeType = grenadeType;
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
            case EXPLOSIVE -> LTXIDamageTypes.EXPLOSIVE_WEAPON;
            case FLAME -> LTXIDamageTypes.FLAME_GRENADE;
            case CRYO -> LTXIDamageTypes.CRYO_GRENADE;
            case ELECTRIC -> LTXIDamageTypes.ELECTRIC_GRENADE;
            case ACID -> LTXIDamageTypes.ACID_GRENADE;
            case NEURO -> LTXIDamageTypes.NEURO_GRENADE;
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
            case FLAME -> type.is(LTXITags.EntityTypes.WEAK_TO_FLAME) ? FLAME_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case CRYO -> type.is(LTXITags.EntityTypes.WEAK_TO_CRYO) ? CRYO_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case ELECTRIC -> (hitEntity.isInWaterOrRain() || type.is(LTXITags.EntityTypes.WEAK_TO_ELECTRIC)) ? ELECTRIC_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
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
            LimaNetworkUtil.sendParticle(level, new ColorParticleOptions(LTXIParticles.FIXED_ELECTRIC_BOLT, grenadeType.getColor()), LimaNetworkUtil.NORMAL_PARTICLE_DIST, hitLocation, hitEntity.getEyePosition());
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
        return 0.0125f;
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        double blastRadius = getBlastRadius();
        List<Entity> hits = getEntitiesInAOE(level, hitLocation, blastRadius, owner, null); // Use new helper

        // Spawn AOE entity
        if (grenadeType == GrenadeType.FLAME)
        {
            FlameFieldEntity flame = new FlameFieldEntity(level);
            flame.setOwner(owner);
            flame.setWeaponItem(getWeaponItem());
            flame.setPos(hitLocation.x, hitLocation.y - 0.5d, hitLocation.z);
            level.addFreshEntity(flame);
        }

        hits.forEach(hitEntity -> {
            // Apply potion effects
            if (hitEntity instanceof LivingEntity living)
            {
                applyPotionEffects(living);
            }

            // Deal damage to entities
            final double baseDamage = getBaseDamage() * getDamageMultiplier(hitEntity);
            LTXIItems.GRENADE_LAUNCHER.get().causeProjectileDamage(getWeaponItem(), this, owner, getDamageType(), hitEntity, baseDamage);

            // Spawn any additional particle effects on hit entities (if applicable)
            spawnHitEntityParticles(level, hitLocation, hitEntity);
        });

        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LTXISounds.GRENADE_EXPLOSIONS.get(grenadeType).get(), SoundSource.PLAYERS, 2.5f, Mth.randomBetween(random, 0.77f, 0.9f));
        LimaNetworkUtil.sendParticle(level, new GrenadeExplosionParticleOptions(grenadeType, blastRadius * 2d), LimaNetworkUtil.UNLIMITED_PARTICLE_DIST, hitLocation);

        return CollisionResult.DESTROY;
    }

    @Override
    protected void tickClient(Level level)
    {
        Vec3 p = LimaCoreMath.createMotionVector(getXRot(), getYRot(), -0.375d, 0d);
        double px = getX() + p.x();
        double py = getY(0.5d) + p.y();
        double pz = getZ() + p.z();
        level.addAlwaysVisibleParticle(new ColorSizeParticleOptions(LTXIParticles.COLOR_GLITTER, grenadeType.getColor(), 0.5f), true, px, py, pz, 0, 0, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setGrenadeType(GrenadeType.CODEC.byNameOrElse(tag.getString("grenade_type"), GrenadeType.EXPLOSIVE));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
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