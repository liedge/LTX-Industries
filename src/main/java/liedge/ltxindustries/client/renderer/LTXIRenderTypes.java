package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import static liedge.limacore.client.renderer.LimaCoreRenderTypes.POSITION_TEX_COLOR_SHADER;

public final class LTXIRenderTypes
{
    private LTXIRenderTypes() {}

    private static final RenderStateShard.TexturingStateShard FABRICATOR_WIREFRAME_TEXTURING = new RenderStateShard.TexturingStateShard("fabricator_preview_texturing", LTXIRenderTypes::setupFabricatorWireframeTexturing, RenderSystem::resetTextureMatrix);

    public static final RenderType ENERGY_FILL = RenderType.create(
            "energy_fill",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            1536,
            false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(false));

    public static final RenderType BUBBLE_SHIELD = RenderType.create(
            "bubble_shield",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            1536,
            false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .createCompositeState(false));

    public static final RenderType WONDERLAND_EPHEMERA = RenderType.create(
            "wonderland_ephemera",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            1536,
            false, true,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setCullState(RenderStateShard.NO_CULL)
                    .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
                    .createCompositeState(false));

    public static final RenderType LOCK_ON_INDICATOR = RenderType.create("lock_on_indicator", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 512, false, false, RenderType.CompositeState.builder()
            .setShaderState(POSITION_TEX_COLOR_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LTXIndustries.RESOURCES.textureLocation("entity", "target_triangle"), false, false))
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static final RenderType FABRICATOR_WIREFRAME = RenderType.create("fabricator_wireframe", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LTXIndustries.RESOURCES.textureLocation("entity", "fabricator_wireframe"), false, false))
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTexturingState(FABRICATOR_WIREFRAME_TEXTURING)
            .createCompositeState(false));

    public static final RenderType GUI_TRIANGLES = RenderType.create(
            "gui_triangles",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            512,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_GUI_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false));

    // State shard helpers
    private static void setupFabricatorWireframeTexturing()
    {
        long time = (long) ((double) Util.getMillis() * 4.0d);

        float x = (time % 144000L) / 144000f;
        float y = (time % 144000L) / 144000f;

        Matrix4f mx4 = new Matrix4f().translation(-x, y, 0).scale(8f);
        RenderSystem.setTextureMatrix(mx4);
    }
}