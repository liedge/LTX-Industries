package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaParticleDescriptionProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.data.PackOutput;

import static liedge.limacore.lib.ModResources.MC;
import static liedge.ltxindustries.registry.game.LTXIParticles.*;

class ParticlesGen extends LimaParticleDescriptionProvider
{
    ParticlesGen(PackOutput output)
    {
        super(output, LTXIndustries.RESOURCES);
    }

    @Override
    protected void addDescriptions()
    {
        spriteSet(COLOR_GLITTER, MC.id("glitter"), 8, true);
        sprite(COLOR_FLASH, MC.id("flash"));
        spriteSet(COLOR_FULL_SONIC_BOOM, "color_sonic_boom", 16, false);
        spriteSet(COLOR_HALF_SONIC_BOOM, "color_sonic_boom", 6, 16, false);
        spriteSet(CRYO_SNOWFLAKE, "cryo_snowflake", 10, false);
        spriteSet(MINI_ELECTRIC_SPARK, MC.id("spark"), 8, false);
        sprite(CORROSIVE_DRIP, MC.id("drip_fall"));
        sprite(ACID_FALL, MC.id("drip_fall"));
        sprite(ACID_LAND, MC.id("drip_land"));
        spriteSet(NEURO_SMOKE, MC.id("big_smoke"), 1, 8, false);
    }
}