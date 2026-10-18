package liedge.ltxindustries.client.renderer.entity;

import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.entity.HomingBomblet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class HomingBombletRenderer extends BaseBombletRenderer<HomingBomblet>
{
    public HomingBombletRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void extractRenderState(HomingBomblet entity, ProjectileRenderState state, float partialTicks)
    {
        super.extractRenderState(entity, state, partialTicks);

        float spin = LimaCoreMath.toRad(entity.lerpSpin(partialTicks));
        state.xRot = spin;
        state.yRot = spin;
        state.color = entity.getColor();
    }
}