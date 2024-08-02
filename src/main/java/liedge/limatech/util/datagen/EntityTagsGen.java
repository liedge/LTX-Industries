package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.TagBuilderHelper;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.EntityTypes.*;
import static net.minecraft.world.entity.EntityType.*;

class EntityTagsGen extends EntityTypeTagsProvider implements TagBuilderHelper<EntityType<?>>
{
    public EntityTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper)
    {
        super(output, registries, LimaTech.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(FLYING_MOBS).add(PHANTOM, GHAST, BLAZE, ENDER_DRAGON, VEX, WITHER);

        // Elite mobs
        buildTag(ELITE_MOBS).add(EVOKER, HOGLIN, ILLUSIONER, IRON_GOLEM, PIGLIN_BRUTE, RAVAGER, VINDICATOR, ZOGLIN);

        // Boss mobs
        buildTag(BOSS_MOBS).add(ELDER_GUARDIAN, ENDER_DRAGON, WITHER, WARDEN);
    }

    @Override
    public TagBuilder getOrCreateRawBuilder(TagKey<EntityType<?>> tagKey)
    {
        return super.getOrCreateRawBuilder(tagKey);
    }

    @Override
    public @Nullable Registry<EntityType<?>> getTagRegistry()
    {
        return BuiltInRegistries.ENTITY_TYPE;
    }
}