package liedge.ltxindustries.blockentity;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.GardenSimulatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalGardenBlockEntity extends LTXIRecipeMachineBlockEntity<GardenSimulatingRecipe>
{
    private ItemStack previewItem = ItemStack.EMPTY;

    public DigitalGardenBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.DIGITAL_GARDEN.get(), LTXIRecipeTypes.GARDEN_SIMULATING.get(), pos, state, 1, 4, 1, 0);
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepItemSynced(this::createPreviewItem, stack -> this.previewItem = stack));
    }

    @Override
    protected void onCraftingStateChanged(boolean newCraftingState) { }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.DIGITAL_GARDEN_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.DIGITAL_GARDEN_ENERGY_USAGE.getAsInt();
    }

    private ItemStack createPreviewItem()
    {
        if (isCrafting())
        {
            return getRecipeCheck().getLastUsedRecipe(level).map(r -> r.value().getFirstItemResult().item()).orElse(ItemStack.EMPTY);
        }

        return ItemStack.EMPTY;
    }
}