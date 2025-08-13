package liedge.ltxindustries.client.model.baked;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import liedge.limacore.client.model.baked.BakedItemLayer;
import liedge.limacore.client.model.baked.LimaAbstractBakedModel;
import liedge.limacore.client.model.geometry.ElementGroupGeometry;
import liedge.limacore.client.model.geometry.LimaGeometryLoader;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class EmissiveBiLayerGeometry implements IUnbakedGeometry<EmissiveBiLayerGeometry>
{
    public static final ResourceLocation LOADER_ID = LTXIndustries.RESOURCES.location("bi_layer");
    public static final LimaGeometryLoader<EmissiveBiLayerGeometry> BI_LAYER_LOADER = new Loader();

    private EmissiveBiLayerGeometry() {}

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        TextureAtlasSprite baseSprite = spriteGetter.apply(context.getMaterial("layer0"));
        TextureAtlasSprite emissiveSprite = spriteGetter.apply(context.getMaterial("layer1"));

        List<BakedQuad> baseQuads = UnbakedGeometryHelper.bakeElements(
                UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite), $ -> baseSprite, modelState);
        List<BakedQuad> emissiveQuads = UnbakedGeometryHelper.bakeElements(
                UnbakedGeometryHelper.createUnbakedItemElements(1, emissiveSprite), $ -> emissiveSprite, modelState);
        QuadTransformers.settingEmissivity(15).processInPlace(emissiveQuads);

        return new Baked(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), baseSprite, context.getTransforms(), overrides, baseQuads, emissiveQuads);
    }

    private static class Baked extends LimaAbstractBakedModel
    {
        private final List<BakedModel> renderPasses;

        private Baked(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, ItemOverrides overrides, List<BakedQuad> baseQuads, List<BakedQuad> emissiveQuads)
        {
            super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, overrides, false);
            BakedItemLayer baseLayer = new BakedItemLayer(this, baseQuads, RenderTypeGroup.EMPTY);
            BakedItemLayer emissiveLayer = new BakedItemLayer(this, emissiveQuads, ElementGroupGeometry.customEmissiveRenderTypes());
            this.renderPasses = List.of(baseLayer, emissiveLayer);
        }

        @Override
        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
        {
            return renderPasses;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData extraData, @Nullable RenderType renderType)
        {
            return List.of();
        }
    }

    private static class Loader implements LimaGeometryLoader<EmissiveBiLayerGeometry>
    {
        @Override
        public ResourceLocation getLoaderId()
        {
            return LOADER_ID;
        }

        @Override
        public EmissiveBiLayerGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException
        {
            return new EmissiveBiLayerGeometry();
        }
    }
}