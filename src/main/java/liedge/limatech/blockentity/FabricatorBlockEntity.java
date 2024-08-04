package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.lib.IODirection;
import liedge.limacore.lib.energy.LimaEnergyStorage;
import liedge.limacore.lib.energy.LimaEnergyUtil;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.SimpleDataWatcher;
import liedge.limacore.recipe.LimaRecipeReference;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaMathUtil;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechCrafting;
import liedge.limatech.registry.LimaTechMenus;
import liedge.limatech.util.config.LimaTechServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import java.util.List;

import static liedge.limacore.util.LimaNbtUtil.deserializeString;

public class FabricatorBlockEntity extends MachineBlockEntity implements RecipeInput
{
    private final LimaRecipeReference<FabricatingRecipe> currentRecipe = new LimaRecipeReference<>(LimaTechCrafting.FABRICATING_TYPE);

    private boolean crafting = false;
    private int energyUsedForRecipe;
    private int clientProgress;
    private ItemStack previewItem = ItemStack.EMPTY;

    public FabricatorBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, LimaTechServerConfig.fabricatorEnergyCapacity(), LimaTechServerConfig.fabricatorEnergyIORate(), 2);
    }

    public int getClientProgress()
    {
        if (checkServerSide())
        {
            return currentRecipe.toIntOrElse(level, r -> (int) (LimaMathUtil.divideFloat(energyUsedForRecipe, r.getEnergyRequired()) * 100f), 0);
        }
        else
        {
            return clientProgress;
        }
    }

    public ItemStack getPreviewItem()
    {
        return previewItem;
    }

    public void setClientProgress(int clientProgress)
    {
        this.clientProgress = clientProgress;
    }

    public boolean isCrafting()
    {
        return crafting;
    }

    private void setCrafting(boolean crafting)
    {
        this.crafting = crafting;
        setChanged();
    }

    public boolean startCrafting(RecipeHolder<FabricatingRecipe> holder)
    {
        if (!isCrafting() && LimaItemUtil.canCombineStacks(getItemHandler().getStackInSlot(1), holder.value().getResultItem(nonNullRegistryAccess())))
        {
            setCrafting(true);
            currentRecipe.setHolderValue(holder);
            return true;
        }

        return false;
    }

    private void stopCrafting(boolean insertResult)
    {
        FabricatingRecipe recipe = currentRecipe.getRecipeValue(level);

        if (insertResult && recipe != null)
        {
            getItemHandler().insertItem(1, recipe.assemble(this, nonNullRegistryAccess()), false);
        }

        energyUsedForRecipe = 0;
        setCrafting(false);
        currentRecipe.setHolderValue(null);
    }

    @Override
    protected void tickServer(Level level, BlockPos pos, BlockState state)
    {
        LimaEnergyStorage machineEnergy = getEnergyStorage();
        if (machineEnergy.getEnergyStored() < machineEnergy.getMaxEnergyStored())
        {
            IEnergyStorage itemEnergy = getItemHandler().getStackInSlot(0).getCapability(Capabilities.EnergyStorage.ITEM);
            if (itemEnergy != null)
            {
                LimaEnergyUtil.transferEnergyBetween(itemEnergy, machineEnergy, machineEnergy.getTransferRate(), false);
            }
        }

        FabricatingRecipe recipe = currentRecipe.getRecipeValue(level);
        if (isCrafting() && recipe != null)
        {
            int totalEnergyRequired = recipe.getEnergyRequired();

            if (energyUsedForRecipe < totalEnergyRequired)
            {
                int energyRemainingNeeded = (totalEnergyRequired - energyUsedForRecipe);
                energyUsedForRecipe += machineEnergy.extractEnergy(energyRemainingNeeded, false, false);
            }
            else
            {
                stopCrafting(true);
            }
        }
        else
        {
            stopCrafting(false);
        }
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.FABRICATOR.get();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return slot != 0 || LimaItemUtil.ENERGY_ITEMS.test(stack);
    }

    @Override
    public ItemStack getItem(int slot)
    {
        return ItemStack.EMPTY;
    }

    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public IODirection getIOForSlot(int slot)
    {
        return slot == 0 ? IODirection.INPUT_ONLY : IODirection.OUTPUT_ONLY;
    }

    @Override
    protected List<LimaDataWatcher<?>> defineDataWatchers()
    {
        return List.of(
                SimpleDataWatcher.keepSynced(LimaCoreNetworkSerializers.BOOL, this::isCrafting, this::setCrafting),
                SimpleDataWatcher.keepItemSynced(() -> currentRecipe.mapOrElse(level, r -> r.getResultItem(nonNullRegistryAccess()), getItemHandler().getStackInSlot(1).copy()), stack -> this.previewItem = stack));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        deserializeString(currentRecipe, registries, tag.get("current_recipe"));
        crafting = tag.getBoolean("crafting");
        energyUsedForRecipe = tag.getInt("recipe_energy");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.put("current_recipe", currentRecipe.serializeNBT(registries));
        tag.putBoolean("crafting", crafting);
        tag.putInt("recipe_energy", energyUsedForRecipe);
    }
}