package liedge.ltxindustries.client.renderer;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.TextureTransform;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.util.Util;
import org.joml.Matrix4f;

public final class LTXIRenderTypes
{
    private LTXIRenderTypes() {}

    // Render types
    private static RenderType simpleType(String name, RenderSetup.RenderSetupBuilder builder)
    {
        return RenderType.create(LTXIndustries.RESOURCES.modid() + "_" + name, builder.createRenderSetup());
    }

    public static final RenderType ENERGY_FILL = simpleType("energy_fill", RenderSetup.builder(LTXIRenderPipelines.ENERGY_FILL)
            .sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE)
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET));

    public static final RenderType BUBBLE_SHIELD = simpleType("bubble_shield", RenderSetup.builder(LTXIRenderPipelines.BUBBLE_SHIELD)
            .sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE)
            .setOutputTarget(OutputTarget.WEATHER_TARGET));

    public static final RenderType WONDERLAND_EPHEMERA = simpleType("ephemera", RenderSetup.builder(LTXIRenderPipelines.WONDERLAND_EPHEMERA)
            .sortOnUpload()
            .setOutline(RenderSetup.OutlineProperty.NONE)
            .setOutputTarget(OutputTarget.WEATHER_TARGET));

    public static final RenderType LOCK_ON_INDICATOR = simpleType("lock_on_indicator", RenderSetup.builder(LTXIRenderPipelines.LOCK_ON_INDICATOR)
            .withTexture("Sampler0", LTXIndustries.RESOURCES.textureLocation("entity", "target_triangle"))
            .bufferSize(512)
            .setOutline(RenderSetup.OutlineProperty.NONE));

    public static final RenderType WIREFRAME = simpleType("wireframe", RenderSetup.builder(LTXIRenderPipelines.WIREFRAME)
            .withTexture("Sampler0", LTXIndustries.RESOURCES.textureLocation("entity", "wireframe"))
            .setTextureTransform(new TextureTransform("wireframe_texturing", LTXIRenderTypes::setupWireframeTexturing))
            //.useOverlay()
            .setOutputTarget(OutputTarget.ITEM_ENTITY_TARGET));

    @SuppressWarnings("deprecation")
    public static final RenderType ICE_PARTICLE = simpleType("ice_particle", RenderSetup.builder(RenderPipelines.TRANSLUCENT_PARTICLE)
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS)
            .useLightmap()
            .sortOnUpload());

    // Misc functions
    private static Matrix4f setupWireframeTexturing()
    {
        float delta = (Util.getMillis() % 7000L) / 7000f;
        return new Matrix4f().translation(-delta, delta, 0).scale(3);
    }
}