package liedge.ltxindustries.client.renderer.entity;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.client.model.entity.SmallRocketModel;
import liedge.ltxindustries.entity.BaseRocketEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class RocketRenderer<T extends BaseRocketEntity> extends ProjectileRenderer<T>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "small_rocket");

    public RocketRenderer(EntityRendererProvider.Context context)
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
        return new SmallRocketModel(context.bakeLayer(LTXIModelLayers.SMALL_ROCKET));
    }
}