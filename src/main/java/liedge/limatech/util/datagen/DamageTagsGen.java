package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.TagBuilderHelper;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.registry.LimaTechDamageTypes.*;
import static net.minecraft.tags.DamageTypeTags.AVOIDS_GUARDIAN_THORNS;
import static net.minecraft.tags.DamageTypeTags.BYPASSES_COOLDOWN;

class DamageTagsGen extends DamageTypeTagsProvider implements TagBuilderHelper<DamageType>
{
    public DamageTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper)
    {
        super(output, registries, LimaTech.MODID, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        buildTag(AVOIDS_GUARDIAN_THORNS).add(WEAPON_DAMAGE, LIGHTFRAG, ROCKET_TURRET);
        buildTag(BYPASSES_COOLDOWN).add(WEAPON_DAMAGE, LIGHTFRAG);
    }

    @Override
    public TagBuilder getOrCreateRawBuilder(TagKey<DamageType> tagKey)
    {
        return super.getOrCreateRawBuilder(tagKey);
    }
}