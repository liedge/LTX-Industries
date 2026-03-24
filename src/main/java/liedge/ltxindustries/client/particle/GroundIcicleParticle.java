package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.client.particle.CustomGeometryParticle;
import liedge.limacore.client.particle.CustomGeometryParticleEntry;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.client.renderer.LTXIRenderTypes;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class GroundIcicleParticle extends CustomGeometryParticle
{
    private static final Direction[] NOT_DOWN = new Direction[] {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private final TextureAtlasSprite sprite;
    private final float baseSize;

    private int prevAge;
    private int light;

    private GroundIcicleParticle(ClientLevel level, double x, double y, double z, TextureAtlasSprite sprite)
    {
        super(level, x, y, z);
        this.sprite = sprite;
        this.baseSize = 0.4f + (random.nextFloat() * 0.6f);

        this.lifetime = random.nextIntBetweenInclusive(24, 30);
        this.hasPhysics = false;
        setSize(0.5f, 0.5f);
    }

    @Override
    public void tick()
    {
        prevAge = age;
        light = getLightColor(1f);

        super.tick();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected int getLightColor(float partialTick)
    {
        BlockPos pos = BlockPos.containing(x, y, z);
        if (level.hasChunkAt(pos))
        {
            BlockState state = level.getBlockState(pos);
            if (state.emissiveRendering(level, pos))
            {
                return LightTexture.FULL_BRIGHT;
            }
            else
            {
                int blockLight = Math.max(level.getBrightness(LightLayer.BLOCK, pos), state.getLightEmission(level, pos));
                int skyLight = Math.max(8, level.getBrightness(LightLayer.SKY, pos));
                return LightTexture.pack(blockLight, skyLight);
            }
        }
        else
        {
            return LightTexture.pack(0, 8);
        }
    }

    @Override
    public @Nullable CustomGeometryParticleEntry extractEntry(float x, float y, float z, Camera camera, float partialTick)
    {
        float ageLerp = LimaCoreMath.divideFloatLerp(partialTick, prevAge, age, lifetime);
        float sizeFade = ageLerp <= 0.2f ? ageLerp / 0.2f : 1 - (ageLerp / 0.8f - 0.25f) * 0.3333f;

        // Order: Side UV0, UV1 ; Top UV0, UV1
        float[] uv = new float[8];
        uv[0] = sprite.getU(0.5f);
        uv[1] = sprite.getV(0.5f);
        uv[2] = sprite.getU(0.625f);
        uv[3] = sprite.getV(0.625f);
        uv[4] = sprite.getU(0.5f);
        uv[5] = sprite.getV(1f - 0.75f);
        uv[6] = sprite.getU(0.5625f);
        uv[7] = sprite.getV(1f - 0.4375f);

        return new Entry(x, y, z, baseSize * sizeFade, uv, light);
    }

    private record Entry(float x, float y, float z, float scale, float[] uv, int light) implements CustomGeometryParticleEntry
    {
        @Override
        public RenderType renderType()
        {
            return LTXIRenderTypes.ICE_PARTICLE;
        }

        @Override
        public void render(PoseStack.Pose pose, VertexConsumer buffer)
        {
            pose.scale(scale, scale, scale);

            // Lower half
            submitFigure(pose, buffer, Direction.values(), -0.0625f, 0f, -0.0625f, 0.0625f, 0.4375f, 0.0625f);
            submitFigure(pose, buffer, NOT_DOWN, -0.03125f, 0.4375f, -0.03125f, 0.03125f, 0.75f, 0.03125f);
        }

        private void submitFigure(PoseStack.Pose pose, VertexConsumer buffer, Direction[] faces, float x1, float y1, float z1, float x2, float y2, float z2)
        {
            for (Direction side : faces)
            {
                int uvi = side.getAxis() == Direction.Axis.Y ? 4 : 0;

                LTXIRenderer.submitParticleFormatQuad(
                        pose, buffer, side,
                        x1, y1, z1,
                        x2, y2, z2,
                        uv[uvi], uv[uvi + 1], uv[uvi + 2], uv[uvi + 3],
                        1f, 1f, 1f, 0.85f, light);
            }
        }
    }

    public static final class Provider implements ParticleProvider<SimpleParticleType>
    {
        private static final Identifier ICE_SPRITE = ModResources.MC.id("block/ice");

        public Provider() { }

        @Override
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, RandomSource random)
        {
            TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).getSprite(ICE_SPRITE);
            return new GroundIcicleParticle(level, x, y, z, sprite);
        }
    }
}