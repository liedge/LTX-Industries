package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.FabricatorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.PlayerMainInvWrapper;

public class FabricatorMenu extends LTXIMachineMenu.RecipeEnergyMachineMenu<FabricatorBlockEntity>
{
    public static final int CRAFT_BUTTON_ID = 2;
    public static final int ENCODE_BLUEPRINT_BUTTON_ID = 3;

    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addRecipeOutputSlot(0, 42, 86);
        addSlot(BlockContentsType.AUXILIARY, BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 43, 61, stack -> stack.is(menuContext().getValidBlueprintItem()));

        addPlayerInventoryAndHotbar(15, 118);
    }

    private void receiveCraftCommand(ServerPlayer sender, ResourceLocation id)
    {
        LimaRecipesUtil.getRecipeById(sender.level(), id, LTXIRecipeTypes.FABRICATING).ifPresent(holder -> menuContext.startCrafting(sender.level(), holder, LimaRecipeInput.create(new PlayerMainInvWrapper(sender.getInventory()), null), sender.isCreative()));
    }

    private void receiveEncodeCommand(ServerPlayer sender, ResourceLocation id)
    {
        LimaRecipesUtil.getRecipeById(sender.level(), id, LTXIRecipeTypes.FABRICATING).ifPresent(holder ->
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
        collector.register(menuContext.getRecipeCheck().keepLastUsedSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(CRAFT_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveCraftCommand);
        builder.handleAction(ENCODE_BLUEPRINT_BUTTON_ID, LimaCoreNetworkSerializers.RESOURCE_LOCATION, this::receiveEncodeCommand);
    }
}