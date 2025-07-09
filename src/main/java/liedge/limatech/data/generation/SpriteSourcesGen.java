package liedge.limatech.data.generation;

import liedge.limatech.client.gui.UpgradeIconSprites;
import liedge.limatech.client.gui.widget.LimaWidgetSprites;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static liedge.limatech.LimaTech.MODID;
import static liedge.limatech.LimaTech.RESOURCES;

class SpriteSourcesGen extends SpriteSourceProvider
{
    SpriteSourcesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper helper)
    {
        super(output, registries, MODID, helper);
    }

    @Override
    protected void gather()
    {
        SpriteSource machineModuleIcon = itemSheetCopy("machine_upgrade_module");

        atlas(LimaWidgetSprites.ATLAS_LOCATION)
                .addSource(nsDirSource("gui/widget"))
                .addSource(machineModuleIcon);
        atlas(UpgradeIconSprites.ATLAS_LOCATION)
                .addSource(nsDirSource("upgrade_module"))
                .addSource(machineModuleIcon);
    }

    private SpriteSource nsDirSource(String path)
    {
        return new NamespacedDirectoryLister(MODID, path, "");
    }

    private SpriteSource itemSheetCopy(String name)
    {
        ResourceLocation loc = RESOURCES.location(name);
        return new SingleFile(loc.withPrefix("item/"), Optional.of(loc));
    }
}