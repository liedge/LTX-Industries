package liedge.limatech.menu;

import liedge.limacore.capability.itemhandler.LimaItemHandlerBase;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.BaseFabricatorBlockEntity;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

import java.util.Optional;

import static liedge.limatech.blockentity.BaseFabricatorBlockEntity.BLUEPRINT_ITEM_SLOT;
import static liedge.limatech.blockentity.BaseFabricatorBlockEntity.ENERGY_ITEM_SLOT;

public class FabricatorMenu extends SidedUpgradableMachineMenu<FabricatorBlockEntity>
{
    public static final int CRAFT_BUTTON_ID = 2;
    public static final int ENCODE_BLUEPRINT_BUTTON_ID = 3;

    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addSlot(ENERGY_ITEM_SLOT, 8, 61);
        addRecipeResultSlot(BaseFabricatorBlockEntity.OUTPUT_SLOT, 43, 86, LimaTechRecipeTypes.FABRICATING);
        addSlot(BLUEPRINT_ITEM_SLOT, 43, 61);

        addPlayerInventoryAndHotbar(15, 118);
    }

    private Optional<RecipeHolder<FabricatingRecipe>> validateRecipeAccess(ServerPlayer sender, ResourceLocation id)
    {
        return LimaRecipesUtil.getRecipeById(sender.level(), id, LimaTechRecipeTypes.FABRICATING).filter(holder -> FabricatingRecipe.validateUnlocked(sender.getRecipeBook(), holder, sender))
                .or(() -> {
                    LimaTech.LOGGER.warn("Player {} tried to access undefined or locked Fabricating recipe '{}'", sender.getName(), id);
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
            LimaItemHandlerBase beInv = menuContext.getItemHandler();
            if (beInv.getStackInSlot(BLUEPRINT_ITEM_SLOT).is(LimaTechItems.EMPTY_FABRICATION_BLUEPRINT))
            {
                ItemStack blueprint = new ItemStack(LimaTechItems.FABRICATION_BLUEPRINT.asItem());
                blueprint.set(LimaTechDataComponents.BLUEPRINT_RECIPE, id);
                beInv.extractItem(BLUEPRINT_ITEM_SLOT, 1, false);
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
        if (slot == BaseFabricatorBlockEntity.OUTPUT_SLOT)
        {
            return quickMoveToAllInventory(stack, true);
        }
        else if (slot < 3)
        {
            return quickMoveToAllInventory(stack, false);
        }
        else
        {
            if (LimaItemUtil.hasEnergyCapability(stack))
                return quickMoveToContainerSlot(stack, ENERGY_ITEM_SLOT);
            else if (stack.is(LimaTechItems.EMPTY_FABRICATION_BLUEPRINT))
                return quickMoveToContainerSlot(stack, BLUEPRINT_ITEM_SLOT);
            else return false;
        }
    }
}