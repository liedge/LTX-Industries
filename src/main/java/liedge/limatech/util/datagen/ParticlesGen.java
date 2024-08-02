package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaParticleDefinitionsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import static liedge.limacore.lib.ModResources.MC;
import static liedge.limatech.registry.LimaTechParticles.*;

class ParticlesGen extends LimaParticleDefinitionsProvider
{
    ParticlesGen(PackOutput output, ExistingFileHelper helper)
    {
        super(output, LimaTech.RESOURCES, helper);
    }

    @Override
    protected void defineParticles()
    {
        orderedSpriteSet(MISSILE_TRAIL.get(), MC.location("glitter"), 8, true);
        orderedSpriteSet(MINI_ELECTRIC_SPARK.get(), MC.location("spark"), 8, false);
        singleSprite(ACID_FALL_AMBIENT.get(), MC.location("drip_fall"));
        singleSprite(ACID_FALL.get(), MC.location("drip_fall"));
        singleSprite(ACID_LAND.get(), MC.location("drip_land"));
        orderedSpriteSet(HALF_SONIC_BOOM.get(), "color_sonic_boom", 6, 16, false);
    }
}