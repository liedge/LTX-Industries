package liedge.limatech.client.gui.widget;

import liedge.limatech.LimaTech;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public final class LimaWidgetSprites extends TextureAtlasHolder
{
    private static LimaWidgetSprites instance;

    public static LimaWidgetSprites getInstance()
    {
        if (instance == null) instance = new LimaWidgetSprites(Minecraft.getInstance().getTextureManager());
        return instance;
    }

    public static TextureAtlasSprite sprite(ResourceLocation location)
    {
        return getInstance().getSprite(location);
    }

    private LimaWidgetSprites(TextureManager textureManager)
    {
        super(textureManager, LimaTech.RESOURCES.textureLocation("atlas", "lima_widget"), LimaTech.RESOURCES.location("lima_widget"));
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }
}