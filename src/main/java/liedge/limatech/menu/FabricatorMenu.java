package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

public class FabricatorMenu extends SidedUpgradableMachineMenu<FabricatorBlockEntity>
{
    public static final int CRAFT_BUTTON_ID = 2;

    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addSlot(0, 7, 61);
        addRecipeResultSlot(1, 43, 86, LimaTechRecipeTypes.FABRICATING);
        addPlayerInventoryAndHotbar(15, 118);
    }

    private void receiveCraftCommand(ServerPlayer sender, ResourceLocation id)
    {
        RecipeHolder<FabricatingRecipe> holder = LimaRecipesUtil.recipeHolderByKey(level(), id, LimaTechRecipeTypes.FABRICATING);

        if (holder != null)
        {
            LimaRecipeInput input = LimaRecipeInput.matchingContainer(new PlayerMainInvWrapper(sender.getInventory()));
            menuContext.startCrafting(holder, input, sender.isCreative());
        }
        else
        {
            LimaTech.LOGGER.warn("Received unknown fabricating recipe iconPath '{}' on server.", id);
        }
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        collector.register(menuContext.keepProcessSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(CRAFT_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveCraftCommand);
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