package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

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
        reverseTag(LIGHTFRAG, BYPASSES_COOLDOWN, AVOIDS_GUARDIAN_THORNS);
        reverseTag(MAGNUM_LIGHTFRAG, BYPASSES_COOLDOWN, BYPASSES_ARMOR, BYPASSES_ENCHANTMENTS, BYPASSES_EFFECTS, AVOIDS_GUARDIAN_THORNS);

        reverseTag(EXPLOSIVE_GRENADE, IS_EXPLOSION);
        reverseTag(FLAME_GRENADE, IS_FIRE, NO_KNOCKBACK);
        reverseTag(ROCKET_LAUNCHER, IS_EXPLOSION);
        tag(BYPASSES_COOLDOWN).add(EXPLOSIVE_GRENADE, FLAME_GRENADE, CRYO_GRENADE, ELECTRIC_GRENADE, ACID_GRENADE, NEURO_GRENADE, ROCKET_LAUNCHER);

        reverseTag(STICKY_FLAME, IS_FIRE, BYPASSES_COOLDOWN, NO_KNOCKBACK);
        reverseTag(TURRET_ROCKET, IS_EXPLOSION, BYPASSES_COOLDOWN);
        reverseTag(RAILGUN_TURRET, BYPASSES_COOLDOWN, BYPASSES_ARMOR, BYPASSES_ENCHANTMENTS, BYPASSES_EFFECTS);
    }
}