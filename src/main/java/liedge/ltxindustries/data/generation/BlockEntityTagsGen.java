package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXITags.BlockEntities.*;
import static liedge.ltxindustries.registry.game.LTXIBlockEntities.*;

class BlockEntityTagsGen extends LimaTagsProvider.RegistryTags<BlockEntityType<?>>
{
    BlockEntityTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(packOutput, BuiltInRegistries.BLOCK_ENTITY_TYPE, LTXIndustries.MODID, lookupProvider);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(STANDARD_UPGRADABLE_MACHINES).add(
                DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                MATERIAL_PRESS,
                ARC_FURNACE,
                HYDROSIEVE,
                ELECTROCENTRIFUGE,
                MIXER,
                VOLTAIC_INJECTOR,
                CHEM_LAB,
                ASSEMBLER,
                GEO_SYNTHESIZER,
                ATMOSPHERIC_SCRUBBER,
                DIGITAL_GARDEN,
                REPAIR_STATION);

        buildTag(STANDARD_PARALLEL_UPGRADABLE).add(
                DIGITAL_FURNACE,
                DIGITAL_SMOKER,
                DIGITAL_BLAST_FURNACE,
                GRINDER,
                ARC_FURNACE,
                HYDROSIEVE,
                ELECTROCENTRIFUGE,
                MIXER,
                VOLTAIC_INJECTOR,
                CHEM_LAB,
                GEO_SYNTHESIZER);

        buildTag(TURRETS).add(ARC_TURRET, ROCKET_TURRET, RAILGUN_TURRET);
    }
}
