package liedge.limatech.compat.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.client.gui.screen.MaterialFusingChamberScreen;
import liedge.limatech.client.gui.widget.ScreenWidgetSprites;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.registry.LimaTechBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;
import java.util.function.Supplier;

public class MaterialFusingJEICategory extends LimaJEICategory<MaterialFusingRecipe>
{
    private final IDrawableStatic progressBackground;
    private final IDrawableAnimated progressForeground;

    MaterialFusingJEICategory(IGuiHelper helper, Supplier<LimaRecipeType<MaterialFusingRecipe>> typeSupplier)
    {
        super(helper, typeSupplier, MaterialFusingChamberScreen.SCREEN_TEXTURE, 40, 25, 92, 38);
        this.progressBackground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.MACHINE_PROGRESS_BACKGROUND).build();
        this.progressForeground = unmanagedSpriteDrawable(helper, ScreenWidgetSprites.MACHINE_PROGRESS_FOREGROUND).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public RecipeType<RecipeHolder<MaterialFusingRecipe>> getRecipeType()
    {
        return LimaTechJEIPlugin.MATERIAL_FUSING_JEI;
    }

    @Override
    public void draw(RecipeHolder<MaterialFusingRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY)
    {
        progressBackground.draw(guiGraphics, 41, 16);
        progressForeground.draw(guiGraphics, 42, 17);
    }

    @Override
    protected ItemStack categoryIconItemStack()
    {
        return LimaTechBlocks.MATERIAL_FUSING_CHAMBER.toStack();
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MaterialFusingRecipe> holder, MaterialFusingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        List<Ingredient> ingredients = recipe.getIngredients();
        for (int i = 0; i < ingredients.size(); i++)
        {
            Ingredient ingredient = ingredients.get(i);
            switch (i)
            {
                case 0 -> builder.addSlot(RecipeIngredientRole.INPUT, 2, 2).addIngredients(ingredient);
                case 1 -> builder.addSlot(RecipeIngredientRole.INPUT, 20, 2).addIngredients(ingredient);
                case 2 -> builder.addSlot(RecipeIngredientRole.INPUT, 11, 20).addIngredients(ingredient);
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 72, 11).addItemStack(recipe.getResultItem(registries));
    }
}