package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.DamageTypes.*;
import static liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes.*;
import static net.minecraft.tags.DamageTypeTags.*;

class DamageTagsGen extends LimaTagsProvider<DamageType>
{
    DamageTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(output, Registries.DAMAGE_TYPE, LTXIndustries.MODID, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        reverseTagToTags(BYPASS_SURVIVAL_DEFENSES, BYPASSES_ARMOR, BYPASSES_WOLF_ARMOR, BYPASSES_RESISTANCE, BYPASSES_ENCHANTMENTS, BYPASSES_EFFECTS, BYPASSES_SHIELD);

        // Tag values
        buildTag(WEAPONS_OR_GADGETS).add(LIGHTFRAG, EXPLOSIVE, FLAME, CRYO, ELECTRIC, ACID, GLOOM_GAS);
        buildTag(TURRETS).add(EXPLOSIVE_TURRET, ELECTRIC_TURRET, RAILGUN_TURRET);

        buildTag(BYPASSES_COOLDOWN).addTags(WEAPONS_OR_GADGETS, TURRETS);
        buildTag(AVOIDS_GUARDIAN_THORNS).addTags(WEAPONS_OR_GADGETS, TURRETS);

        buildTag(BYPASS_SURVIVAL_DEFENSES).add(RAILGUN_TURRET);

        buildTag(IS_EXPLOSION).add(EXPLOSIVE, EXPLOSIVE_TURRET);
        buildTag(IS_FIRE).add(FLAME);
        buildTag(IS_ELECTRIC).add(ELECTRIC, ELECTRIC_TURRET);
        buildTag(NO_KNOCKBACK).add(FLAME, CRYO, ELECTRIC, ACID, GLOOM_GAS, EXPLOSIVE_TURRET, ELECTRIC_TURRET);
    }
}