package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.DamageTypes.BYPASS_SURVIVAL_DEFENSES;
import static liedge.limatech.LimaTechTags.DamageTypes.WEAPON_DAMAGE;
import static liedge.limatech.registry.bootstrap.LimaTechDamageTypes.*;
import static net.minecraft.tags.DamageTypeTags.*;

class DamageTagsGen extends LimaTagsProvider<DamageType>
{
    DamageTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper helper)
    {
        super(output, Registries.DAMAGE_TYPE, LimaTech.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookup)
    {
        // Mod tag definitions
        reverseTagToTags(WEAPON_DAMAGE, BYPASSES_COOLDOWN, AVOIDS_GUARDIAN_THORNS);
        reverseTagToTags(BYPASS_SURVIVAL_DEFENSES, BYPASSES_ARMOR, BYPASSES_WOLF_ARMOR, BYPASSES_RESISTANCE, BYPASSES_ENCHANTMENTS, BYPASSES_EFFECTS, BYPASSES_SHIELD);

        // Tag values
        buildTag(WEAPON_DAMAGE)
                .add(LIGHTFRAG, ROCKET_LAUNCHER)
                .add(EXPLOSIVE_GRENADE, FLAME_GRENADE, CRYO_GRENADE, ELECTRIC_GRENADE, ACID_GRENADE, NEURO_GRENADE)
                .add(STICKY_FLAME, TURRET_ROCKET, RAILGUN_TURRET);

        buildTag(BYPASS_SURVIVAL_DEFENSES).add(RAILGUN_TURRET);

        buildTag(IS_EXPLOSION).add(EXPLOSIVE_GRENADE, ROCKET_LAUNCHER, TURRET_ROCKET);
        buildTag(IS_FIRE).add(FLAME_GRENADE, STICKY_FLAME);
        buildTag(NO_KNOCKBACK).add(FLAME_GRENADE, STICKY_FLAME);
    }
}