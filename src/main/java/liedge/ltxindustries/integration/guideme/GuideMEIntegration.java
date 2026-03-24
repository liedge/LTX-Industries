package liedge.ltxindustries.integration.guideme;

import guideme.Guides;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;

public final class GuideMEIntegration
{
    public static final Identifier GUIDE_ID = LTXIndustries.RESOURCES.id("guide");

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
            ItemStack stack = Guides.createGuideItem(GUIDE_ID);
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
            return stack;
        }
    }
}