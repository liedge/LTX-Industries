package liedge.limatech.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.entity.OrbGrenadeEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;

public class OrbGrenadeModel extends Model
{
    public static final ResourceLocation TEXTURE = LimaTech.RESOURCES.textureLocation("entity", "orb_grenade");
    private static final RenderType RENDER_TYPE = RenderType.entitySolid(TEXTURE);
    private static final RenderType EMISSIVE_RENDER_TYPE = NeoForgeRenderTypes.getUnlitTranslucent(TEXTURE);

    private final ModelPart body;
    private final ModelPart accents;
    private final ModelPart caps;

    public OrbGrenadeModel(EntityModelSet set)
    {
        super(RenderType::entitySolid);
        ModelPart root = set.bakeLayer(LimaTechModelLayers.ORB_GRENADE);
        body = root.getChild("body");
        accents = root.getChild("accents");
        caps = root.getChild("caps");

        NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get();
    }

    public void animateFromEntity(OrbGrenadeEntity entity, float partialTick)
    {
        float xRot = LimaMathUtil.toRad(entity.lerpSpinAnimation(partialTick));

        body.xRot = xRot;
        accents.xRot = xRot;
        caps.xRot = xRot;
    }

    public void renderToBuffer(PoseStack poseStack, MultiBufferSource bufferSource, int light, LimaColor color)
    {
        VertexConsumer buffer = bufferSource.getBuffer(RENDER_TYPE);

        body.render(poseStack, buffer, light, OverlayTexture.NO_OVERLAY);
        caps.render(poseStack, buffer, light, OverlayTexture.NO_OVERLAY);

        buffer = bufferSource.getBuffer(EMISSIVE_RENDER_TYPE);
        accents.render(poseStack, buffer, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, color.rgb());
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay, int rgb)
    {
        body.render(poseStack, buffer, light, overlay);
        accents.render(poseStack, buffer, LightTexture.FULL_BRIGHT, overlay, rgb);
        caps.render(poseStack, buffer, light, overlay);
    }

    public static LayerDefinition defineLayer()
    {
        MeshDefinition meshDef = new MeshDefinition();

        PartDefinition partDef = meshDef.getRoot();

        PartPose pivot = PartPose.offset(0f, 19f, 0f);

        partDef.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3f, -3f, -3f, 6f, 6f, 6f, CubeDeformation.NONE), pivot);

        partDef.addOrReplaceChild("caps", CubeListBuilder.create()
                .texOffs(24, 12).addBox(-1.0F, -1.0F, -5.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(24, 12).addBox(-1.0F, -1.0F, 4.0F, 2.0F, 2.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(16, 15).addBox(4.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 15).addBox(-5.0F, -1.0F, -1.0F, 1.0F, 2.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 12).addBox(-1.0F, -5.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE)
                .texOffs(16, 12).addBox(-1.0F, 4.0F, -1.0F, 2.0F, 1.0F, 2.0F, CubeDeformation.NONE), pivot);

        partDef.addOrReplaceChild("accents", CubeListBuilder.create().texOffs(0, 12).addBox(-2.0F, 3.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 17).addBox(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 17).addBox(-2.0F, -2.0F, 3.0F, 4.0F, 4.0F, 1.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(3.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 22).addBox(-4.0F, -2.0F, -2.0F, 1.0F, 4.0F, 4.0F, CubeDeformation.NONE)
                .texOffs(0, 12).addBox(-2.0F, -4.0F, -2.0F, 4.0F, 1.0F, 4.0F, CubeDeformation.NONE), pivot);

        return LayerDefinition.create(meshDef, 32, 32);
    }
}