package liedge.ltxindustries.entity;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.util.LimaNetworkUtil;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.game.LTXIEntities;
import liedge.ltxindustries.registry.game.LTXIParticles;
import liedge.ltxindustries.registry.game.LTXISounds;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.jspecify.annotations.Nullable;

public class Bomblet extends LTXIProjectileEntity implements IEntityWithComplexSpawn
{
    private int lifespan;
    private int color = -1;
    private float power;

    // client props
    private final int spinSpeed;
    private float oSpin;
    private float spin;

    public Bomblet(EntityType<?> type, Level level)
    {
        super(type, level);

        this.lifespan = random.nextIntBetweenInclusive(10, 25);
        this.spinSpeed = random.nextIntBetweenInclusive(30, 60);
    }

    public Bomblet(Level level, int color, float power)
    {
        this(LTXIEntities.BOMBLET.get(), level);
        this.color = color;
        this.power = power;
    }

    private void explode(ServerLevel level, @Nullable LivingEntity owner, Vec3 pos)
    {
        if (power <= 0f) return;

        getEntities(level, pos, 3.5d, owner, null).forEach(hit -> hurtEntity(level, LTXIDamageTypes.EXPLOSIVE, hit, owner, power));

        level.playSound(null, pos.x, pos.y, pos.z, LTXISounds.BOMBLET_EXPLODE, SoundSource.NEUTRAL, 3f, Mth.nextFloat(random, 0.9f, 1f));

        LimaNetworkUtil.sendAlwaysVisibleParticle(level, ColorSizeParticleOptions.of(LTXIParticles.COLOR_FLASH, color, 7f), pos);
        LimaNetworkUtil.sendAlwaysVisibleParticle(level, ColorParticleOptions.of(LTXIParticles.COLOR_HALF_SONIC_BOOM, color), pos);
    }

    @Override
    public int getColor()
    {
        return color;
    }

    @Override
    protected CollisionResult onCollision(ServerLevel level, @Nullable LivingEntity owner, HitResult hitResult, Vec3 hitLocation)
    {
        explode(level, owner, hitLocation);
        return CollisionResult.DESTROY;
    }

    @Override
    protected void onExpire(ServerLevel level, int age)
    {
        explode(level, getOwner(), position());
        super.onExpire(level, age);
    }

    @Override
    protected int getLifetime()
    {
        return lifespan;
    }

    @Override
    protected double getDefaultGravity()
    {
        return 0.0625d;
    }

    @Override
    protected void tickClient(Level level)
    {
        oSpin = spin;
        spin = (spin + spinSpeed) % 360f;

        if (tickCount % 2 == 0)
        {
            ParticleOptions particle = ColorSizeParticleOptions.of(LTXIParticles.COLOR_GLITTER, color, 0.325f);
            level.addParticle(particle, getX(), getY(0.5d), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input)
    {
        super.readAdditionalSaveData(input);

        this.lifespan = input.getIntOr(LimaCommonConstants.KEY_LIFESPAN, 25);
        this.color = input.getIntOr(LimaCommonConstants.KEY_COLOR, -1);
        this.power = input.getFloatOr(LimaCommonConstants.KEY_POWER, 0f);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output)
    {
        super.addAdditionalSaveData(output);

        output.putInt(LimaCommonConstants.KEY_LIFESPAN, lifespan);
        output.putInt(LimaCommonConstants.KEY_COLOR, color);
        output.putFloat(LimaCommonConstants.KEY_POWER, power);
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

    public float lerpSpin(float partialTick)
    {
        return Mth.rotLerp(partialTick, oSpin, spin);
    }
}