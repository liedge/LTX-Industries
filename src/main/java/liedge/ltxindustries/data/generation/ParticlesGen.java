package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaParticleDescriptionProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static liedge.limacore.lib.ModResources.MC;
import static liedge.ltxindustries.registry.game.LTXIParticles.*;

class ParticlesGen extends LimaParticleDescriptionProvider
{
    ParticlesGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LTXIndustries.RESOURCES, helper);
    }

    @Override
    protected void addDescriptions()
    {
        spriteSet(COLOR_GLITTER, MC.location("glitter"), 8, true);
        sprite(COLOR_FLASH, MC.location("flash"));
        spriteSet(COLOR_FULL_SONIC_BOOM, "color_sonic_boom", 16, false);
        spriteSet(COLOR_HALF_SONIC_BOOM, "color_sonic_boom", 6, 16, false);
        spriteSet(CRYO_SNOWFLAKE, "cryo_snowflake", 10, false);
        spriteSet(MINI_ELECTRIC_SPARK, MC.location("spark"), 8, false);
        sprite(CORROSIVE_DRIP, MC.location("drip_fall"));
        sprite(ACID_FALL, MC.location("drip_fall"));
        sprite(ACID_LAND, MC.location("drip_land"));
        spriteSet(NEURO_SMOKE, MC.location("big_smoke"), 1, 8, false);
    }
}