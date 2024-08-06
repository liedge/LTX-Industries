package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.EntityTypes.*;
import static net.minecraft.tags.EntityTypeTags.FREEZE_HURTS_EXTRA_TYPES;
import static net.minecraft.tags.EntityTypeTags.SENSITIVE_TO_IMPALING;
import static net.minecraft.world.entity.EntityType.*;

class EntityTagsGen extends LimaTagsProvider.RegistryTags<EntityType<?>>
{
    public EntityTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(output, BuiltInRegistries.ENTITY_TYPE, LimaTech.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(IMMUNE_TO_LTX_WEAPONS).add(ITEM, EXPERIENCE_ORB, ITEM_FRAME, GLOW_ITEM_FRAME, ARMOR_STAND);

        buildTag(WEAK_TO_FLAME).add(STRAY, POLAR_BEAR, SNOW_GOLEM);
        buildTag(WEAK_TO_FREEZE).addTag(FREEZE_HURTS_EXTRA_TYPES);
        buildTag(WEAK_TO_ELECTRIC).addTag(SENSITIVE_TO_IMPALING);

        buildTag(ROCKET_TURRET_TARGETS).add(PHANTOM, GHAST, BLAZE, ENDER_DRAGON, VEX, WITHER);
        buildTag(MEDIUM_THREAT_LEVEL).add(EVOKER, HOGLIN, ILLUSIONER, IRON_GOLEM, PIGLIN_BRUTE, RAVAGER, VINDICATOR, ZOGLIN);
        buildTag(HIGH_THREAT_LEVEL).add(ELDER_GUARDIAN, ENDER_DRAGON, WITHER, WARDEN);
    }
}