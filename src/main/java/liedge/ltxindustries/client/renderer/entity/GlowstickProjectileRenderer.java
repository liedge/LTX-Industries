package liedge.ltxindustries.client.renderer.entity;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.GlowstickProjectileModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.entity.GlowstickProjectile;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;

public class GlowstickProjectileRenderer extends ProjectileRenderer<GlowstickProjectile>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("block", "glowstick");

    public GlowstickProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void extractRenderState(GlowstickProjectile entity, ProjectileRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.lightCoords = LightCoordsUtil.FULL_BRIGHT;
    }

    @Override
    protected Identifier texture()
    {
        return TEXTURE;
    }

    @Override
    protected ProjectileModel createBaseModel(EntityRendererProvider.Context context)
    {
        return new GlowstickProjectileModel(context.bakeLayer(LTXIModelLayers.GLOWSTICK_PROJECTILE));
    }
}