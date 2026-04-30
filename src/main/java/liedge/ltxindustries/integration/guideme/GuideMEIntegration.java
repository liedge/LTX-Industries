package liedge.ltxindustries.integration.guideme;

import guideme.GuidesCommon;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;

public final class GuideMEIntegration
{
    public static final String MODID = "guideme";
    public static final Identifier GUIDE_ID = LTXIndustries.RESOURCES.id("guide");

    private GuideMEIntegration() { }

    public static boolean isGuideMEInstalled()
    {
        return ModList.get().isLoaded(MODID);
    }

    public static void openGuide(Player player)
    {
        GuidesCommon.openGuide(player, GUIDE_ID);
    }
}