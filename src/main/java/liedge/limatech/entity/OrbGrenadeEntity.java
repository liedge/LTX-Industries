package liedge.limatech.entity;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.limatech.LimaTechTags;
import liedge.limatech.client.particle.GrenadeExplosionParticleOptions;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.registry.*;
import liedge.limatech.upgradesystem.ItemEquipmentUpgrades;
import liedge.limatech.upgradesystem.effect.EquipmentUpgradeEffect;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;

import java.util.List;

import static liedge.limatech.registry.LimaTechMobEffects.*;
import static liedge.limatech.util.config.LimaTechWeaponsConfig.*;

public class OrbGrenadeEntity extends LimaTechProjectile implements IEntityWithComplexSpawn
{
    public static final double FLAME_BLAST_RADIUS = 9d;

    private GrenadeType grenadeType = GrenadeType.EXPLOSIVE;
    private ItemEquipmentUpgrades upgrades = ItemEquipmentUpgrades.EMPTY;
    private float spin0;
    private float spin;

    public OrbGrenadeEntity(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public OrbGrenadeEntity(Level level, GrenadeType grenadeType, ItemEquipmentUpgrades upgrades)
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
            case EXPLOSIVE -> 10d;
            case FLAME -> FLAME_BLAST_RADIUS;
            case FREEZE -> 11d;
            case ELECTRIC -> checkGrenadeInRainOrWater() ? 16d : 8d;
            case ACID, NEURO -> 5d;
        };
    }

    private ResourceKey<DamageType> getDamageType()
    {
        return switch (getGrenadeType())
        {
            case EXPLOSIVE -> LimaTechDamageTypes.EXPLOSIVE_GRENADE;
            case FLAME -> LimaTechDamageTypes.FLAME_GRENADE;
            case FREEZE -> LimaTechDamageTypes.FREEZE_GRENADE;
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
            case FREEZE -> type.is(LimaTechTags.EntityTypes.WEAK_TO_FREEZE) ? FREEZE_GRENADE_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
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
            case FREEZE -> FREEZE_GRENADE_BASE_DAMAGE;
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
            case FREEZE -> new MobEffectInstance(FREEZING, 400, 1);
            case ACID -> new MobEffectInstance(CORROSIVE, 200, 2);
            case NEURO -> new MobEffectInstance(NEURO, 600, 2);
            default -> null;
        };

        if (instance != null) hitEntity.forceAddEffect(instance, getOwner());
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

        double radius = getBlastRadius();
        List<Entity> hits = level.getEntities(this, AABB.ofSize(hitLocation, radius, radius, radius), e -> LimaTechEntityUtil.isValidWeaponTarget(owner, e));

        // Spawn AOE entity
        if (grenadeType == GrenadeType.FLAME)
        {
            for (int i = 0; i < 9; i++)
            {
                int offsetX = (i % 3) - 1;
                int offsetZ = (i / 3) - 1;

                StickyFlameEntity flames = new StickyFlameEntity(level);
                flames.setOwner(owner);
                flames.setPos(hitLocation.x + 2f * offsetX, hitLocation.y, hitLocation.z + 2f * offsetZ);
                level.addFreshEntity(flames);
            }
        }

        hits.forEach(hitEntity -> {
            // Deal damage to entities
            final double baseDamage = getBaseDamage() * getDamageMultiplier(hitEntity);
            LimaTechItems.GRENADE_LAUNCHER.get().causeProjectileDamage(upgrades, this, owner, getDamageType(), hitEntity, baseDamage);

            // Apply potion effects
            if (hitEntity instanceof LivingEntity living)
            {
                applyPotionEffects(living);
            }

            // Add electric bolt particles between impact and targets if electric
            if (grenadeType == GrenadeType.ELECTRIC)
            {
                LimaNetworkUtil.spawnAlwaysVisibleParticle(level, LimaTechParticles.FIXED_ELECTRIC_BOLT, hitLocation, hitEntity.getEyePosition());
            }
        });

        if (upgrades.noEffectMatches(EquipmentUpgradeEffect::preventsWeaponVibrationEvent)) level.gameEvent(owner, LimaTechGameEvents.PROJECTILE_EXPLODED, hitLocation);

        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, LimaTechSounds.GRENADE_SOUNDS.get(grenadeType).get(), SoundSource.PLAYERS, 2.5f, 0.9f);
        LimaNetworkUtil.spawnAlwaysVisibleParticle(level, new GrenadeExplosionParticleOptions(grenadeType, radius), hitLocation);
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
        setGrenadeType(GrenadeType.CODEC.byName(tag.getString("grenade_type")));

        if (tag.contains("upgrades")) upgrades = LimaNbtUtil.codecDecode(ItemEquipmentUpgrades.CODEC, registryAccess(), tag, "upgrades");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putString("grenade_type", grenadeType.getSerializedName());

        if (upgrades != ItemEquipmentUpgrades.EMPTY) tag.put("upgrades", LimaNbtUtil.codecEncode(ItemEquipmentUpgrades.CODEC, registryAccess(), upgrades));
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