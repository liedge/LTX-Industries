package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.limatech.LimaTech;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTechTags.BlockEntities.GENERAL_PROCESSING_MACHINES;
import static liedge.limatech.LimaTechTags.BlockEntities.TURRETS;
import static liedge.limatech.registry.game.LimaTechBlockEntities.*;

class BlockEntityTagsGen extends LimaTagsProvider.RegistryTags<BlockEntityType<?>>
{
    BlockEntityTagsGen(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper helper)
    {
        super(packOutput, BuiltInRegistries.BLOCK_ENTITY_TYPE, LimaTech.MODID, lookupProvider, helper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        buildTag(GENERAL_PROCESSING_MACHINES).add(DIGITAL_FURNACE, GRINDER, RECOMPOSER, MATERIAL_FUSING_CHAMBER);
        buildTag(TURRETS).add(ROCKET_TURRET, RAILGUN_TURRET);
    }
}
