package liedge.limatech.client.renderer.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

public final class BlueprintItemExtensions implements ItemGuiRenderOverride
{
    public static final BlueprintItemExtensions INSTANCE = new BlueprintItemExtensions();

    private static boolean shouldShowIcon()
    {
        return Minecraft.getInstance().screen != null && Screen.hasShiftDown();
    }

    @Override
    public boolean renderCustomGuiItem(GuiGraphics graphics, ItemStack stack, int x, int y)
    {
        if (shouldShowIcon())
        {
            ResourceLocation id = stack.get(LimaTechDataComponents.BLUEPRINT_RECIPE);
            if (id != null && Minecraft.getInstance().level != null)
            {
                RecipeHolder<FabricatingRecipe> recipe = LimaRecipesUtil.getRecipeById(Minecraft.getInstance().level, id, LimaTechRecipeTypes.FABRICATING).orElse(null);
                if (recipe != null)
                {
                    ItemStack result = recipe.value().getResultItem();
                    graphics.renderFakeItem(result, x, y);
                    graphics.renderItemDecorations(Minecraft.getInstance().font, result, x, y);
                    return true;
                }
            }
        }

        return false;
    }
}