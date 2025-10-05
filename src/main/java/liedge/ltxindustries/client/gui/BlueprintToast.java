package liedge.ltxindustries.client.gui;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.renderer.item.UpgradeModuleItemExtensions;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class BlueprintToast implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = LTXIndustries.RESOURCES.location("blueprint_toast");
    private static final double DISPLAY_TIME = 5000.0d;

    private final List<FabricatingRecipe> recipes = new ObjectArrayList<>();
    private long lastChanged;
    private boolean changed;

    public BlueprintToast(FabricatingRecipe recipe)
    {
        recipes.add(recipe);
    }

    public void addRecipe(FabricatingRecipe recipe)
    {
        recipes.add(recipe);
        changed = true;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible)
    {
        // Refresh the last changed timestamp if a recipe was added to the toast
        if (changed)
        {
            lastChanged = timeSinceLastVisible;
            changed = false;
        }

        // Hide early if for some reason toast was created with no recipes
        if (recipes.isEmpty()) return Visibility.HIDE;

        graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
        graphics.drawString(toastComponent.getMinecraft().font, LTXILangKeys.BLUEPRINT_TOAST_MESSAGE.translate(), 35, 7, LTXIConstants.LIME_GREEN.argb32(), false);

        int size = recipes.size();
        int i = (int) ((double) timeSinceLastVisible / Math.max(1d, LimaCoreMath.divideDouble(DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier(), size)) % size);
        FabricatingRecipe recipe = recipes.get(i);
        ItemStack displayItem = recipe.getFabricatingResultItem();

        // Always render upgrade module icons. Otherwise, render the item normally
        if (displayItem.getItem() instanceof UpgradeModuleItem<?,?> moduleItem)
        {
            UpgradeBaseEntry<?> entry = displayItem.get(moduleItem.entryComponentType());
            if (entry != null)
            {
                UpgradeModuleItemExtensions.getInstance().renderIconWithRankBar(graphics, entry, 8, 8);
            }
            else
            {
                graphics.renderFakeItem(displayItem, 8, 8);
            }
        }
        else
        {
            graphics.renderFakeItem(displayItem, 8, 8);
        }

        return (double) (timeSinceLastVisible - lastChanged) >= DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }
}