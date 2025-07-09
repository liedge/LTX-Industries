package liedge.limatech.client.gui;

import liedge.limatech.LimaTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public final class UpgradeIconSprites extends TextureAtlasHolder
{
    public static final ResourceLocation ATLAS_LOCATION = LimaTech.RESOURCES.location("upgrade_modules");
    private static final ResourceLocation ATLAS_TEXTURE = LimaTech.RESOURCES.textureLocation("atlas", "upgrade_modules");

    private static UpgradeIconSprites instance;

    public static UpgradeIconSprites getInstance()
    {
        if (instance == null) instance = new UpgradeIconSprites(Minecraft.getInstance().getTextureManager());
        return instance;
    }

    private UpgradeIconSprites(TextureManager textureManager)
    {
        super(textureManager, ATLAS_TEXTURE, ATLAS_LOCATION);
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }
}