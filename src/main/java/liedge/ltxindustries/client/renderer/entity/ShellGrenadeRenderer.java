package liedge.ltxindustries.client.renderer.entity;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.ProjectileModel;
import liedge.ltxindustries.client.model.entity.ShellGrenadeModel;
import liedge.ltxindustries.entity.ShellGrenadeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.Identifier;

public class ShellGrenadeRenderer extends ProjectileRenderer<ShellGrenadeEntity>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "shell_grenade");

    public ShellGrenadeRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void extractRenderState(ShellGrenadeEntity entity, ProjectileRenderState reusedState, float partialTick)
    {
        super.extractRenderState(entity, reusedState, partialTick);
        reusedState.color = entity.getGrenadeType().getColor();
    }

    @Override
    protected ProjectileModel createModel(EntityRendererProvider.Context context)
    {
        return new ShellGrenadeModel(context.bakeLayer(LTXIModelLayers.SHELL_GRENADE));
    }

    @Override
    protected Identifier texture()
    {
        return TEXTURE;
    }
}