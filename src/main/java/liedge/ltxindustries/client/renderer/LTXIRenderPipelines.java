package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.renderer.RenderPipelines;

public final class LTXIRenderPipelines
{
    private LTXIRenderPipelines() {}

    private static final RenderPipeline.Snippet TRANSLUCENT_ENERGY_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withBlend(BlendFunction.TRANSLUCENT)
            .buildSnippet();

    public static final RenderPipeline ENERGY_FILL = RenderPipeline.builder(TRANSLUCENT_ENERGY_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("pipeline/energy_fill"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS)
            .build();

    public static final RenderPipeline BUBBLE_SHIELD = RenderPipeline.builder(TRANSLUCENT_ENERGY_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("pipeline/bubble_shield"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .build();

    public static final RenderPipeline WONDERLAND_EPHEMERA = RenderPipeline.builder(TRANSLUCENT_ENERGY_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("pipeline/ephemera"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .withCull(false)
            .build();

    public static final RenderPipeline LOCK_ON_INDICATOR = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("pipeline/lock_on_indicator"))
            .withVertexShader("core/position_tex_color")
            .withFragmentShader("core/position_tex_color")
            .withSampler("Sampler0")
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withCull(false)
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .build();
}