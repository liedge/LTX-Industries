package liedge.ltxindustries.client.renderer.entity;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.GlowstickProjectileModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.entity.GlowstickProjectileEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class GlowstickProjectileRenderer extends ProjectileRenderer<GlowstickProjectileEntity>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("block", "glowstick");

    public GlowstickProjectileRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    protected Identifier texture()
    {
        return TEXTURE;
    }

    @Override
    protected ProjectileModel createModel(EntityRendererProvider.Context context)
    {
        return new GlowstickProjectileModel(context.bakeLayer(LTXIModelLayers.GLOWSTICK_PROJECTILE));
    }
}