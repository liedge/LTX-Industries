package liedge.ltxindustries.entity;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.client.particle.HanabiParticleOptions;
import liedge.ltxindustries.data.LightColors;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import liedge.ltxindustries.world.level.DynamicClipContext;
import liedge.ltxindustries.world.level.FluidCollision;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static liedge.ltxindustries.registry.game.LTXIMobEffects.*;
import static liedge.ltxindustries.util.config.LTXIWeaponsConfig.*;

public class ShellGrenade extends LTXIProjectileEntity implements IEntityWithComplexSpawn
{
    private GrenadeType grenadeType = GrenadeType.SPARKLER;
    private int color = -1;

    public ShellGrenade(EntityType<?> type, Level level)
    {
        super(type, level);
    }

    public ShellGrenade(Level level, GrenadeType grenadeType, ItemStack weaponItem)
    {
        this(LTXIEntities.SHELL_GRENADE.get(), level);
        this.grenadeType = grenadeType;
        this.color = UpgradableEquipmentItem.getLightColorFrom(weaponItem, LightColors.Channel.ENERGY);
        setWeaponItem(weaponItem.copy());
    }

    public GrenadeType getGrenadeType()
    {
        return grenadeType;
    }

    @Override
    public int getColor()
    {
        return color;
    }

    private double getBlastRadius(Vec3 hitLocation)
    {
        return switch (getGrenadeType())
        {
            case SPARKLER -> 5d;
            case FLAME -> 3.5d;
            case CRYO -> 6d;
            case ELECTRIC -> wouldBeInRainOrWater(hitLocation) ? 10d : 5d;
            case ACID -> 2.5d;
            case GLOOM_GAS -> 4.5d;
        };
    }

    private ResourceKey<DamageType> getDamageType()
    {
        return switch (getGrenadeType())
        {
            case SPARKLER -> LTXIDamageTypes.EXPLOSIVE;
            case FLAME -> LTXIDamageTypes.FLAME;
            case CRYO -> LTXIDamageTypes.CRYO;
            case ELECTRIC -> LTXIDamageTypes.ELECTRIC;
            case ACID -> LTXIDamageTypes.ACID;
            case GLOOM_GAS -> LTXIDamageTypes.GLOOM_GAS;
        };
    }

    private boolean wouldBeInRainOrWater(Vec3 hitLocation)
    {
        Level level = level();
        AABB bb = makeBoundingBox(hitLocation).inflate(0.25d);
        return LimaBlockUtil.betweenClosedStreamSafe(level, bb).anyMatch(pos -> level.isRainingAt(pos) || level.getFluidState(pos).is(FluidTags.WATER));
    }

    private double getDamageMultiplier(Entity hitEntity)
    {
        return switch (grenadeType)
        {
            case FLAME -> hitEntity.is(LTXITags.EntityTypes.WEAK_TO_FLAME) ? FLAME_SHELL_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case CRYO -> hitEntity.is(LTXITags.EntityTypes.WEAK_TO_CRYO) ? CRYO_SHELL_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            case ELECTRIC -> (hitEntity.isInWaterOrRain() || hitEntity.is(LTXITags.EntityTypes.WEAK_TO_ELECTRIC)) ? ELECTRIC_SHELL_DAMAGE_MULTIPLIER.getAsDouble() : 1d;
            default -> 1d;
        };
    }

    private double getBaseDamage()
    {
        ModConfigSpec.DoubleValue configValue = switch (grenadeType)
        {
            case SPARKLER -> SPARKLER_SHELL_BASE_DAMAGE;
            case FLAME -> FLAME_SHELL_BASE_DAMAGE;
            case CRYO -> CRYO_SHELL_BASE_DAMAGE;
            case ELECTRIC -> ELECTRIC_SHELL_BASE_DAMAGE;
            case ACID -> ACID_SHELL_BASE_DAMAGE;
            case GLOOM_GAS -> GLOOM_GAS_SHELL_BASE_DAMAGE;
        };

        return configValue.getAsDouble();
    }

    private void applyPotionEffects(LivingEntity hitEntity)
    {
        MobEffectInstance instance = switch (grenadeType)
        {
            case CRYO -> new MobEffectInstance(FROSTBITE, 400, 2);
            case ACID -> new MobEffectInstance(CORROSIVE, 200, 2);
            case GLOOM_GAS -> new MobEffectInstance(GLOOM, 600, 2);
            default -> null;
        };

        if (instance != null) hitEntity.forceAddEffect(instance, getOwner());
    }

    private void spawnHitEntityParticles(ServerLevel level, Vec3 hitLocation, Entity hitEntity)
    {
        // Add electric bolt particles between impact and targets if electric
        if (grenadeType == GrenadeType.ELECTRIC)
        {
            LimaNetworkUtil.sendAlwaysVisibleParticle(level, ColorParticleOptions.of(LTXIParticles.ENERGY_BOLT, color), hitLocation, hitEntity.getEyePosition());
        }
    }

    @Override
    protected double getDefaultGravity()
    {
        return 0.0125d;
    }

