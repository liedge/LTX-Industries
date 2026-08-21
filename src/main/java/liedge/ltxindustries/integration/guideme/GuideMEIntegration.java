package liedge.ltxindustries.integration.guideme;

import guideme.Guide;
import guideme.GuidesCommon;
import guideme.color.ColorValue;
import guideme.color.ConstantColor;
import guideme.color.SymbolicColorResolver;
import guideme.compiler.tags.BlockTagCompiler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

import java.util.Map;

public final class GuideMEIntegration
{
    public static final String MODID = "guideme";
    public static final Identifier GUIDE_ID = LTXIndustries.RESOURCES.id("guide");

    private GuideMEIntegration() { }

    public static void register()
    {
        if (isGuideMEInstalled())
        {
            Guide.builder(GUIDE_ID)
                    .extension(SymbolicColorResolver.EXTENSION_POINT, ColorMapBuilder.builder()
                            .add("energy", LTXIConstants.REM_BLUE)
                            .add("electric", LTXIConstants.ELECTRIC_GREEN)
                            .add("shield", LTXIConstants.BUBBLE_SHIELD_BLUE)
                            .build())
                    .extension(BlockTagCompiler.EXTENSION_POINT, new UpgradeEntryCompiler())
                    .build();
        }
    }

    public static boolean isGuideMEInstalled()
    {
        return ModList.get().isLoaded(MODID);
    }

    public static void openGuide(Player player)
    {
        GuidesCommon.openGuide(player, GUIDE_ID);
    }

    private static class ColorMapBuilder
    {
        private final Map<Identifier, ColorValue> map = new Object2ObjectOpenHashMap<>();

        private static ColorMapBuilder builder()
        {
            return new ColorMapBuilder();
        }

        ColorMapBuilder add(String name, LimaColor color)
        {
            map.put(LTXIndustries.RESOURCES.id(name), new ConstantColor(color.argb32()));
            return this;
        }

        SymbolicColorResolver build()
        {
            return map::get;
        }
    }
}