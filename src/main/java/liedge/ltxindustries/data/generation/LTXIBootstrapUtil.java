package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.upgrades.UpgradeIcon;
import liedge.ltxindustries.lib.upgrades.effect.value.UpgradeDoubleValue;
import net.minecraft.world.level.ItemLike;

import static liedge.ltxindustries.LTXIConstants.REM_BLUE;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.itemIcon;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.sprite;

public final class LTXIBootstrapUtil
{
    private LTXIBootstrapUtil() {}

    // Helper UpgradeIcon factories
    public static UpgradeIcon bottomRightComposite(UpgradeIcon background, UpgradeIcon overlay, int overlaySize, int padding)
    {
        int offset = 16 - overlaySize - padding;
        return UpgradeIcon.compositeIcon(background, overlay, overlaySize, offset, offset);
    }

    public static UpgradeIcon bottomRightComposite(UpgradeIcon background, UpgradeIcon overlay, int overlaySize)
    {
        return bottomRightComposite(background, overlay, overlaySize, 1);
    }

    public static UpgradeIcon spriteOverItemIcon(ItemLike bgItem, String spritePath, int spriteSize)
    {
        return bottomRightComposite(itemIcon(bgItem), sprite(spritePath), spriteSize);
    }

    public static UpgradeIcon defaultModuleIcon(ItemLike item)
    {
        return spriteOverItemIcon(item, "default_overlay", 7);
    }

    public static UpgradeIcon luckOverlayIcon(ItemLike item)
    {
        return spriteOverItemIcon(item, "luck_overlay", 7);
    }

    public static UpgradeIcon plusOverlay(UpgradeIcon bg)
    {
        return bottomRightComposite(bg, sprite("plus_overlay"), 9);
    }

    public static UpgradeIcon greenArrowOverlay(UpgradeIcon bg)
    {
        return bottomRightComposite(bg, sprite("green_arrow_overlay"), 9);
    }

    public static UpgradeIcon yellowArrowOverlay(UpgradeIcon bg)
    {
        return bottomRightComposite(bg, sprite("yellow_arrow_overlay"), 9);
    }

    public static UpgradeIcon redXOverlay(UpgradeIcon bg)
    {
        return bottomRightComposite(bg, sprite("red_x_overlay"), 9);
    }

    // Upgrade tooltips
    public static UpgradeComponentLike energyCapacityTooltip(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike energyTransferTooltip(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike energyUsageTooltip(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_USAGE_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike parallelOpsTooltip(UpgradeDoubleValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.PARALLEL_OPERATIONS_UPGRADE, ValueComponent.of(value, format, sentiment));
    }
}