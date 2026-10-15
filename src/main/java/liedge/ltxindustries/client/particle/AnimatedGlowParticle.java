package liedge.ltxindustries.client.particle;

import liedge.limacore.client.particle.ColorParticleOptions;
import liedge.limacore.client.particle.ColorSizeParticleOptions;
import liedge.limacore.client.util.LimaCoreClientUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.util.RandomSource;

public class AnimatedGlowParticle extends SimpleAnimatedParticle
{
    private AnimatedGlowParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, float sizeMultiplier)
    {
        super(level, x, y, z, sprites, 0f);
        this.quadSize *= sizeMultiplier;
        setSpriteFromAge(sprites);
    }

    @Override
    public int getLightCoords(float a)
    {
        return LightCoordsUtil.FULL_BRIGHT;
    }

    private static class SparkParticle extends AnimatedGlowParticle
    {
        private boolean twinkle;
        private boolean visible = true;

        private SparkParticle(ClientLevel level, SpriteSet sprites, double x, double y, double z, float sizeMultiplier)
        {
            super(level, sprites, x, y, z, sizeMultiplier);

            this.lifetime = random.nextIntBetweenInclusive(15, 30);
            this.friction = 0.85f;
        }

        @Override
        public void tick()
        {
            super.tick();

            this.visible = !twinkle || age < lifetime / 3 || (age + lifetime) / 3 % 2 == 0;
        }

        @Override
        public void extract(QuadParticleRenderState particleTypeRenderState, Camera camera, float partialTickTime)
        {
            if (visible) super.extract(particleTypeRenderState, camera, partialTickTime);
        }
    }

    public static final class ColorGlitterProvider implements ParticleProvider<ColorSizeParticleOptions>
    {
        private final SpriteSet sprites;

        public ColorGlitterProvider(SpriteSet sprites)
        {
            this.sprites = sprites;
        }

        @Override
        public Particle createParticle(ColorSizeParticleOptions data, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            AnimatedGlowParticle particle = new AnimatedGlowParticle(level, sprites, x, y, z, data.size());
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            particle.lifetime = 20;
            LimaCoreClientUtil.setQuadParticleColor(particle, data.color());

            return particle;
        }
    }

    public static final class ColorSparkProvider implements ParticleProvider<ColorParticleOptions>
    {
        private final SpriteSet sprites;
        private final boolean twinkle;

        public ColorSparkProvider(SpriteSet sprites, boolean twinkle)
        {
            this.sprites = sprites;
            this.twinkle = twinkle;
        }

        @Override
        public Particle createParticle(ColorParticleOptions options, ClientLevel level, double x, double y, double z, double xAux, double yAux, double zAux, RandomSource random)
        {
            SparkParticle particle = new SparkParticle(level, sprites, x, y, z, 0.825f);

            particle.setParticleSpeed(xAux, yAux, zAux);
            particle.twinkle = this.twinkle;
            LimaCoreClientUtil.setQuadParticleColor(particle, options.color());

            return particle;
        }
    }
}