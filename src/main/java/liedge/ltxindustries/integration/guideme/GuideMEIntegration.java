package liedge.ltxindustries.integration.guideme;

import guideme.Guides;
import liedge.limacore.AllNotNull;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

@AllNotNull
public final class GuideMEIntegration
{
    public static final ResourceLocation GUIDE_ID = LTXIndustries.RESOURCES.location("guide");

    private GuideMEIntegration() {}

    public static boolean isGuideMEInstalled()
    {
        return ModList.get().isLoaded("guideme");
    }

    public static ItemStack createGuideTabletItem()
    {
        if (!isGuideMEInstalled())
        {
            return ItemStack.EMPTY;
        }
        else
        {
            return Guides.createGuideItem(GUIDE_ID);
        }
    }
}
