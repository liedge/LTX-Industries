package liedge.ltxindustries.client.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.ltxindustries.client.LTXIClientRecipes;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class BlueprintClientItem implements ItemGuiRenderOverride
{
    public static final BlueprintClientItem INSTANCE = new BlueprintClientItem();

    private BlueprintClientItem() {}

    @Override
    public boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (Minecraft.getInstance().screen != null && Minecraft.getInstance().hasShiftDown())
        {
            RecipeHolder<FabricatingRecipe> recipe = LTXIClientRecipes.byKey(LTXIRecipeTypes.FABRICATING, stack.get(LTXIDataComponents.BLUEPRINT_RECIPE));
            if (recipe != null)
            {
                ItemStack result = recipe.value().getResultPreview();
                graphics.renderFakeItem(result, x, y);
                graphics.renderItemDecorations(Minecraft.getInstance().font, result, x, y);
                return true;
            }
        }

        return false;
    }
}