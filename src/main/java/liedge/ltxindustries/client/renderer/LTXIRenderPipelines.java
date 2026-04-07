package liedge.ltxindustries.client.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
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
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withDepthStencilState(DepthStencilState.DEFAULT)
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
            .withCull(false)
            .withDepthStencilState(new DepthStencilState(CompareOp.ALWAYS_PASS, true))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS)
            .build();

    public static final RenderPipeline WIREFRAME = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("pipeline/wireframe"))
            .withVertexShader("core/glint")
            .withFragmentShader("core/glint")
            .withSampler("Sampler0")
            .withCull(true)
            .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .build();

    public static final RenderPipeline GUI_TRIANGLES = RenderPipeline.builder(RenderPipelines.GUI_SNIPPET)
            .withLocation(LTXIndustries.RESOURCES.id("gui_triangles"))
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .build();
}