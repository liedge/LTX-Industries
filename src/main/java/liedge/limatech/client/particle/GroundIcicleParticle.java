package liedge.limatech.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import liedge.limacore.lib.ModResources;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.client.LimaTechClient;
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

    public GroundIcicleParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
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

        float ageLerp = LimaMathUtil.divideFloatLerp(partialTick, prevAge, age, lifetime);
        float sizeFade = (ageLerp <= 0.2f) ? ageLerp / 0.2f : 1 - ((ageLerp / 0.8f) - 0.25f) * 0.333f;

        float xz11 = -0.0625f * sizeFade;
        float xz21 = 0.0625f * sizeFade;
        float y21 = 0.4375f * sizeFade;

        float xz12 = -0.03125f * sizeFade;
        float xz22 = 0.03125f * sizeFade;
        float y12 = 0.4375f * sizeFade;
        float y22 = 0.75f * sizeFade;

        renderBlockFormatCuboid(buffer, mx4, xz11, 0, xz11, xz21, y21, xz21,
                sprite.getU(0.5f), sprite.getU(0.625f), sprite.getV(0.5f), sprite.getV(0.625f), sprite.getU(0.5f), sprite.getU(0.625f), sprite.getV(1f - 0.4375f), sprite.getV(1f),
                light, 1f, 1f, 1f, 0.85f, LimaTechClient.ALL_SIDES);

        renderBlockFormatCuboid(buffer, mx4, xz12, y12, xz12, xz22, y22, xz22,
                sprite.getU(0.5f), sprite.getU(0.5625f), sprite.getV(0.5f), sprite.getV(0.5625f), sprite.getU(0.5f), sprite.getU(0.5625f), sprite.getV(1f - 0.75f), sprite.getV(1f - 0.4375f),
                light, 1f, 1f, 1f, 0.85f, NOT_DOWN);
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.TERRAIN_SHEET;
    }

    private void renderBlockFormatCuboid(VertexConsumer buffer, Matrix4f mx4, float x1, float y1, float z1, float x2, float y2, float z2,
                                         float u0A, float u1A, float udV0, float udV1, float ewU0, float ewU1, float hV0, float hV1,
                                         int light, float red, float green, float blue, float alpha, Direction[] sides)
    {
        // Light calculations
        int lu0 = light & 65535;
        int lu1 = light >> 16 & 65535;

        // Quad draw
        for (Direction side : sides)
        {
            switch (side)
            {
                case UP ->
                {
                    // Top quad / Vertex CW top left / UV CW top left
                    buffer.addVertex(mx4, x1, y2, z2).setUv(u0A, udV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y2, z2).setUv(u1A, udV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y2, z1).setUv(u1A, udV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y2, z1).setUv(u0A, udV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
                case DOWN ->
                {
                    // Down quad / Vertex CCW bottom left / UV CW top left
                    buffer.addVertex(mx4, x1, y1, z1).setUv(u0A, udV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y1, z1).setUv(u1A, udV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y1, z2).setUv(u1A, udV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y1, z2).setUv(u0A, udV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
                case NORTH ->
                {
                    // North face / Vertex CCW bottom right / UV CW top left
                    buffer.addVertex(mx4, x2, y1, z1).setUv(u0A, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y1, z1).setUv(u1A, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y2, z1).setUv(u1A, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y2, z1).setUv(u0A, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
                case SOUTH ->
                {
                    // South quad / Vertex CCW top right / UV CCW bottom right
                    buffer.addVertex(mx4, x2, y2, z2).setUv(u1A, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y2, z2).setUv(u0A, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y1, z2).setUv(u0A, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y1, z2).setUv(u1A, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
                case EAST ->
                {
                    // East quad / Vertex CW bottom right / UV CW top left
                    buffer.addVertex(mx4, x2, y1, z2).setUv(ewU0, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y1, z1).setUv(ewU1, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y2, z1).setUv(ewU1, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x2, y2, z2).setUv(ewU0, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
                case WEST ->
                {
                    // West quad / Vertex CCW top right / UV ccw bottom right
                    buffer.addVertex(mx4, x1, y2, z2).setUv(ewU1, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y2, z1).setUv(ewU0, hV0).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y1, z1).setUv(ewU0, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                    buffer.addVertex(mx4, x1, y1, z2).setUv(ewU1, hV1).setColor(red, green, blue, alpha).setUv2(lu0, lu1);
                }
            }
        }
    }
}