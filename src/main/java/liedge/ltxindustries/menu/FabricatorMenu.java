package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.recipe.SimpleResourceAccess;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.FabricatorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.PlayerInventoryWrapper;
import net.neoforged.neoforge.transfer.transaction.Transaction;

public class FabricatorMenu extends LTXIMachineMenu<FabricatorBlockEntity>
{
    public static final int CRAFT_BUTTON_ID = 2;
    public static final int ENCODE_BLUEPRINT_BUTTON_ID = 3;

    public FabricatorMenu(LimaMenuType<FabricatorBlockEntity, ?> type, int containerId, Inventory inventory, FabricatorBlockEntity context)
    {
        super(type, containerId, inventory, context);

        // Slots
        addOutputSlot(0, 42, 86);
        addSlot(BlockContentsType.AUXILIARY, BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 43, 61);

        //addRecipeOutputSlot(0, 42, 86, menuContext().getRecipeCheck().getRecipeType());
        //addSlot(BlockContentsType.AUXILIARY, BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 43, 61, stack -> stack.is(menuContext().getValidBlueprintItem()));

        addPlayerInventoryAndHotbar(15, 118);
    }

    private void receiveCraftCommand(ServerPlayer sender, ResourceKey<Recipe<?>> key)
    {
        ServerLevel level = sender.level();

        LimaRegistryUtil.getRecipeByKey(level, key, LTXIRecipeTypes.FABRICATING).ifPresent(holder ->
        {
            SimpleResourceAccess access = new SimpleResourceAccess(PlayerInventoryWrapper.of(sender), null);
            menuContext.startCrafting(level, holder, access, sender.isCreative());
        });
    }

    private void receiveEncodeCommand(ServerPlayer sender, ResourceKey<Recipe<?>> key)
    {
        ServerLevel level = sender.level();
        LimaRegistryUtil.getRecipeByKey(level, key, LTXIRecipeTypes.FABRICATING).ifPresent(_ ->
        {
            LimaBlockEntityItems auxInventory = menuContext.getItemsOrThrow(BlockContentsType.AUXILIARY);
            Item blankBPItem = menuContext.getValidBlueprintItem();

            if (auxInventory.getResource(BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT).is(blankBPItem))
            {
                ItemStack blueprint = new ItemStack(LTXIItems.FABRICATION_BLUEPRINT.asItem());
                blueprint.set(LTXIDataComponents.BLUEPRINT_RECIPE, key);

                int extracted;
                try (Transaction tx = Transaction.openRoot())
                {
                    extracted = auxInventory.extract(BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, ItemResource.of(blankBPItem), 1, tx);
                    tx.commit();
                }

                if (extracted > 0)
                {
                    sender.getInventory().placeItemBackInInventory(blueprint);
                }
            }
        });
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergy().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        collector.register(menuContext.keepProgressSynced());
        collector.register(menuContext.getRecipeCheck().keepLastUsedSynced());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        super.defineButtonEventHandlers(builder);
        builder.handleAction(CRAFT_BUTTON_ID, LimaCoreNetworkSerializers.RECIPE_KEY, this::receiveCraftCommand);
        builder.handleAction(ENCODE_BLUEPRINT_BUTTON_ID, LimaCoreNetworkSerializers.RECIPE_KEY, this::receiveEncodeCommand);
    }
}