package liedge.ltxindustries.util;

import liedge.ltxindustries.LTXIConstants;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;

import java.util.function.UnaryOperator;

public enum LTXIChatStyles implements UnaryOperator<Style>
{
    LIME_GREEN(LTXIConstants.LIME_GREEN),
    REM_BLUE(LTXIConstants.REM_BLUE),
    HOSTILE_ORANGE(LTXIConstants.HOSTILE_ORANGE),
    UPGRADE_RANK_MAGENTA(LTXIConstants.UPGRADE_RANK_MAGENTA_1),
    INPUT_BLUE(LTXIConstants.INPUT_BLUE),
    OUTPUT_ORANGE(LTXIConstants.OUTPUT_ORANGE),
    INPUT_OUTPUT_GREEN(LTXIConstants.INPUT_OUTPUT_GREEN);

    private final int color;

    LTXIChatStyles(int color)
    {
        this.color = ARGB.opaque(color);
    }

    @Override
    public Style apply(Style style)
    {
        return style.withColor(color);
    }
}