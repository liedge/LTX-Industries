package liedge.ltxindustries.menu;

import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.FabricatorBlockEntity;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.Optional;

public class FabricatorMenu extends LTXIMachineMenu.EnergyMachineMenu<FabricatorBlockEntity>
{
    public static final int CRAFT_BUTTON_ID = 2;
    public static final int ENCODE_BLUEPRINT_BUTTON_ID = 3;

    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addHandlerRecipeOutputSlot(context.getOutputInventory(), 0, 42, 86, LTXIRecipeTypes.FABRICATING);
        addHandlerSlot(context.getAuxInventory(), BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 43, 61);

        addPlayerInventoryAndHotbar(15, 118);
    }

    private Optional<RecipeHolder<FabricatingRecipe>> validateRecipeAccess(ServerPlayer sender, ResourceLocation id)
    {
        return LimaRecipesUtil.getRecipeById(sender.level(), id, LTXIRecipeTypes.FABRICATING).filter(holder -> FabricatingRecipe.validateUnlocked(sender.getRecipeBook(), holder, sender))
                .or(() -> {
                    LTXIndustries.LOGGER.warn("Player {} tried to access undefined or locked Fabricating recipe '{}'", sender.getName(), id);
                    return Optional.empty();
                });
    }

    private void receiveCraftCommand(ServerPlayer sender, ResourceLocation id)
    {
        validateRecipeAccess(sender, id).ifPresent(holder -> {
            LimaRecipeInput input = LimaRecipeInput.create(new PlayerMainInvWrapper(sender.getInventory()));
            menuContext.startCrafting(sender.level(), holder, input, sender.isCreative());
        });
    }

    private void receiveEncodeCommand(ServerPlayer sender, ResourceLocation id)
    {
        validateRecipeAccess(sender, id).ifPresent(holder ->
        {
            LimaBlockEntityItemHandler auxInventory = menuContext().getAuxInventory();
            if (auxInventory.getStackInSlot(BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT).is(menuContext.getValidBlueprintItem()))
            {
                ItemStack blueprint = new ItemStack(LTXIItems.FABRICATION_BLUEPRINT.asItem());
                blueprint.set(LTXIDataComponents.BLUEPRINT_RECIPE, id);
                auxInventory.extractItem(BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 1, false);
                ItemHandlerHelper.giveItemToPlayer(sender, blueprint);
            }
        });
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        collector.register(menuContext.keepProgressSynced());
        collector.register(menuContext.getRecipeCheck().createDataWatcher());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(CRAFT_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveCraftCommand);
        builder.handleAction(ENCODE_BLUEPRINT_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveEncodeCommand);
    }

    @Override
    protected boolean quickMoveInternal(int slot, ItemStack stack)
    {
        if (slot < inventoryStart)
        {
            return quickMoveToAllInventory(stack, slot == 1);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
                return quickMoveToContainerSlot(stack, ENERGY_SLOT_INDEX);
            else if (stack.is(menuContext.getValidBlueprintItem()))
                return quickMoveToContainerSlot(stack, 2);
            else return false;
        }
    }
}