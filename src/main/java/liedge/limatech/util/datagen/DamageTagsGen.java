package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.registry.LimaTechDamageTypes.*;
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
        buildTag(AVOIDS_GUARDIAN_THORNS).add(LIGHTFRAG, MAGNUM_LIGHTFRAG, FLAME_GRENADE, FREEZE_GRENADE, ELECTRIC_GRENADE, ACID_GRENADE, STICKY_FLAME);

        reverseTag(LIGHTFRAG, BYPASSES_COOLDOWN);
        reverseTag(MAGNUM_LIGHTFRAG, BYPASSES_COOLDOWN, BYPASSES_ARMOR, BYPASSES_ENCHANTMENTS, BYPASSES_EFFECTS);

        reverseTag(EXPLOSIVE_GRENADE, IS_EXPLOSION);
        reverseTag(FLAME_GRENADE, IS_FIRE, BYPASSES_COOLDOWN);
        reverseTag(TURRET_ROCKET, IS_EXPLOSION);
        reverseTag(STICKY_FLAME, IS_FIRE, BYPASSES_COOLDOWN, NO_KNOCKBACK);
    }
}