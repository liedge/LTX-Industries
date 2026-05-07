package liedge.ltxindustries.integration.ae2;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;

record AEFabricationPattern(AEItemKey definition, RecipeHolder<FabricatingRecipe> recipe, IInput[] inputs, List<GenericStack> output) implements IPatternDetails
{
    private static @Nullable RecipeHolder<FabricatingRecipe> findRecipe(Level level, ResourceKey<Recipe<?>> key)
    {
        if (level instanceof ServerLevel serverLevel)
        {
            return LimaRegistryUtil.getRecipeByKey(serverLevel, key, LTXIRecipeTypes.FABRICATING).orElse(null);
        }
        else
        {
            return LimaCoreClient.getClientRecipes().byKey(LTXIRecipeTypes.FABRICATING, key);
        }
    }

    static @Nullable AEFabricationPattern tryCreate(AEItemKey definition, Level level)
    {
        ResourceKey<Recipe<?>> key = definition.get(LTXIDataComponents.BLUEPRINT_RECIPE.get());
        if (key == null) return null;

        RecipeHolder<FabricatingRecipe> holder = findRecipe(level, key);
        if (holder == null) return null;

        FabricatingRecipe recipe = holder.value();

        ResourceStack<ItemResource> resultStack = recipe.generateItemResult(level);
        GenericStack output = GenericStack.from(resultStack.resource(), resultStack.amount());
        if (output == null) return null;
        IInput[] inputs = recipe.getItemInputs().stream().map(Input::new).toArray(IInput[]::new);

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

    private static class Input implements IInput
    {
        private final RecipeItemInput recipeInput;
        private final GenericStack[] template;

        @SuppressWarnings("deprecation")
        private Input(RecipeItemInput recipeInput)
        {
            this.recipeInput = recipeInput;
            this.template = recipeInput.ingredient().items()
                    .map(Holder::value)
                    .map(item -> GenericStack.fromItemStack(item.getDefaultInstance()))
                    .toArray(GenericStack[]::new);
        }

        @Override
        public GenericStack[] getPossibleInputs()
        {
            return template;
        }

        @Override
        public long getMultiplier()
        {
            return recipeInput.count();
        }

        @Override
        public boolean isValid(AEKey input, Level level)
        {
            return input instanceof AEItemKey itemKey && itemKey.matches(recipeInput.ingredient());
        }

        @Override
        public @Nullable AEKey getRemainingKey(AEKey template)
        {
            return null;
        }
    }
}