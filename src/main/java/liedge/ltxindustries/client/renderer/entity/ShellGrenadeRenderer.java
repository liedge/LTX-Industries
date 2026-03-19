package liedge.ltxindustries.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.model.entity.ShellGrenadeModel;
import liedge.ltxindustries.entity.ShellGrenadeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class ShellGrenadeRenderer extends EntityRenderer<ShellGrenadeEntity>
{
    private static final ResourceLocation TEXTURE = LTXIndustries.RESOURCES.textureLocation("entity", "shell_grenade");

    private final ShellGrenadeModel model;

    public ShellGrenadeRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new ShellGrenadeModel(context.getModelSet());
    }

    @Override
    public void render(ShellGrenadeEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        model.prepare(entity, partialTick);
        model.render(poseStack, bufferSource, TEXTURE, packedLight, entity.getGrenadeType().getColor().argb32());
    }

    @Override
    public ResourceLocation getTextureLocation(ShellGrenadeEntity shellGrenadeEntity)
    {
        return TEXTURE;
    }
}