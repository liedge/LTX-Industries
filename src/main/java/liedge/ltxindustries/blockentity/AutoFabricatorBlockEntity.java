package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public class AutoFabricatorBlockEntity extends BaseFabricatorBlockEntity
{
    private boolean shouldCheckRecipe;
    private boolean shouldCheckBlueprint;

    public AutoFabricatorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.AUTO_FABRICATOR.get(), pos, state, 19);
    }

    private void updateBlueprint(Level level)
    {
        shouldCheckBlueprint = false;

        ItemStack bpItem = getItemHandler().getStackInSlot(BLUEPRINT_ITEM_SLOT);
        ResourceLocation bpId = bpItem.get(LTXIDataComponents.BLUEPRINT_RECIPE);
        RecipeHolder<FabricatingRecipe> holder = null;

        if (bpId != null) holder = LimaRecipesUtil.getRecipeById(level, bpId, LTXIRecipeTypes.FABRICATING).orElse(null);

        getRecipeCheck().setLastUsedRecipe(holder);
    }

    private LimaRecipeInput createRecipeInput()
    {
        return LimaRecipeInput.createWithSize(getItemHandler(), 3, 16);
    }

    @Override
    public int inputSlotsStart()
    {
        return 3;
    }

    @Override
    public int inputSlotsCount()
    {
        return 16;
    }

    @Override
    protected void tickServerFabricator(ServerLevel level, BlockPos pos, BlockState state)
    {
        // Update blueprint status (if necessary)
        if (shouldCheckBlueprint && !isCrafting()) updateBlueprint(level);

        // Check and update crafting status
        if (shouldCheckRecipe && !isCrafting())
        {
            shouldCheckRecipe = false;

            Optional<RecipeHolder<FabricatingRecipe>> optional = getRecipeCheck().getLastUsedRecipe(level);
            boolean check = false;

            if (optional.isPresent())
            {
                FabricatingRecipe recipe = optional.get().value();

                LimaRecipeInput input = createRecipeInput();
                if (canInsertRecipeResults(level, recipe) && recipe.matches(input, level)) // Preliminary check
                {
                    recipe.consumeIngredientsLenientSlots(input, false);
                    check = true; // We consume ingredients here and start crafting. Last used recipe will persist until crafting completes.
                }
            }

            setCrafting(check);
        }

        // Tick recipe progress
        FabricatingRecipe recipe = getRecipeCheck().getLastUsedRecipe(level).map(RecipeHolder::value).orElse(null);
        if (isCrafting() && recipe != null && canInsertRecipeResults(level, recipe))
        {
            // Accumulate energy for recipe
            if (energyCraftProgress < recipe.getEnergyRequired())
            {
                int toExtract = Math.min(getEnergyUsage(), recipe.getEnergyRequired() - energyCraftProgress);
                energyCraftProgress += getEnergyStorage().extractEnergy(toExtract, false);
            }
            else // When done crafting
            {
                getItemHandler().insertItem(FABRICATOR_OUTPUT_SLOT, recipe.generateFabricatingResult(level.random), false);

                // Reset state & check recipe after every craft
                energyCraftProgress = 0;
                setCrafting(false);
                shouldCheckRecipe = true;
            }
        }
        else
        {
            // Reset energy just in case crafting was interrupted, but this shouldn't ever happen.
            energyCraftProgress = 0;
        }
    }

    @Override
    protected ItemLike getValidBlueprintItem()
    {
        return LTXIItems.FABRICATION_BLUEPRINT;
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.AUTO_FABRICATOR.get();
    }

    @Override
    public void onItemSlotChanged(int handlerIndex, int slot)
    {
        super.onItemSlotChanged(handlerIndex, slot);

        if (handlerIndex == 0)
        {
            if (slot == BLUEPRINT_ITEM_SLOT)
            {
                shouldCheckBlueprint = true;
                shouldCheckRecipe = true;
            }
            else if (slot != ENERGY_ITEM_SLOT)
            {
                shouldCheckRecipe = true;
            }
        }
    }
}