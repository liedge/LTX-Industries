package liedge.ltxindustries.blockentity;

import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.GardenSimulatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class DigitalGardenBlockEntity extends LTXIRecipeMachineBlockEntity<GardenSimulatingRecipe>
{
    private ItemStack clientPreviewItem = ItemStack.EMPTY;

    public DigitalGardenBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.DIGITAL_GARDEN.get(), LTXIRecipeTypes.GARDEN_SIMULATING.get(), pos, state, 1, 4, 1, 0);
    }

    public ItemStack getClientPreviewItem()
    {
        return clientPreviewItem;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LimaCoreNetworkSerializers.ITEM_RESOURCE, this::writePreviewResource, this::readPreviewResource));
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

    private ItemResource writePreviewResource()
    {
        ItemResource resource = getOutputInventory().getResource(0);

        if (isCrafting() && level instanceof ServerLevel serverLevel)
        {
            return getRecipeCheck().getLastUsedRecipe(serverLevel).map(holder -> holder.value().getFirstItemResult().getResource()).orElse(resource);
        }

        return resource;
    }

    private void readPreviewResource(ItemResource resource)
    {
        this.clientPreviewItem = resource.toStack();
    }
}