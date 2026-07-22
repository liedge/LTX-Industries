package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.recipe.LTXIRecipe;
import liedge.ltxindustries.recipe.LTXIRecipeInput;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public abstract class LTXIRecipeMachineBlockEntity<R extends LTXIRecipe> extends BaseRecipeMachineBlockEntity<LTXIRecipeInput, R> implements RecipeModeHolderBlockEntity
{
    private @Nullable Holder<RecipeMode> mode;
    private HolderSet<RecipeMode> availableModes = HolderSet.empty();

    protected LTXIRecipeMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, RecipeType<R> recipeType, BlockPos pos, BlockState state, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, recipeType, pos, state, inputSlots, outputSlots, inputTanks, outputTanks);
    }

    @Override
    public @Nullable Holder<RecipeMode> getMode()
    {
        return mode;
    }

    @Override
    public void setMode(@Nullable Holder<RecipeMode> mode)
    {
        this.mode = mode;

        if (checkServerSide())
        {
            setChanged();
            reCheckRecipe();
        }
    }

    @Override
    public HolderSet<RecipeMode> getAvailableRecipeModes()
    {
        return availableModes;
    }

    @Override
    public void setAvailableRecipeModes(HolderSet<RecipeMode> availableModes)
    {
        this.availableModes = availableModes;
    }

    @Override
    protected LTXIRecipeInput getRecipeInput(Level level)
    {
        return new LTXIRecipeInput(getItems(BlockContentsType.INPUT), getFluids(BlockContentsType.INPUT), mode);
    }

    @Override
    protected int getBaseRecipeCraftingTime(R recipe)
    {
        return recipe.getCraftTime();
    }

    @Override
    protected void consumeIngredients(LTXIRecipeInput inputAccess, R recipe, Level level)
    {
        recipe.consumeItemInputs(inputAccess, level.getRandom());
        recipe.consumeFluidInputs(inputAccess, level.getRandom());
    }

    @Override
    public boolean canInsertRecipeResults(ServerLevel level, R recipe, LTXIRecipeInput inputAccess)
    {
        boolean itemsCheck = canInsertResourceResults(recipe.getItemResults(), getItems(BlockContentsType.OUTPUT));
        boolean fluidsCheck = canInsertResourceResults(recipe.getFluidResults(), getFluids(BlockContentsType.OUTPUT));

        return itemsCheck && fluidsCheck;
    }

    @Override
    protected void insertRecipeResults(Level level, R recipe, LTXIRecipeInput recipeInput)
    {
        // Insert item results
        List<ResourceStack<ItemResource>> itemResults = recipe.generateItemResults(level.getRandom());
        insertResourceResults(itemResults, getItems(BlockContentsType.OUTPUT));

        // Insert fluid results
        List<ResourceStack<FluidResource>> fluidResults = recipe.generateFluidResults(level.getRandom());
        insertResourceResults(fluidResults, getFluids(BlockContentsType.OUTPUT));
    }

    @Override
    protected void craftRecipe(ServerLevel level, R recipe, int maxOperations)
    {
        LTXIRecipeInput input = getRecipeInput(level);

        boolean a = recipe.getItemInputs().stream().allMatch(o -> o.consumeChance() == 0f);
        boolean b = recipe.getFluidInputs().stream().allMatch(o -> o.consumeChance() == 0f);
        boolean skipInputCheck = a && b;

        for (int i = 0 ; i < maxOperations; i++)
        {
            if (i > 0)
            {
                boolean canContinue = (skipInputCheck || recipe.matches(input, level)) && canInsertRecipeResults(level, recipe, input);
                if (!canContinue) break;
            }

            insertRecipeResults(level, recipe, input);
            consumeIngredients(input, recipe, level);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.mode = input.read(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC).orElse(null);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.storeNullable(TAG_KEY_RECIPE_MODE, RecipeMode.CODEC, mode);
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        RecipeModeHolderBlockEntity.applyUpgrades(this, upgrades);
    }
}