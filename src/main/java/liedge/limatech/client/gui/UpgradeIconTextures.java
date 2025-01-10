package liedge.limatech.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

import static liedge.limatech.LimaTech.RESOURCES;

public final class UpgradeIconTextures extends TextureAtlasHolder
{
    private static UpgradeIconTextures instance;

    public static UpgradeIconTextures getUpgradeSprites()
    {
        if (instance == null) instance = new UpgradeIconTextures(Minecraft.getInstance().getTextureManager());

        return instance;
    }

    private UpgradeIconTextures(TextureManager textureManager)
    {
        super(textureManager, RESOURCES.textureLocation("atlas", "upgrade_icon"), RESOURCES.location("upgrade_icon"));
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }
}