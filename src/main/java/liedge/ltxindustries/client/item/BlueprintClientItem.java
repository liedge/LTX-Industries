package liedge.ltxindustries.client.item;

import liedge.limacore.client.ItemGuiRenderOverride;
import liedge.limacore.client.LimaCoreClient;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public final class BlueprintClientItem implements ItemGuiRenderOverride
{
    public static final BlueprintClientItem INSTANCE = new BlueprintClientItem();

    private BlueprintClientItem() {}

    @Override
    public boolean renderCustomGuiItem(GuiGraphicsExtractor graphics, @Nullable LivingEntity owner, @Nullable Level level, ItemStack stack, int x, int y)
    {
        if (Minecraft.getInstance().screen != null && Minecraft.getInstance().hasShiftDown())
        {
            RecipeHolder<FabricatingRecipe> recipe = LimaCoreClient.getClientRecipes().byKey(LTXIRecipeTypes.FABRICATING, stack.get(LTXIDataComponents.BLUEPRINT_RECIPE));
            if (recipe != null)
            {
                ItemStack result = recipe.value().getResultPreview();
                graphics.fakeItem(result, x, y);
                graphics.itemDecorations(Minecraft.getInstance().font, result, x, y);
                return true;
            }
        }

        return false;
    }
}