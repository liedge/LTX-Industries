package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.BlockEntities.GENERAL_PROCESSING_MACHINES;
import static liedge.ltxindustries.LTXITags.BlockEntities.TURRETS;
import static liedge.ltxindustries.registry.game.LTXIBlockEntities.*;

class BlockEntityTagsGen extends LimaTagsProvider.RegistryTags<BlockEntityType<?>>
{
    BlockEntityTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.BLOCK_ENTITY_TYPE, LTXIndustries.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(GENERAL_PROCESSING_MACHINES).add(DIGITAL_FURNACE, DIGITAL_SMOKER, DIGITAL_BLAST_FURNACE, GRINDER, MATERIAL_FUSING_CHAMBER, ELECTROCENTRIFUGE, MIXER, VOLTAIC_INJECTOR, CHEM_LAB);
        buildTag(TURRETS).add(ROCKET_TURRET, RAILGUN_TURRET);
    }
}
