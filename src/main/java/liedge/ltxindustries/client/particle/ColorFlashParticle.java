package liedge.ltxindustries.client.particle;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.state.QuadParticleRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class ColorFlashParticle extends SingleQuadParticle
{
    private final float flashSize;

    private ColorFlashParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite, float flashSize)
    {
        super(level, x, y, z, sprite);
        this.flashSize = flashSize;
        this.lifetime = 4;
    }

    @Override
    protected Layer getLayer()
    {
        return Layer.TRANSLUCENT;
    }

    @Override
    protected int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public void extract(QuadParticleRenderState reusedState, Camera camera, float partialTick)
    {
        setAlpha(0.75f - (age + partialTick - 1f) * 0.11666f);
        super.extract(reusedState, camera, partialTick);
    }

    @Override
    public float getQuadSize(float partialTicks)
    {
        return flashSize * Mth.sin((age + partialTicks - 1f) * 0.25f * Mth.PI);
    }

    public static final class Provider implements ParticleProvider<ColorSizeParticleOptions>
    {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorSizeParticleOptions particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            TextureAtlasSprite sprite = sprites.get(random);
            ColorFlashParticle particle = new ColorFlashParticle(level, x, y, z, sprite, particleType.size());
            LimaCoreClientUtil.setQuadParticleColor(particle, particleType.color());

            return particle;
        }
    }
}