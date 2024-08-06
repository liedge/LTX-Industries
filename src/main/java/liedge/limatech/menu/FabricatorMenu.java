package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaItemHandlerMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.recipe.BaseFabricatingRecipe;
import liedge.limatech.registry.LimaTechNetworkSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

public class FabricatorMenu extends LimaItemHandlerMenu<FabricatorBlockEntity>
{
    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addContextSlot(0, 7, 61);
        addContextRecipeResultSlot(1, 43, 86, LimaTechRecipeTypes.FABRICATING);
        addPlayerInventory(15, 118);
        addPlayerHotbar(15, 176);
    }

    private void receiveCraftCommand(ServerPlayer sender, ResourceLocation id)
    {
        RecipeHolder<BaseFabricatingRecipe> holder = LimaRecipesUtil.recipeHolderByKey(level(), id, LimaTechRecipeTypes.FABRICATING);

        if (holder != null)
        {
            LimaRecipeInput input = LimaRecipeInput.matchingContainer(new PlayerMainInvWrapper(sender.getInventory()));
            menuContext.startCrafting(holder, input, sender.isCreative());
        }
        else
        {
            LimaTech.LOGGER.warn("Received unknown fabricating recipe id '{}' on server.", id);
        }
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(menuContext.getEnergyStorage().createDataWatcher());
        collector.register(menuContext.keepProcessSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleAction(0, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveCraftCommand);
        builder.handleAction(1, LimaTechNetworkSerializers.MACHINE_INPUT_TYPE, menuContext::openIOControlMenuScreen);
    }

    @Override
    protected boolean quickMoveInternal(int slot, ItemStack stack)
    {
        if (slot == 0 || slot == 1)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else if (LimaItemUtil.hasEnergyCapability(stack))
        {
            return quickMoveToContainerSlot(stack, 0);
        }

        return false;
    }
}