    @Override
    protected ClipContext blockTraceContext(Vec3 start, Vec3 end, double blockPierce)
    {
        FluidCollision fluidCollision = grenadeType == GrenadeType.FLAME ? FluidCollision.WATER : FluidCollision.NONE;
        return new DynamicClipContext(start, end, this, fluidCollision, blockPierce);
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        double blastRadius = getBlastRadius(hitLocation);
        List<Entity> hits = getEntities(level, hitLocation, blastRadius, owner, null); // Use new helper

        switch (grenadeType)
        {
            case SPARKLER ->
            {
                spawnBomblets(level, hitLocation, owner, 0.1375d, 0.250d);
                spawnBomblets(level, hitLocation, owner, 0.325d, 0.45d);
            }
            case FLAME ->
            {
                if (wouldBeInRainOrWater(hitLocation))
                {
                    level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, SoundEvents.FIRE_EXTINGUISH, SoundSource.PLAYERS, 3f, 1f);
                    return CollisionResult.DESTROY;
                }

                HanabiFlameField flame = new HanabiFlameField(level);
                flame.setOwner(owner);
                flame.setWeaponItem(getWeaponItem().copy());

                Vec3 flamePos = hitResult instanceof EntityHitResult ehr ? ehr.getEntity().getBoundingBox().getBottomCenter() : hitLocation.subtract(0, 0.25d, 0);
                flame.setPos(flamePos);

                level.addFreshEntity(flame);
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
            hurtEntity(level, getDamageType(), hitEntity, owner, baseDamage);

            // Spawn any additional particle effects on hit entities (if applicable)
            spawnHitEntityParticles(level, hitLocation, hitEntity);
        });

        Holder<SoundEvent> sound = switch (grenadeType)
        {
            case SPARKLER -> LTXISounds.SPARKLER_SHELL_IMPACT;
            case FLAME -> LTXISounds.FLAME_SHELL_IMPACT;
            case CRYO -> LTXISounds.CRYO_SHELL_IMPACT;
            case ELECTRIC -> LTXISounds.ELECTRIC_SHELL_IMPACT;
            case ACID -> LTXISounds.ACID_SHELL_IMPACT;
            case GLOOM_GAS -> LTXISounds.GLOOM_GAS_SHELL_IMPACT;
        };
        level.playSound(null, hitLocation.x, hitLocation.y, hitLocation.z, sound, SoundSource.PLAYERS, 3f, Mth.randomBetween(random, 0.775f, 0.95f));

        LimaNetworkUtil.sendAlwaysVisibleParticle(level, new HanabiParticleOptions(grenadeType, color, (float) blastRadius * 2f), hitLocation);

        return CollisionResult.DESTROY;
    }

    @Override
    protected void tickClient(Level level)
    {
        Vec3 p = LimaCoreMath.createMotionVector(getXRot(), getYRot(), -0.375d, 0d);
        double px = getX() + p.x();
        double py = getY(0.5d) + p.y();
        double pz = getZ() + p.z();
        level.addAlwaysVisibleParticle(ColorSizeParticleOptions.of(LTXIParticles.COLOR_GLITTER, color, 0.5f), true, px, py, pz, 0, 0, 0);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);
        this.grenadeType = input.read("grenade_type", GrenadeType.CODEC).orElse(GrenadeType.SPARKLER);
        this.color = input.getIntOr(LTXIConstants.KEY_COLOR, -1);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);
        output.store("grenade_type", GrenadeType.CODEC, grenadeType);
        output.putInt(LTXIConstants.KEY_COLOR, color);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf net)
    {
        net.writeVarInt(color);
    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf net)
    {
        this.color = net.readVarInt();
    }

    private void spawnBomblets(ServerLevel level, Vec3 hitLocation, @Nullable LivingEntity owner, double minSpread, double maxSpread)
    {
        double arc = Math.PI * 2 / 7d;
        double arcOffset = random.nextDouble() * arc;
        float dmg = (float) SPARKLER_BOMBLET_BASE_DAMAGE.getAsDouble();

        for (int i = 0; i < 7; i++)
        {
            double a = arc * i + arcOffset;
            double spread = Mth.nextDouble(random, minSpread, maxSpread);

            double dx = Math.cos(a) * spread;
            double dy = Mth.nextDouble(random, 0.3d, 0.6d);
            double dz = Math.sin(a) * spread;

            Vec3 delta = new Vec3(dx, dy, dz);
            Vec3 pos = hitLocation.add(delta);

            HitResult clip = level.clip(new ClipContext(hitLocation, pos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
            if (clip.getType() == HitResult.Type.BLOCK)
            {
                Vec3 rd = getDeltaMovement().scale(-1d);
                float xRot = LimaCoreMath.getXRot(rd);
                float yRot = LimaCoreMath.getYRot(rd);

                delta = LimaCoreMath.relativePointToRotations(xRot, yRot, dx, dz, dy);
                pos = hitLocation.add(delta);
            }

            Bomblet bomblet = new Bomblet(level, color, dmg);
            bomblet.setOwner(owner);
            bomblet.setWeaponItem(getWeaponItem().copy());
            bomblet.setPos(pos);
            bomblet.setDeltaMovement(delta);

            level.addFreshEntity(bomblet);
        }
    }
}