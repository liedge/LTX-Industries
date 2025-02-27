package liedge.limatech.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.limatech.LimaTech;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.util.function.Function;

public final class LimaTechRenderTypes
{
    private LimaTechRenderTypes() {}

    private static final RenderStateShard.ShaderStateShard POSITION_TEX_COLOR_SHADER = new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader);
    private static final RenderStateShard.TexturingStateShard FABRICATOR_WIREFRAME_TEXTURING = new RenderStateShard.TexturingStateShard("fabricator_preview_texturing", LimaTechRenderTypes::setupFabricatorWireframeTexturing, RenderSystem::resetTextureMatrix);

    private static final Function<ResourceLocation, RenderType> POSITION_TEX_COLOR = Util.memoize(texture -> {
        RenderType.CompositeState state = RenderType.CompositeState.builder()
                .setShaderState(POSITION_TEX_COLOR_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                .setCullState(RenderStateShard.NO_CULL)
                .createCompositeState(true);

        return RenderType.create("position_tex_color", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, state);
    });

    public static final RenderType BUBBLE_SHIELD = RenderType.create("bubble_shield", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES, 1536, false, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .setOutputState(RenderStateShard.TRANSLUCENT_TARGET)
            .createCompositeState(false));

    public static final RenderType LOCK_ON_INDICATOR = RenderType.create("lock_on_indicator", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 512, false, false, RenderType.CompositeState.builder()
            .setShaderState(POSITION_TEX_COLOR_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "target_triangle"), false, false))
            .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
            .setCullState(RenderStateShard.NO_CULL)
            .createCompositeState(false));

    public static final RenderType FABRICATOR_WIREFRAME = RenderType.create("fabricator_wireframe", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 1024, false, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
            .setTextureState(new RenderStateShard.TextureStateShard(LimaTech.RESOURCES.textureLocation("entity", "fabricator_wireframe"), false, false))
            .setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
            .setWriteMaskState(RenderStateShard.COLOR_WRITE)
            .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
            .setTexturingState(FABRICATOR_WIREFRAME_TEXTURING)
            .createCompositeState(false));

    public static final RenderType POSITION_COLOR_TRANSLUCENT = RenderType.create("position_color_translucent", DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 1536, true, true, RenderType.CompositeState.builder()
            .setShaderState(RenderStateShard.POSITION_COLOR_SHADER)
            .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
            .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
            .createCompositeState(true));

    public static RenderType positionTexColor(ResourceLocation texture)
    {
        return POSITION_TEX_COLOR.apply(texture);
    }

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