package liedge.ltxindustries.data.generation;

import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.icon.ItemIcon;
import liedge.ltxindustries.lib.icon.ItemLikeIcon;
import liedge.ltxindustries.lib.icon.SpriteIcon;
import liedge.ltxindustries.lib.upgrades.tooltip.*;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import net.minecraft.world.level.ItemLike;

import static liedge.ltxindustries.LTXIConstants.REM_BLUE;

public final class LTXIBootstrapUtil
{
    private LTXIBootstrapUtil() {}

    // Helper UpgradeIcon factories
    public static ItemLikeIcon bottomRightOverlay(ItemLikeIcon background, String overlayPath, int width, int height)
    {
        int xo = 16 - width - 1;
        int yo = 16 - height - 1;
        return background.add(SpriteIcon.create(overlayPath, width, height, xo, yo));
    }

    public static ItemLikeIcon bottomRightOverlay(ItemLikeIcon background, String overlayPath, int squareSize)
    {
        return bottomRightOverlay(background, overlayPath, squareSize, squareSize);
    }

    public static ItemLikeIcon defaultOverlay(ItemLikeIcon background)
    {
        return bottomRightOverlay(background, "default_overlay", 7);
    }

    public static ItemLikeIcon defaultModuleIcon(ItemLike item)
    {
        return defaultOverlay(ItemIcon.of(item));
    }

    public static ItemLikeIcon luckOverlay(ItemLike item)
    {
        return bottomRightOverlay(ItemIcon.of(item), "luck_overlay", 7);
    }

    public static ItemLikeIcon plusOverlay(ItemLikeIcon bg)
    {
        return bottomRightOverlay(bg, "plus_overlay", 9);
    }

    public static ItemLikeIcon greenArrowOverlay(ItemLikeIcon bg)
    {
        return bottomRightOverlay(bg, "green_arrow_overlay", 9);
    }

    public static ItemLikeIcon yellowArrowOverlay(ItemLikeIcon bg)
    {
        return bottomRightOverlay(bg, "yellow_arrow_overlay", 9);
    }

    public static ItemLikeIcon redXOverlay(ItemLikeIcon bg)
    {
        return bottomRightOverlay(bg, "red_x_overlay", 9);
    }

    public static ItemLikeIcon veinMineOverlay(ItemLikeIcon bg)
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

    public static UpgradeComponentLike fluidCapacityTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.FLUID_CAPACITY_UPGRADE, ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike parallelOpsTooltip(ContextlessValue value, ValueFormat format, ValueSentiment sentiment)
    {
        return TranslatableTooltip.create(LTXILangKeys.PARALLEL_OPERATIONS_UPGRADE, ValueComponent.of(value, format, sentiment));
    }

    public static UpgradeComponentLike energyActionsTooltip(ContextlessValue value)
    {
        return TranslatableTooltip.create(LTXILangKeys.ENERGY_ACTIONS_TOOLTIP, REM_BLUE.chatStyle(), ValueComponent.of(value, ValueFormat.FLAT_NUMBER, ValueSentiment.NEGATIVE));
    }
}