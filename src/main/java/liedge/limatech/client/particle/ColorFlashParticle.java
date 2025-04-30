package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.util.Mth;

public class ColorFlashParticle extends TextureSheetParticle
{
    private final float flashSize;

    public ColorFlashParticle(ColorSizeParticleOptions options, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);
        LimaCoreClientUtil.setParticleColor(this, options.color());
        this.flashSize = options.size();
        this.lifetime = 4;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks)
    {
        setAlpha(0.75f - (age + partialTicks - 1f) * 0.11666f);
        super.render(buffer, renderInfo, partialTicks);
    }

    @Override
    public float getQuadSize(float partialTicks)
    {
        return flashSize * Mth.sin((age + partialTicks - 1f) * 0.25f * Mth.PI);
    }
}