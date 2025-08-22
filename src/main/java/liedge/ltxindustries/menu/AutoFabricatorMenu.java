package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.menu.LimaMenuType;
import liedge.ltxindustries.blockentity.AutoFabricatorBlockEntity;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.Predicate;

public class AutoFabricatorMenu extends LTXIMachineMenu.RecipeEnergyMachineMenu<AutoFabricatorBlockEntity>
{
    public AutoFabricatorMenu(LimaMenuType<AutoFabricatorBlockEntity, ?> type, int containerId, Inventory inventory, AutoFabricatorBlockEntity menuContext)
    {
        super(type, containerId, inventory, menuContext);

        final Predicate<ItemStack> blueprintPredicate = stack -> stack.is(menuContext.getValidBlueprintItem()) && !Objects.isNull(stack.get(LTXIDataComponents.BLUEPRINT_RECIPE));
        addSlotsGrid(BlockContentsType.INPUT, 0, 33, 30, 8, 2, blueprintPredicate.negate());
        addSlot(BlockContentsType.AUXILIARY, BaseFabricatorBlockEntity.AUX_BLUEPRINT_SLOT, 120, 73, blueprintPredicate);
        addRecipeOutputSlot(0, 152, 73);
        addPlayerInventoryAndHotbar(15, 98);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        menuContext.getEnergyStorage().keepAllPropertiesSynced(collector);
        menuContext.keepEnergyConsumerPropertiesSynced(collector);
        collector.register(menuContext.keepProgressSynced());
        collector.register(menuContext.getRecipeCheck().createDataWatcher());
    }
}