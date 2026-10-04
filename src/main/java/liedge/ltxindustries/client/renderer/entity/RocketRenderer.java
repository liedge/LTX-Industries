package liedge.ltxindustries.client.renderer.entity;

import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.client.model.entity.SmallRocketModel;
import liedge.ltxindustries.entity.BaseRocketEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

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
    public void extractRenderState(T entity, ProjectileRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.energyColor = entity.getColor();
    }

    @Override
    protected ProjectileModel createBaseModel(EntityRendererProvider.Context context)
    {
        return new SmallRocketModel(context.bakeLayer(LTXIModelLayers.SMALL_ROCKET_BASE), RenderTypes::entityCutout);
    }

    @Override
    protected @Nullable ProjectileModel createEmissiveModel(EntityRendererProvider.Context context)
    {
        return new SmallRocketModel(context.bakeLayer(LTXIModelLayers.SMALL_ROCKET_EMISSIVE), LimaCoreRenderTypes::entityCutoutEmissive);
    }
}