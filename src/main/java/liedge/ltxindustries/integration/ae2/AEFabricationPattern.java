package liedge.ltxindustries.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record AEFabricationPattern(AEItemKey definition, RecipeHolder<FabricatingRecipe> recipe, IInput[] inputs, List<GenericStack> output) implements IPatternDetails
{
    static @Nullable AEFabricationPattern tryCreate(AEItemKey definition, Level level)
    {
        if (!(level instanceof ServerLevel serverLevel)) return null;
        ResourceKey<Recipe<?>> key = definition.get(LTXIDataComponents.BLUEPRINT_RECIPE.get());
        if (key == null) return null;

        RecipeHolder<FabricatingRecipe> holder = LimaRegistryUtil.getRecipeByKey(serverLevel, key, LTXIRecipeTypes.FABRICATING).orElse(null);
        if (holder == null) return null;

        FabricatingRecipe recipe = holder.value();

        IInput[] inputs = recipe.getItemInputs().stream().map(Input::of).filter(Objects::nonNull).toArray(IInput[]::new);
        GenericStack output = GenericStack.fromItemStack(recipe.getResultPreview());
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
        @SuppressWarnings("deprecation")
        private static @Nullable Input of(RecipeItemInput recipeInput)
        {
            Ingredient ingredient = recipeInput.ingredient();

            GenericStack[] template = ingredient.items()
                    .map(holder -> GenericStack.fromItemStack(holder.value().getDefaultInstance()))
                    .toArray(GenericStack[]::new);

            return new Input(template, recipeInput.count(), ingredient);
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