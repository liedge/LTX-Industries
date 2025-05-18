package liedge.limatech.registry.game;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.client.particle.LimaParticleType;
import liedge.limatech.LimaTech;
import liedge.limatech.client.particle.ColorEndpointParticleOptions;
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

    public static void register(IEventBus bus)
    {
        PARTICLES.register(bus);
    }

    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorEndpointParticleOptions>> LIGHTFRAG_TRACER = PARTICLES.register("lightfrag_tracer", () -> ColorEndpointParticleOptions.type(true));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorEndpointParticleOptions>> LINEAR_FUSION_BOLT = PARTICLES.register("linear_fusion_bolt", () -> ColorEndpointParticleOptions.type(true));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_GLITTER = PARTICLES.register("color_glitter", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_FLASH = PARTICLES.register("color_flash", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_FULL_SONIC_BOOM = PARTICLES.register("color_full_sonic_boom", () -> ColorParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_HALF_SONIC_BOOM = PARTICLES.register("color_half_sonic_boom", () -> ColorParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> HALF_SONIC_BOOM_EMITTER = PARTICLES.register("half_sonic_boom_emitter", () -> ColorParticleOptions.createParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GROUND_ICICLE = registerSimple("icicle", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRYO_SNOWFLAKE = registerSimple("cryo_snowflake", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINI_ELECTRIC_SPARK = registerSimple("mini_electric_spark", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorEndpointParticleOptions>> FIXED_ELECTRIC_BOLT = PARTICLES.register("fixed_electric_bolt", () -> ColorEndpointParticleOptions.type(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSIVE_DRIP = registerSimple("corrosive_drip", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_FALL = registerSimple("acid_fall", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_LAND = registerSimple("acid_land", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NEURO_SMOKE = registerSimple("neuro_smoke", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<GrenadeExplosionParticleOptions>> GRENADE_EXPLOSION = PARTICLES.register("grenade_explosion", () -> LimaParticleType.create(true, GrenadeExplosionParticleOptions.CODEC, GrenadeExplosionParticleOptions.STREAM_CODEC));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RAILGUN_BOLT = registerSimple("railgun_bolt", true);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimple(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}