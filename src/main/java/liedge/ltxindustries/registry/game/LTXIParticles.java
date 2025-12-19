package liedge.ltxindustries.registry.game;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.client.particle.LimaParticleType;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.particle.GrenadeExplosionParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIParticles
{
    private LTXIParticles() {}

    private static final DeferredRegister<ParticleType<?>> PARTICLES = LTXIndustries.RESOURCES.deferredRegister(Registries.PARTICLE_TYPE);

    public static void register(IEventBus bus)
    {
        PARTICLES.register(bus);
    }

    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> LIGHTFRAG_TRACER = PARTICLES.register("lightfrag_tracer", () -> ColorParticleOptions.createParticleType(true));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_GLITTER = PARTICLES.register("color_glitter", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_FLASH = PARTICLES.register("color_flash", () -> ColorSizeParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_FULL_SONIC_BOOM = PARTICLES.register("color_full_sonic_boom", () -> ColorParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_HALF_SONIC_BOOM = PARTICLES.register("color_half_sonic_boom", () -> ColorParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> HALF_SONIC_BOOM_EMITTER = PARTICLES.register("half_sonic_boom_emitter", () -> ColorParticleOptions.createParticleType(true));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GROUND_ICICLE = registerSimple("icicle", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CRYO_SNOWFLAKE = registerSimple("cryo_snowflake", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> MINI_ELECTRIC_SPARK = registerSimple("mini_electric_spark", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> FIXED_ELECTRIC_BOLT = PARTICLES.register("fixed_electric_bolt", () -> ColorParticleOptions.createParticleType(false));
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSIVE_DRIP = registerSimple("corrosive_drip", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_FALL = registerSimple("acid_fall", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_LAND = registerSimple("acid_land", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> NEURO_SMOKE = registerSimple("neuro_smoke", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<GrenadeExplosionParticleOptions>> GRENADE_EXPLOSION = PARTICLES.register("grenade_explosion", () -> LimaParticleType.create(true, GrenadeExplosionParticleOptions.CODEC, GrenadeExplosionParticleOptions.STREAM_CODEC));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> RAILGUN_BOLT = PARTICLES.register("railgun_bolt", () -> ColorParticleOptions.createParticleType(true));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> SHIELD_BREAK = PARTICLES.register("shield_break", () -> ColorSizeParticleOptions.createParticleType(true));

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimple(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }
}