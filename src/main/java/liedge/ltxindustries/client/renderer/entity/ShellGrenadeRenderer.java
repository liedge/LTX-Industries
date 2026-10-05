package liedge.ltxindustries.client.renderer.entity;

import liedge.limacore.client.renderer.LimaCoreRenderTypes;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.*;
import liedge.ltxindustries.entity.ShellGrenade;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class ShellGrenadeRenderer extends ProjectileRenderer<ShellGrenade>
{
    private static final Identifier TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", LTXIIdentifiers.ID_SHELL_GRENADE);

    public ShellGrenadeRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void extractRenderState(ShellGrenade entity, ProjectileRenderState state, float partialTick)
    {
        super.extractRenderState(entity, state, partialTick);
        state.energyColor = entity.getGrenadeType().getColor();
    }

    @Override
    protected ProjectileModel createBaseModel(EntityRendererProvider.Context context)
    {
        return new ShellGrenadeModel(context.bakeLayer(LTXIModelLayers.SHELL_GRENADE_BASE), RenderTypes::entityCutout);
    }

    @Override
    protected @Nullable ProjectileModel createEmissiveModel(EntityRendererProvider.Context context)
    {
        return new ShellGrenadeModel(context.bakeLayer(LTXIModelLayers.SHELL_GRENADE_EMISSIVE), LimaCoreRenderTypes::entityCutoutEmissive);
    }

    @Override
    protected float yOffset()
    {
        return -1.125f;
    }

    @Override
    protected Identifier texture()
    {
        return TEXTURE;
    }
}