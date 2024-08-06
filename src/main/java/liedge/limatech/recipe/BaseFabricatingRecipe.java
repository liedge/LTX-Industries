package liedge.limatech.recipe;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.menu.tooltip.FabricatorIngredientTooltip;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class BaseFabricatingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    private final int energyRequired;

    protected BaseFabricatingRecipe(NonNullList<Ingredient> ingredients, int energyRequired)
    {
        super(ingredients);
        this.energyRequired = energyRequired;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public TooltipComponent createIngredientTooltip()
    {
        return new FabricatorIngredientTooltip(getIngredients());
    }

    @Override
    public abstract String getGroup();

    @Override
    public abstract ItemStack assemble(@Nullable LimaRecipeInput input, HolderLookup.Provider registries);

    @Override
    public abstract ItemStack getResultItem(@Nullable HolderLookup.Provider registries);

    @Override
    public boolean matches(LimaRecipeInput input, @Nullable Level level)
    {
        return consumeIngredientsLenientSlots(input, true);
    }

    @Override
    public final RecipeType<?> getType()
    {
        return LimaTechRecipeTypes.FABRICATING.get();
    }
}