package liedge.limatech.client.model.baked;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.model.LimaBasicBakedModel;
import liedge.limacore.lib.LimaColor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DynamicModularBakedModel extends LimaBasicBakedModel
{
    private final RenderType nonEmissiveRenderType;
    private final RenderType emissiveRenderType;
    private final Map<String, SubModel> submodels;

    DynamicModularBakedModel(boolean ambientOcclusion, boolean gui3d, boolean useBlockLight, TextureAtlasSprite particleIcon, ItemTransforms transforms, RenderTypeGroup renderTypeGroup,
                             Map<String, DynamicModularGeometry.SubModelBuilder> submodelBuilders)
    {
        super(ambientOcclusion, gui3d, useBlockLight, particleIcon, transforms, ItemOverrides.EMPTY, true, renderTypeGroup);

        this.nonEmissiveRenderType = LimaCoreClientUtil.getItemRenderTypeOrDefault(renderTypeGroup);
        this.emissiveRenderType = NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get();
        Object2ObjectMap<String, SubModel> map = new Object2ObjectOpenHashMap<>();
        submodelBuilders.forEach((name, builder) -> map.put(name, builder.buildSubmodel(this)));
        this.submodels = Object2ObjectMaps.unmodifiable(map);
    }

    public SubModel getSubmodel(String name)
    {
        return Objects.requireNonNull(submodels.get(name), "Submodel '" + name + "' does not exist in parent model.");
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side)
    {
        return List.of();
    }

    @Override
    public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous)
    {
        return List.of();
    }

    public static class SubModel
    {
        private final DynamicModularBakedModel parent;
        private final List<BakedQuad> nonEmissiveQuads;
        private final List<BakedQuad> emissiveQuads;

        SubModel(DynamicModularBakedModel parent, List<BakedQuad> nonEmissiveQuads, List<BakedQuad> emissiveQuads)
        {
            this.parent = parent;
            this.nonEmissiveQuads = nonEmissiveQuads;
            this.emissiveQuads = emissiveQuads;
        }

        public void renderToBuffer(PoseStack poseStack, MultiBufferSource bufferSource, int light)
        {
            renderToBuffer(poseStack, bufferSource, light, LimaColor.WHITE, LimaColor.WHITE);
        }

        public void renderToBuffer(PoseStack poseStack, MultiBufferSource bufferSource, int light, LimaColor nonEmissiveTint, LimaColor emissiveTint)
        {
            PoseStack.Pose pose = poseStack.last();

            if (!nonEmissiveQuads.isEmpty())
            {
                VertexConsumer buffer = bufferSource.getBuffer(parent.nonEmissiveRenderType);
                for (BakedQuad quad : nonEmissiveQuads)
                {
                    buffer.putBulkData(pose, quad, nonEmissiveTint.red(), nonEmissiveTint.green(), nonEmissiveTint.blue(), 1f, light, OverlayTexture.NO_OVERLAY);
                }
            }

            if (!emissiveQuads.isEmpty())
            {
                VertexConsumer buffer = bufferSource.getBuffer(parent.emissiveRenderType);
                for (BakedQuad quad : emissiveQuads)
                {
                    buffer.putBulkData(pose, quad, emissiveTint.red(), emissiveTint.green(), emissiveTint.blue(), 1f, light, OverlayTexture.NO_OVERLAY);
                }
            }
        }
    }
}