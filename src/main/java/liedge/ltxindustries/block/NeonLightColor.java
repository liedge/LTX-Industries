package liedge.ltxindustries.block;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum NeonLightColor
{
    // Standard colors
    WHITE(DyeColor.WHITE),
    ORANGE(DyeColor.ORANGE),
    MAGENTA(DyeColor.MAGENTA),
    LIGHT_BLUE(DyeColor.LIGHT_BLUE),
    YELLOW(DyeColor.YELLOW),
    LIME(DyeColor.LIME),
    PINK(DyeColor.PINK),
    GRAY(DyeColor.GRAY),
    LIGHT_GRAY(DyeColor.LIGHT_GRAY),
    CYAN(DyeColor.CYAN),
    PURPLE(DyeColor.PURPLE),
    BLUE(DyeColor.BLUE),
    BROWN(DyeColor.BROWN),
    GREEN(DyeColor.GREEN),
    RED(DyeColor.RED),
    BLACK(DyeColor.BLACK),
    // Custom colors
    LTX_LIME(MapColor.COLOR_LIGHT_GREEN, null),
    ENERGY_BLUE(MapColor.COLOR_LIGHT_BLUE, null),
    ELECTRIC_CHARTREUSE(MapColor.COLOR_LIGHT_GREEN, null),
    VIRIDIC_GREEN(MapColor.PLANT, null),
    NEURO_BLUE(MapColor.WATER, null);

    private final MapColor mapColor;
    private final @Nullable DyeColor dyeColor;

    NeonLightColor(MapColor mapColor, @Nullable DyeColor dyeColor)
    {
        this.mapColor = mapColor;
        this.dyeColor = dyeColor;
    }

    NeonLightColor(DyeColor color)
    {
        this(color.getMapColor(), color);
    }

    @Override
    public String toString()
    {
        return name().toLowerCase(Locale.ROOT);
    }

    public MapColor getMapColor()
    {
        return mapColor;
    }

    public @Nullable DyeColor getDyeColor()
    {
        return dyeColor;
    }
}