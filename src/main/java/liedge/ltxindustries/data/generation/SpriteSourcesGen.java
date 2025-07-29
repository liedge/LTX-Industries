package liedge.ltxindustries.data.generation;

import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.client.gui.UpgradeIconSprites;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SpriteSourceProvider;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXIndustries.MODID;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

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

        // Mod atlas definitions
        atlas(UpgradeIconSprites.ATLAS_LOCATION)
                .addSource(nsDirSource("upgrade_module"))
                .addSource(machineModuleIcon);

        // Vanilla atlas modifications
        atlas(ModResources.MC.location("gui"))
                .addSource(machineModuleIcon)
                .addSource(singleSprite("gui/light_panel", "slot/empty"));
        atlas(BLOCKS_ATLAS).addSource(singleSprite("misc/solid_lime", "solid_lime"));
    }

    private SpriteSource nsDirSource(String path)
    {
        return new NamespacedDirectoryLister(MODID, path, "");
    }

    private SpriteSource singleSprite(String path, String name)
    {
        return new SingleFile(RESOURCES.location(path), Optional.of(RESOURCES.location(name)));
    }

    private SpriteSource itemSheetCopy(String name)
    {
        return singleSprite("item/" + name, name);
    }
}