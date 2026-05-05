package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.limacore.client.model.StaticQuads;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.UVPair;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Set;

public record WireframeNodeCollector(SubmitNodeCollector parent) implements SubmitNodeCollector
{
    private static final Set<VertexFormat> VALID_FORMATS = Set.of(DefaultVertexFormat.BLOCK, DefaultVertexFormat.ENTITY);
    private static final float UV_SCALE = 0.09375f;

    @Override
    public OrderedSubmitNodeCollector order(int order)
    {
        return parent.order(order);
    }

    @Override
    public <S> void submitModel(Model<? super S> model, S state, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay)
    {
        renderType = swapRenderType(renderType);
        if (renderType != null)
        {
            parent.submitModel(model, state, poseStack, renderType, lightCoords, overlayCoords, tintedColor, sprite, outlineColor, crumblingOverlay);
        }
    }

    @Override
    public void submitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int lightCoords, int overlayCoords, @Nullable TextureAtlasSprite sprite, boolean sheeted, boolean hasFoil, int tintedColor, ModelFeatureRenderer.@Nullable CrumblingOverlay crumblingOverlay, int outlineColor)
    {
        renderType = swapRenderType(renderType);
        if (renderType != null) parent.submitModelPart(modelPart, poseStack, renderType, lightCoords, overlayCoords, sprite);
    }

    @Override
    public void submitItem(PoseStack poseStack, ItemDisplayContext displayContext, int lightCoords, int overlayCoords, int outlineColor, int[] tintLayers, List<BakedQuad> quads, ItemStackRenderState.FoilType foilType)
    {
        submitWireframeInternal(poseStack, quads);
    }

    public void submitWireframePiece(PoseStack poseStack, StaticQuads staticQuads)
    {
        for (StaticQuads.Layer layer : staticQuads.getLayers())
        {
            RenderType renderType = swapRenderType(layer.renderType());
            if (renderType != null) submitWireframeInternal(poseStack, layer.quads());
        }
    }

    private void submitWireframeInternal(PoseStack poseStack, List<BakedQuad> quads)
    {
        parent.submitCustomGeometry(poseStack, LTXIRenderTypes.WIREFRAME, (pose, buffer) ->
        {
            for (BakedQuad quad : quads)
            {
                putScaledUVQuad(pose, buffer, quad);
            }
        });
    }

    @Override
    public void submitCustomGeometry(PoseStack poseStack, RenderType renderType, CustomGeometryRenderer customGeometryRenderer)
    {
        renderType = swapRenderType(renderType);
        if (renderType != null) parent.submitCustomGeometry(poseStack, renderType, customGeometryRenderer);
    }

    @Override
    public void submitShadow(PoseStack poseStack, float radius, List<EntityRenderState.ShadowPiece> pieces) { }

    @Override
    public void submitNameTag(PoseStack poseStack, @Nullable Vec3 nameTagAttachment, int offset, Component name, boolean seeThrough, int lightCoords, double distanceToCameraSq, CameraRenderState camera) { }

    @Override
    public void submitText(PoseStack poseStack, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor, int outlineColor) { }

    @Override
    public void submitFlame(PoseStack poseStack, EntityRenderState renderState, Quaternionf rotation) { }

    @Override
    public void submitLeash(PoseStack poseStack, EntityRenderState.LeashState leashState) { }

    @Override
    public void submitMovingBlock(PoseStack poseStack, MovingBlockRenderState movingBlockRenderState) { }

    @Override
    public void submitBlockModel(PoseStack poseStack, RenderType renderType, List<BlockStateModelPart> parts, int[] tintLayers, int lightCoords, int overlayCoords, int outlineColor) { }

    @Override
    public void submitBreakingBlockModel(PoseStack poseStack, BlockStateModel model, long seed, int progress) { }

    @Override
    public void submitParticleGroup(ParticleGroupRenderer particleGroupRenderer) { }

    private void putScaledUVQuad(PoseStack.Pose pose, VertexConsumer buffer, BakedQuad quad)
    {
        BakedQuad.MaterialInfo material = quad.materialInfo();
        TextureAtlasSprite sprite = material.sprite();
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float uWidth = sprite.getU1() - u0;
        float vHeight = sprite.getV1() - v0;

        for (int vertex = 0; vertex < 4; vertex++)
        {
            long packedUV = quad.packedUV(vertex);
            float u = (UVPair.unpackU(packedUV) - u0) / uWidth;
            float v = (UVPair.unpackV(packedUV) - v0) / vHeight;

            buffer.addVertex(pose, quad.position(vertex)).setUv(u * UV_SCALE, v * UV_SCALE);
        }
    }

    private @Nullable RenderType swapRenderType(RenderType renderType)
    {
        if (renderType.mode() == VertexFormat.Mode.QUADS && VALID_FORMATS.contains(renderType.format()))
        {
            return LTXIRenderTypes.WIREFRAME;
        }

        return null;
    }
}