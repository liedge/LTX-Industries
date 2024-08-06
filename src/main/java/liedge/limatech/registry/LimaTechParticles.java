package liedge.limatech.registry;

import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.client.particle.LimaParticleType;
import liedge.limatech.LimaTech;
import liedge.limatech.client.particle.GrenadeExplosionParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechParticles
{
    private LimaTechParticles() {}

    private static final DeferredRegister<ParticleType<?>> PARTICLES = LimaTech.RESOURCES.deferredRegister(Registries.PARTICLE_TYPE);

    public static void initRegister(IEventBus bus)
    {
        PARTICLES.register(bus);
    }

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> LIGHTFRAG_TRACER = registerSimple("lightfrag_tracer", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_GLITTER = PARTICLES.register("color_glitter", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_FLASH = PARTICLES.register("color_flash", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HALF_SONIC_BOOM = registerSimple("half_sonic_boom", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HALF_SONIC_BOOM_EMITTER = registerSimple("half_sonic_boom_emitter", true);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GROUND_ICICLE = registerSimple("icicle", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FREEZE_SNOWFLAKE = registerSimple("freeze_snowflake", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINI_ELECTRIC_SPARK = registerSimple("mini_electric_spark", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FIXED_ELECTRIC_BOLT = registerSimple("fixed_electric_bolt", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSIVE_DRIP = registerSimple("corrosive_drip", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_FALL = registerSimple("acid_fall", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_LAND = registerSimple("acid_land", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NEURO_SMOKE = registerSimple("neuro_smoke", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<GrenadeExplosionParticleOptions>> GRENADE_EXPLOSION = PARTICLES.register("grenade_explosion", () -> LimaParticleType.create(true, GrenadeExplosionParticleOptions.CODEC, GrenadeExplosionParticleOptions.STREAM_CODEC));

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimple(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}