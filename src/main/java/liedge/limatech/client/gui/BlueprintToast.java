package liedge.limatech.client.gui;

import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.client.renderer.item.UpgradeModuleItemExtensions;
import liedge.limatech.item.UpgradeModuleItem;
import liedge.limatech.lib.upgrades.UpgradeBaseEntry;
import liedge.limatech.recipe.FabricatingRecipe;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class BlueprintToast implements Toast
{
    private static final ResourceLocation BACKGROUND_SPRITE = LimaTech.RESOURCES.location("blueprint_toast");
    private static final int DISPLAY_TIME = 5000;

    private final Renderable renderable;

    public BlueprintToast(FabricatingRecipe recipe)
    {
        ItemStack stack = recipe.getResultItem();

        Renderable function = null;

        // Special handling for upgrade modules (always show icon)
        if (stack.getItem() instanceof UpgradeModuleItem<?,?> moduleItem)
        {
            UpgradeBaseEntry<?> entry = stack.get(moduleItem.entryComponentType());
            if (entry != null) function = (graphics, x, y) -> UpgradeModuleItemExtensions.getInstance().renderIconWithRankBar(graphics, entry, x, y);
        }

        // Otherwise, render the item stack
        if (function == null) function = (graphics, x, y) -> graphics.renderFakeItem(stack, x, y);

        this.renderable = function;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible)
    {
        graphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
        graphics.drawString(toastComponent.getMinecraft().font, LimaTechLang.BLUEPRINT_TOAST_MESSAGE.translate(), 35, 7, LimaTechConstants.LIME_GREEN.argb32(), false);

        renderable.render(graphics, 8, 8);

        return timeSinceLastVisible >= (double) DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    @FunctionalInterface
    private interface Renderable
    {
        void render(GuiGraphics graphics, int x, int y);
    }
}