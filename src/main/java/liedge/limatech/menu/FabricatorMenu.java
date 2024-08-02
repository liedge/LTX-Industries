package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.slot.LimaMenuSlot;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleDataWatcher;
import liedge.limacore.recipe.LimaSimpleCountIngredient;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechCrafting;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.List;

public class FabricatorMenu extends LimaMenu<FabricatorBlockEntity>
{
    public FabricatorMenu(int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(LimaTechMenus.FABRICATOR.get(), containerId, inventory, context);

        // Slots
        addSlot(new LimaMenuSlot(context.getItemHandler(), 0, 7, 61));
        addRecipeResultSlot(context.getItemHandler(), 1, 43, 86, LimaTechCrafting.FABRICATING_TYPE);
        addPlayerInventory(15, 118);
        addPlayerHotbar(15, 176);
    }

    public void tryStartCrafting(ResourceLocation id)
    {
        RecipeHolder<FabricatingRecipe> holder = LimaCoreUtil.getRecipeByKey(level(), id, LimaTechCrafting.FABRICATING_TYPE);

        if (holder != null)
        {
            if (getUser().isCreative())
            {
                menuContext().startCrafting(holder);
            }
            else
            {
                if (consumeIngredients(holder, true) && menuContext().startCrafting(holder))
                {
                    consumeIngredients(holder, false);
                }
            }
        }
        else
        {
            LimaTech.LOGGER.warn("Received unknown fabricating recipe id '{}' on server.", id);
        }
    }

    private boolean consumeIngredients(RecipeHolder<FabricatingRecipe> holder, boolean simulate)
    {
        IItemHandler invWrapper = new PlayerMainInvWrapper(playerInventory);
        List<IngredientInstance> ingredientInstances = holder.value().getIngredients().stream().map(IngredientInstance::new).toList();

        for (IngredientInstance instance : ingredientInstances)
        {
            // Handle simple count ingredients differently. Because ingredient.test() requires that a slot contains the ingredient's entire count,
            // the check will fail even if the player has the necessary materials split over several inventory slots.
            if (instance.ingredient.getCustomIngredient() instanceof LimaSimpleCountIngredient countIngredient)
            {
                for (int i = 0; i < invWrapper.getSlots(); i++)
                {
                    if (countIngredient.matchesItem(invWrapper.getStackInSlot(i)))
                    {
                        ItemStack extracted = invWrapper.extractItem(i, instance.remaining, simulate);
                        instance.remaining -= extracted.getCount();

                        if (instance.remaining == 0) break;
                    }
                }
            }
            else
            {
                for (int i = 0; i < invWrapper.getSlots(); i++)
                {
                    if (instance.ingredient.test(invWrapper.getStackInSlot(i)))
                    {
                        ItemStack extracted = invWrapper.extractItem(i, instance.remaining, simulate);
                        instance.remaining -= extracted.getCount();

                        if (instance.remaining == 0) break;
                    }
                }
            }

            if (instance.remaining > 0) return false;
        }

        return true;
    }

    @Override
    protected boolean quickMoveInternal(int slot, ItemStack stack)
    {
        if (slot == 0 || slot == 1)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (LimaItemUtil.ENERGY_ITEMS.test(stack))
        {
            return quickMoveToSlot(stack, 0, false);
        }

        return false;
    }

    @Override
    protected List<LimaDataWatcher<?>> defineDataWatchers(FabricatorBlockEntity menuContext)
    {
        return List.of(
                menuContext.getEnergyStorage().createDataWatcher(),
                SimpleDataWatcher.keepSynced(LimaCoreNetworkSerializers.VAR_INT, menuContext::getClientProgress, menuContext::setClientProgress));
    }

    private static class IngredientInstance
    {
        private final Ingredient ingredient;
        private int remaining;

        IngredientInstance(Ingredient ingredient)
        {
            this.ingredient = ingredient;
            this.remaining = ingredient.getItems()[0].getCount();
        }
    }
}