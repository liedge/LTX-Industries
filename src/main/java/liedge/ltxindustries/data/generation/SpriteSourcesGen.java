package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.client.LTXIAtlasIds;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static liedge.ltxindustries.LTXIndustries.MODID;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;

class SpriteSourcesGen extends SpriteSourceProvider
{
    SpriteSourcesGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, registries, MODID);
    }

    @Override
    protected void gather()
    {
        // Mod atlas definitions
        atlas(LTXIAtlasIds.UPGRADE_ICONS_ID)
                .addSource(nsDirSource("upgrade_module"))
                .addSource(itemSheetCopy("titanium_gear"))
                .addSource(itemSheetCopy("slatesteel_gear"));

        // Vanilla atlas modifications
        atlas(AtlasIds.GUI)
                .addSource(itemSheetCopy("upgrade_module"))
                .addSource(singleSprite("gui/light_panel", "slot/empty"));
        atlas(AtlasIds.BLOCKS)
                .addSource(singleSprite("core/solid_lime", "block/solid_lime"))
                .addSource(singleSprite("core/lime_fluid", "block/lime_fluid"))
                .addSource(singleSprite("core/flowing_lime_fluid", "block/flowing_lime_fluid"));
        atlas(AtlasIds.ITEMS)
                .addSource(singleSprite("core/solid_lime", "item/solid_lime"))
                .addSource(singleSprite("block/glacia_glass", "item/glacia_glass"));
    }

    private SpriteSource nsDirSource(String path)
    {
        return new NamespacedDirectoryLister(MODID, path, "");
    }

    private SpriteSource singleSprite(String path, String name)
    {
        return new SingleFile(RESOURCES.id(path), Optional.of(RESOURCES.id(name)));
    }

    private SpriteSource itemSheetCopy(String name)
    {
        return singleSprite("item/" + name, name);
    }
}