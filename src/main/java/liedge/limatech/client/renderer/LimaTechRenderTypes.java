package liedge.limatech.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.limatech.LimaTech;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

import java.util.OptionalDouble;

public final class LimaTechRenderTypes
{
    private LimaTechRenderTypes() {}

    public static final RenderType BUBBLE_SHIELD = newRenderType("bubble_shield", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
            .createCompositeState(false));

    public static final RenderType TARGET_TRIANGLE = newRenderType("target_triangle", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, false, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "target_triangle"), false, false))
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static final RenderType NO_DEPTH_LINES = newRenderType("no_depth_lines", DefaultVertexFormat.POSITION_COLOR_NORMAL, VertexFormat.Mode.LINES, false, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_LINES_SHADER)
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setOutputState(RenderStateShard.OUTLINE_TARGET)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static final RenderType FABRICATOR_ITEM_PREVIEW = newRenderType("fabricator_item_preview", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "lime_glint"), false, false))
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setCullState(RenderStateShard.NO_CULL)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
            .createCompositeState(false));

    public static final RenderType POSITION_COLOR_TRANSLUCENT = newRenderType("position_color_translucent", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(false));

    private static RenderType newRenderType(String name, VertexFormat format, VertexFormat.Mode mode, boolean sortOnUpload, RenderType.CompositeState state)
    {
        return RenderType.create(LimaTech.RESOURCES.location(name).toString(), format, mode, 256, false, sortOnUpload, state);
    }
}