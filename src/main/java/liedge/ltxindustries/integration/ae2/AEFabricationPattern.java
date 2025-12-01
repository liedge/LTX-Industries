package liedge.ltxindustries.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record AEFabricationPattern(AEItemKey definition, RecipeHolder<FabricatingRecipe> recipe, IInput[] inputs, List<GenericStack> output) implements IPatternDetails
{
    static @Nullable AEFabricationPattern tryCreate(AEItemKey definition, Level level)
    {
        RecipeHolder<FabricatingRecipe> holder = Optional.ofNullable(definition.get(LTXIDataComponents.BLUEPRINT_RECIPE.get())).flatMap(id -> LimaRecipesUtil.getRecipeById(level, id, LTXIRecipeTypes.FABRICATING)).orElse(null);
        if (holder == null) return null;

        FabricatingRecipe recipe = holder.value();

        IInput[] inputs = recipe.getItemIngredients().stream().map(Input::of).filter(Objects::nonNull).toArray(IInput[]::new);
        GenericStack output = GenericStack.fromItemStack(recipe.getFabricatingResultItem());
        if (output == null) return null;

        return new AEFabricationPattern(definition, holder, inputs, List.of(output));
    }

    @Override
    public AEItemKey getDefinition()
    {
        return definition;
    }

    @Override
    public IInput[] getInputs()
    {
        return inputs;
    }

    @Override
    public List<GenericStack> getOutputs()
    {
        return output;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof AEFabricationPattern that)) return false;
        return Objects.equals(definition, that.definition);
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode(definition);
    }

    @Override
    public boolean supportsPushInputsToExternalInventory()
    {
        return false;
    }

    private record Input(GenericStack[] template, long multiplier, Ingredient ingredient) implements IInput
    {
        private static @Nullable Input of(LimaSizedItemIngredient sizedIngredient)
        {
            Ingredient ingredient = sizedIngredient.getIngredient();
            if (ingredient.getItems().length == 0) return null;

            GenericStack[] template = Arrays.stream(ingredient.getItems()).map(GenericStack::fromItemStack).toArray(GenericStack[]::new);

            return new Input(template, sizedIngredient.getSize(), ingredient);
        }

        @Override
        public GenericStack[] getPossibleInputs()
        {
            return template;
        }

        @Override
        public long getMultiplier()
        {
            return multiplier;
        }

        @Override
        public boolean isValid(AEKey input, Level level)
        {
            if (input instanceof AEItemKey itemKey)
            {
                return itemKey.matches(ingredient);
            }

            return false;
        }

        @Override
        public @Nullable AEKey getRemainingKey(AEKey template)
        {
            return null;
        }
    }
}