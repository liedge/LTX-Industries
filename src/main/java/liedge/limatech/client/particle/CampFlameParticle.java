package liedge.limatech.client.particle;

import liedge.limacore.lib.ModResources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class CampFlameParticle extends TextureSheetParticle
{
    private static final ResourceLocation SPRITE_LOCATION = ModResources.MC.location("block/campfire_fire");

    public CampFlameParticle(ParticleType<SimpleParticleType> type, ClientLevel level, double x, double y, double z, double dx, double dy, double dz)
    {
        super(level, x, y, z);
        setParticleSpeed(dx, dy, dz);
        setSprite(Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(SPRITE_LOCATION));
        this.quadSize = 0.125f + (random.nextFloat() * 0.15f);
        setSize(quadSize, quadSize);
        this.lifetime = random.nextIntBetweenInclusive(14, 24);
        this.gravity = 0.775f;
        this.friction = 0.95f;
    }

    @Override
    protected int getLightColor(float partialTick)
    {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType()
    {
        return ParticleRenderType.TERRAIN_SHEET;
    }
}