package liedge.limatech.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.limatech.LimaTech;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

import static liedge.limacore.client.renderer.LimaCoreRenderTypes.POSITION_TEX_COLOR_SHADER;

public final class LimaTechRenderTypes
{
    private LimaTechRenderTypes() {}

    private static final RenderStateShard.TexturingStateShard FABRICATOR_WIREFRAME_TEXTURING = new RenderStateShard.TexturingStateShard("fabricator_preview_texturing", LimaTechRenderTypes::setupFabricatorWireframeTexturing, RenderSystem::resetTextureMatrix);

    private static RenderType positionColorTranslucent(VertexFormat.Mode mode)
    {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .createCompositeState(false);
        return RenderType.create("position_color_" + mode.name(), DefaultVertexFormat.POSITION_COLOR, mode, 1536, false, true, state);
    }

    public static final RenderType POSITION_COLOR_TRIANGLES = positionColorTranslucent(VertexFormat.Mode.TRIANGLES);
    public static final RenderType POSITION_COLOR_QUADS = positionColorTranslucent(VertexFormat.Mode.QUADS);

    public static final RenderType LOCK_ON_INDICATOR = RenderType.create("lock_on_indicator", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 512, false, false, RenderType.CompositeState.builder()
            .setShaderState(POSITION_TEX_COLOR_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "target_triangle"), false, false))
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static final RenderType FABRICATOR_WIREFRAME = RenderType.create("fabricator_wireframe", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, false, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "fabricator_wireframe"), false, false))
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTexturingState(FABRICATOR_WIREFRAME_TEXTURING)
            .createCompositeState(false));

    public static final RenderType GUI_TRIANGLE_STRIP = RenderType.create("position_color_triangle_strip", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_STRIP, 512, false, false, RenderType.CompositeState.builder()
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