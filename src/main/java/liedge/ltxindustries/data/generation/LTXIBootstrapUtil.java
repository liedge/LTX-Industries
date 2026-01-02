package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.upgrades.UpgradeIcon;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import net.minecraft.world.level.ItemLike;

import static liedge.ltxindustries.LTXIConstants.REM_BLUE;
import static liedge.ltxindustries.lib.upgrades.UpgradeIcon.*;

public final class LTXIBootstrapUtil
{
    private LTXIBootstrapUtil() {}

    // Helper UpgradeIcon factories
    public static UpgradeIcon bottomRightOverlay(UpgradeIcon background, String overlayPath, int width, int height)
    {
        int xo = 16 - width - 1;
        int yo = 16 - height - 1;

        return UpgradeIcon.overlayIcon(background, LTXIndustries.RESOURCES.location(overlayPath), width, height, xo, yo);
    }

    public static UpgradeIcon bottomRightOverlay(UpgradeIcon background, String overlayPath, int squareSize)
    {
        return bottomRightOverlay(background, overlayPath, squareSize, squareSize);
    }

    public static UpgradeIcon defaultOverlay(UpgradeIcon background)
    {
        return bottomRightOverlay(background, "default_overlay", 7);
    }

    public static UpgradeIcon defaultModuleIcon(ItemLike item)
    {
        return defaultOverlay(itemIcon(item));
    }

    public static UpgradeIcon luckOverlay(ItemLike item)
    {
        return bottomRightOverlay(itemIcon(item), "luck_overlay", 7);
    }

    public static UpgradeIcon plusOverlay(UpgradeIcon bg)
    {
        return bottomRightOverlay(bg, "plus_overlay", 9);
    }

    public static UpgradeIcon greenArrowOverlay(UpgradeIcon bg)
    {
        return bottomRightOverlay(bg, "green_arrow_overlay", 9);
    }

    public static UpgradeIcon yellowArrowOverlay(UpgradeIcon bg)
    {
        return bottomRightOverlay(bg, "yellow_arrow_overlay", 9);
    }

    public static UpgradeIcon redXOverlay(UpgradeIcon bg)
    {
        return bottomRightOverlay(bg, "red_x_overlay", 9);
    }

    public static UpgradeIcon veinMineOverlay(UpgradeIcon bg)
    {
        return bottomRightOverlay(bg, "vein_mine_overlay", 10, 6);
    }

    // Upgrade tooltips
    public static UpgradeComponentLike energyCapacityTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_CAPACITY_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike energyTransferTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_TRANSFER_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike energyUsageTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_USAGE_UPGRADE, REM_BLUE.chatStyle(), ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike parallelOpsTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.PARALLEL_OPERATIONS_UPGRADE, ValueComponent.of(value, format, sentiment));
    }
}