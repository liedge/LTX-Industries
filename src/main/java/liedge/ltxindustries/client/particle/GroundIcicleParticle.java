package liedge.ltxindustries.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.client.LTXIRenderUtil;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class GroundIcicleParticle extends Particle
{
    private static final ResourceLocation ICE_SPRITE_LOCATION = ModResources.MC.location("block/ice");
    private static final Direction[] NOT_DOWN = new Direction[] {Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};

    private final TextureAtlasSprite sprite;
    private final float baseSize;

    private int light;
    private int prevAge;

    public GroundIcicleParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z)
    {
        super(level, x, y, z);
        this.sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ICE_SPRITE_LOCATION);
        this.baseSize = 0.4f + (random.nextFloat() * 0.6f);
        this.lifetime = random.nextIntBetweenInclusive(24, 30);
        this.hasPhysics = false;
        setSize(0.5f, 0.5f);

        light = getLightColor(0f);
    }

    @Override
    public void tick()
    {
        prevAge = age;

        super.tick();

        light = getLightColor(0f);
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
    public void render(VertexConsumer buffer, Camera camera, float partialTick)
    {
        Vec3 camPos = camera.getPosition();
        float px = (float) (Mth.lerp(partialTick, xo, x) - camPos.x);
        float py = (float) (Mth.lerp(partialTick, yo, y) - camPos.y);
        float pz = (float) (Mth.lerp(partialTick, zo, z) - camPos.z);

        Matrix4f mx4 = new Matrix4f();
        mx4.translate(px, py, pz);
        mx4.scale(baseSize);

        float ageLerp = LimaCoreMath.divideFloatLerp(partialTick, prevAge, age, lifetime);
        float sizeFade = (ageLerp <= 0.2f) ? ageLerp / 0.2f : 1 - ((ageLerp / 0.8f) - 0.25f) * 0.333f;

        float lowerXZ1 = -0.0625f * sizeFade;
        float lowerXZ2 = 0.0625f * sizeFade;
        float lowerY2 = 0.4375f * sizeFade;
        submitIcicleCuboid(LTXIRenderUtil.ALL_SIDES, buffer, mx4, lowerXZ1, 0, lowerXZ1, lowerXZ2, lowerY2, lowerXZ2,
                light, sprite.getU(0.5f), sprite.getV(0.5f), sprite.getU(0.625f), sprite.getV(0.625f),
                sprite.getU(0.5f), sprite.getV(1f - 0.4375f), sprite.getU(0.625f), sprite.getV1());

        float upperXZ1 = -0.03125f * sizeFade;
        float upperXZ2 = 0.03125f * sizeFade;
        float upperY1 = 0.4375f * sizeFade;
        float upperY2 = 0.75f * sizeFade;
        submitIcicleCuboid(NOT_DOWN, buffer, mx4, upperXZ1, upperY1, upperXZ1, upperXZ2, upperY2, upperXZ2,
                light, sprite.getU(0.5f), sprite.getV(0.5f), sprite.getU(0.5625f), sprite.getV(0.5625f),
                sprite.getU(0.5f), sprite.getV(1f - 0.75f), sprite.getU(0.5625f), sprite.getV(1f - 0.4375f));
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    private void submitIcicleCuboid(Direction[] faces, VertexConsumer buffer, Matrix4f mx4, float x1, float y1, float z1, float x2, float y2, float z2, int packedLight,
                                    float verticalU0, float verticalV0, float verticalU1, float verticalV1,
                                    float horizontalU0, float horizontalV0, float horizontalU1, float horizontalV1)
    {
        for (Direction side : faces)
        {
            if (side.getAxis() == Direction.Axis.Y)
            {
                LTXIRenderUtil.submitTexturedCuboidFace(side, buffer, mx4, x1, y1, z1, x2, y2, z2, verticalU0, verticalV0, verticalU1, verticalV1, 1f, 1f, 1f, 0.85f, packedLight);
            }
            else
            {
                LTXIRenderUtil.submitTexturedCuboidFace(side, buffer, mx4, x1, y1, z1, x2, y2, z2, horizontalU0, horizontalV0, horizontalU1, horizontalV1, 1f, 1f, 1f, 0.85f, packedLight);
            }
        }
    }
}