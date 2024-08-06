package liedge.limatech.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.ModResources;
import liedge.limatech.entity.StickyFlameEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import org.joml.Matrix4f;

public class StickyFlameRenderer extends EntityRenderer<StickyFlameEntity>
{
    private static final ResourceLocation SPRITE_TEXTURE = ModResources.MC.location("block/campfire_fire");

    private final TextureAtlasSprite sprite;

    public StickyFlameRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SPRITE_TEXTURE);
    }

    @Override
    public void render(StickyFlameEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight)
    {
        VertexConsumer buffer = bufferSource.getBuffer(NeoForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get());

        Matrix4f mx4 = poseStack.last().pose();

        float size = entity.tickCount < 4 ? (entity.tickCount + partialTick) / 4f : 1;
        float xz = (entity.getBbWidth() / 2f) * size;
        float y = entity.getBbHeight() * size;

        buffer.addVertex(mx4, xz, 0, xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU1(), sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, xz, y, xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU1(), sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, -xz, y, -xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU0(), sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, -xz, 0, -xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU0(), sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);

        buffer.addVertex(mx4, xz, 0, -xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU1(), sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, xz, y, -xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU1(), sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, -xz, y, xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU0(), sprite.getV0()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
        buffer.addVertex(mx4, -xz, 0, xz).setColor(1f, 1f, 1f, 1).setUv(sprite.getU0(), sprite.getV1()).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(1, 0, 0);
    }

    @Override
    public ResourceLocation getTextureLocation(StickyFlameEntity entity)
    {
        return InventoryMenu.BLOCK_ATLAS;
    }
}