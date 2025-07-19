package liedge.ltxindustries.client.gui.widget;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.resources.ResourceLocation;

public final class LTXIWidgetSprites extends TextureAtlasHolder
{
    public static final ResourceLocation ATLAS_LOCATION = LTXIndustries.RESOURCES.location("widgets");
    private static final ResourceLocation ATLAS_TEXTURE = LTXIndustries.RESOURCES.textureLocation("atlas", "widgets");

    private static LTXIWidgetSprites instance;

    public static LTXIWidgetSprites getInstance()
    {
        if (instance == null) instance = new LTXIWidgetSprites(Minecraft.getInstance().getTextureManager());
        return instance;
    }

    public static TextureAtlasSprite sprite(ResourceLocation location)
    {
        return getInstance().getSprite(location);
    }

    private LTXIWidgetSprites(TextureManager textureManager)
    {
        super(textureManager, ATLAS_TEXTURE, ATLAS_LOCATION);
    }

    @Override
    public TextureAtlasSprite getSprite(ResourceLocation location)
    {
        return super.getSprite(location);
    }
}