package liedge.ltxindustries.registry.game;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.client.particle.LimaParticleType;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.particle.HanabiParticleOptions;
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

    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> LIGHTFRAG_TRACER = registerColor("lightfrag_tracer", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_GLITTER = registerColorSize("color_glitter", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> COLOR_FLASH = registerColorSize("color_flash", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_FULL_SONIC_BOOM = registerColor("color_full_sonic_boom", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_HALF_SONIC_BOOM = registerColor("color_half_sonic_boom", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GROUND_ICICLE = registerSimple("icicle", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SNOWFLAKE = registerSimple("snowflake", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FREEZING_SNOWFLAKE = registerSimple("freezing_snowflake", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_SPARK = registerColor("color_spark", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> COLOR_TWINKLE_SPARK = registerColor("color_twinkle_spark", false);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> ROCKET_BURST = registerColor("rocket_burst", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> ENERGY_BOLT = registerColor("energy_bolt", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> CORROSIVE_DRIP = registerSimple("corrosive_drip", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_FALL = registerSimple("acid_fall", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> ACID_LAND = registerSimple("acid_land", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOOM_BURST = registerSimple("gloom_burst", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SMALL_GLOOM_PUFF = registerSimple("small_gloom_puff", false);
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> GLOOM_BURST_EMITTER =  registerSimple("gloom_burst_emitter", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<HanabiParticleOptions>> HANABI_EXPLOSION = PARTICLES.register("hanabi_explosion", () -> LimaParticleType.create(true, HanabiParticleOptions.CODEC, HanabiParticleOptions.STREAM_CODEC));
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> RAILGUN_BOLT = registerColor("railgun_bolt", true);
    public static final DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> SHIELD_BREAK = registerColorSize("shield_break", true);

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> registerSimple(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> new SimpleParticleType(overrideLimiter));
    }

    private static DeferredHolder<ParticleType<?>, LimaParticleType<ColorParticleOptions>> registerColor(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> ColorParticleOptions.createParticleType(overrideLimiter));
    }

    public static DeferredHolder<ParticleType<?>, LimaParticleType<ColorSizeParticleOptions>> registerColorSize(String name, boolean overrideLimiter)
    {
        return PARTICLES.register(name, () -> ColorSizeParticleOptions.createParticleType(overrideLimiter));
    }
}