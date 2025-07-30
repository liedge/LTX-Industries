package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class FabricatorBlockEntity extends BaseFabricatorBlockEntity
{
    public FabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.FABRICATOR.get(), pos, state, 3);
    }

    @Override
    public int inputSlotsStart()
    {
        return -1;
    }

    @Override
    public int inputSlotsCount()
    {
        return 0;
    }

    @Override
    public boolean isInputSlot(int index)
    {
        return false;
    }

    public void startCrafting(Level level, RecipeHolder<FabricatingRecipe> holder, LimaRecipeInput input, boolean forceStart)
    {
        FabricatingRecipe recipe = holder.value();

        if (!isCrafting() && canInsertRecipeResults(level, recipe))
        {
            if (forceStart)
            {
                setCrafting(true);
                getRecipeCheck().setLastUsedRecipe(holder);
            }
            else if (recipe.matches(input, level))
            {
                recipe.consumeIngredientsLenientSlots(input, false);
                setCrafting(true);
                getRecipeCheck().setLastUsedRecipe(holder);
            }
        }
    }

    private void stopCrafting(boolean insertResult, Level level)
    {
        getRecipeCheck().getLastUsedRecipe(level).ifPresent(holder ->
        {
            if (insertResult)
            {
                getItemHandler().insertItem(FABRICATOR_OUTPUT_SLOT, holder.value().generateFabricatingResult(level.random), false);
            }

            energyCraftProgress = 0;
            setCrafting(false);
        });
    }

    @Override
    protected void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Try progressing crafting recipe
        FabricatingRecipe recipe = getRecipeCheck().getLastUsedRecipe(level).map(RecipeHolder::value).orElse(null);
        if (isCrafting() && recipe != null)
        {
            if (energyCraftProgress < recipe.getEnergyRequired())
            {
                int toExtract = Math.min(getEnergyUsage(), recipe.getEnergyRequired() - energyCraftProgress);
                energyCraftProgress += getEnergyStorage().extractEnergy(toExtract, false);
            }
            else
            {
                stopCrafting(true, level);
            }
        }
        else
        {
            stopCrafting(false, level);
        }
    }

    @Override
    protected ItemLike getValidBlueprintItem()
    {
        return LTXIItems.EMPTY_FABRICATION_BLUEPRINT;
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.FABRICATOR.get();
    }
}