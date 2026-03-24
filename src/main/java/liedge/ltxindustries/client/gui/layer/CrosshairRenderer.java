package liedge.ltxindustries.client.gui.layer;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.LTXIRenderer;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public abstract class CrosshairRenderer
{
    public static CrosshairRenderer bracketsWithHollowDot()
    {
        return BracketsWithDot.INSTANCE;
    }

    public static CrosshairRenderer circleBrackets()
    {
        return CircleBrackets.INSTANCE;
    }

    public static CrosshairRenderer aoeRing(float animationFactor)
    {
        return new AOERing(animationFactor);
    }

    private static final Identifier HOLLOW_DOT = RESOURCES.id("crosshair/hollow_dot");
    private static final Identifier CIRCLE_BRACKET = RESOURCES.id("crosshair/circle_bracket");
    private static final Identifier ANGLE_BRACKET = RESOURCES.id("crosshair/angle_bracket");
    private static final Identifier AOE_HORIZONTAL = RESOURCES.id("crosshair/aoe_h");
    private static final Identifier AOE_VERTICAL = RESOURCES.id("crosshair/aoe_v");
    private static final Identifier HEAVY_PISTOL_CROSSHAIR = RESOURCES.id("crosshair/heavy_pistol");

    private final int xOffset;
    private final int yOffset;
    private final float bloomDistance;
    private final float animationFactor;

    protected CrosshairRenderer(int xOffset, int yOffset, float bloomDistance, float animationFactor)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.bloomDistance = bloomDistance;
        this.animationFactor = animationFactor;
    }

    public final void render(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int screenWidth, int screenHeight, LimaColor color, float partialTick)
    {
        int centerX = (screenWidth - xOffset) / 2;
        int centerY = (screenHeight - yOffset) / 2;
        float bloom = bloomDistance * LTXIRenderer.linearThresholdCurve(controls.lerpTriggerTimer(weaponItem, partialTick), animationFactor);

        renderSprites(graphics, pipeline, player, weaponItem, controls, centerX, centerY, bloom, color, partialTick);
    }

    protected abstract void renderSprites(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, float bloom, LimaColor color, float partialTick);

    protected void submitCrosshairSprite(GuiGraphics graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite, x, y, width, height, color.argb32());
    }

    protected void submitCrosshairSpriteMirrorU(GuiGraphics graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + height, sprite.getU1(), sprite.getU0(), sprite.getV0(), sprite.getV1(), color.argb32());
    }

    protected void submitCrosshairSpriteMirrorV(GuiGraphics graphics, RenderPipeline pipeline, Identifier spriteId, float x, float y, int width, int height, LimaColor color)
    {
        TextureAtlasSprite sprite = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.GUI).getSprite(spriteId);
        LimaGuiUtil.floatBlit(graphics, pipeline, sprite.atlasLocation(), x, y, x + width, y + height, sprite.getU0(), sprite.getU1(), sprite.getV1(), sprite.getV0(), color.argb32());
    }

    private static class BracketsWithDot extends CrosshairRenderer
    {
        private static final BracketsWithDot INSTANCE = new BracketsWithDot();

        private BracketsWithDot()
        {
            super(5, 5, 5f, 0.1f);
        }

        @Override
        protected void renderSprites(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, float bloom, LimaColor color, float partialTick)
        {
            submitCrosshairSprite(graphics, pipeline, HOLLOW_DOT, x, y, 5, 5, color);
            submitCrosshairSprite(graphics, pipeline, ANGLE_BRACKET, x - 6 - bloom, y - 1, 4, 7, color);
            submitCrosshairSpriteMirrorU(graphics, pipeline, ANGLE_BRACKET, x + 7 + bloom, y - 1, 4, 7, color);
        }
    }

    private static class CircleBrackets extends CrosshairRenderer
    {
        private static final CircleBrackets INSTANCE = new CircleBrackets();

        private CircleBrackets()
        {
            super(1, 13, 4f, 0.1f);
        }

        @Override
        protected void renderSprites(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, float bloom, LimaColor color, float partialTick)
        {
            submitCrosshairSprite(graphics, pipeline, CIRCLE_BRACKET, x - 6 - bloom, y, 6, 13, color);
            submitCrosshairSpriteMirrorU(graphics, pipeline, CIRCLE_BRACKET, x + 1 + bloom, y, 6, 13, color);
        }
    }

    private static class FocusableCircleBrackets extends CrosshairRenderer
    {
        private FocusableCircleBrackets(int xOffset, int yOffset, float bloomDistance, float animationFactor)
        {
            super(xOffset, yOffset, bloomDistance, animationFactor);
        }

        @Override
        protected void renderSprites(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, float bloom, LimaColor color, float partialTick)
        {

        }
    }

    private static class AOERing extends CrosshairRenderer
    {
        protected AOERing(float animationFactor)
        {
            super(5, 5, 4f, animationFactor);
        }

        @Override
        protected void renderSprites(GuiGraphics graphics, RenderPipeline pipeline, LocalPlayer player, WeaponItem weaponItem, ClientExtendedInput controls, int x, int y, float bloom, LimaColor color, float partialTick)
        {
            submitCrosshairSprite(graphics, pipeline, HOLLOW_DOT, x, y, 5, 5, color);

            submitCrosshairSpriteMirrorV(graphics, pipeline, AOE_VERTICAL, x - 1, y - 4 - bloom, 7, 2, color);
            submitCrosshairSprite(graphics, pipeline, AOE_VERTICAL, x - 1, y + 7 + bloom, 7, 2, color);

            submitCrosshairSprite(graphics, pipeline, AOE_HORIZONTAL, x - 4 - bloom, y - 1, 2, 7, color);
            submitCrosshairSpriteMirrorU(graphics, pipeline, AOE_HORIZONTAL, x + 7 + bloom, y - 1, 2, 7, color);
        }
    }
}