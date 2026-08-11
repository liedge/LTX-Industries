package liedge.ltxindustries.data.generation;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.GrayscaleSprite;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.client.LTXIAtlasIds;
import liedge.ltxindustries.lib.BuiltInOres;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.AtlasIds;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.data.SpriteSourceProvider;
import net.neoforged.neoforge.client.textures.NamespacedDirectoryLister;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
        atlas(LTXIAtlasIds.MODULAR_ICONS_ID)
                .addSource(nsDirSource("modular_icon"));

        // Vanilla atlas modifications
        atlas(AtlasIds.GUI)
                .addSource(itemSheetCopy("upgrade_module"))
                .addSource(singleSprite("gui/light_panel", "slot/empty"));
        atlas(AtlasIds.BLOCKS)
                .addSource(singleSprite("core/solid_lime", "block/solid_lime"))
                .addSource(grayscaleMC("block/light_molten_still", "block/lava_still", 1.75f, 1))
                .addSource(grayscaleMC("block/light_molten_flow", "block/lava_flow", 1.75f, 1))
                .addSource(grayscaleMC("block/bright_water_still", "block/water_still", 1.15f, 1.25f))
                .addSource(grayscaleMC("block/bright_water_flow", "block/water_flow", 1.15f, 1.25f));
        atlas(AtlasIds.ITEMS)
                .addSource(singleSprite("core/solid_lime", "item/solid_lime"))
                .addSource(singleSprite("block/glacia_glass", "item/glacia_glass"))
                .addSource(singleSprite("block/glowstick", "item/glowstick"))
                .addSource(orePermutations());

        SourceList particles = atlas(AtlasIds.PARTICLES);
        for (int i = 0; i < 16; i++)
        {
            String name = "sonic_boom_" + i;
            particles.addSource(grayscaleMC(name, "particle/" + name, 1.375f, 1));
        }
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

    private SpriteSource grayscaleMC(String name, String sourcePath, float brightness, float alpha)
    {
        return new GrayscaleSprite(RESOURCES.id(name), ModResources.MC.id(sourcePath), brightness, alpha);
    }

    private SpriteSource orePermutations()
    {
        List<Identifier> textures = Stream.of("item/crushed_ore", "item/washed_ore", "item/ore_chunk", "item/ore_solution", "item/ore_crystal").map(RESOURCES::id).toList();
        Identifier paletteKey = RESOURCES.id("palette/ore_key");
        Map<String, Identifier> permutations = new Object2ObjectOpenHashMap<>();

        for (BuiltInOres ore : BuiltInOres.values())
        {
            permutations.put(ore.getSerializedName(), RESOURCES.id("palette/ore/" + ore.getSerializedName()));
        }

        return new PalettedPermutations(textures, paletteKey, permutations);
    }
}