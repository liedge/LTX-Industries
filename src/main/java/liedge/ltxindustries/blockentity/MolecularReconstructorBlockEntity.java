package liedge.ltxindustries.blockentity;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.TimedProcessMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;

public class MolecularReconstructorBlockEntity extends ProductionMachineBlockEntity implements EnergyConsumerBlockEntity, TimedProcessMachineBlockEntity
{
    private int energyUsage = getBaseEnergyUsage();
    private int machineSpeed = getBaseTicksPerOperation();
    private int currentProcessTime;

    public MolecularReconstructorBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MOLECULAR_RECONSTRUCTOR.get(), pos, state, 2, 1, 1);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.REPAIRER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.MOLECULAR_RECONSTRUCTOR.get();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.REPAIRER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getEnergyUsage()
    {
        return energyUsage;
    }

    @Override
    public void setEnergyUsage(int energyUsage)
    {
        this.energyUsage = energyUsage;
    }

    @Override
    protected boolean isItemValidForInputInventory(int slot, ItemStack stack)
    {
        return stack.isDamageableItem() && !stack.is(LTXITags.Items.REPAIR_BLACKLIST);
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        consumer.accept(LTXILangKeys.MACHINE_TICKS_PER_OP_TOOLTIP.translateArgs(getTicksPerOperation()));
        LTXITooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        LimaBlockEntityItemHandler inputInventory = getInputInventory();
        LimaBlockEntityItemHandler outputInventory = getOutputInventory();

        // Fill internal energy buffer
        fillEnergyBuffer();

        // Try repairing item
        ItemStack inputItem = inputInventory.getStackInSlot(0);
        if (inputItem.isDamaged() && !inputItem.is(LTXITags.Items.REPAIR_BLACKLIST))
        {
            if (LimaEnergyUtil.consumeEnergy(getEnergyStorage(), getEnergyUsage(), false))
            {
                currentProcessTime++;

                if (currentProcessTime >= getTicksPerOperation())
                {
                    ItemStack stack = inputItem.copy();

                    int oldDamage = stack.getOrDefault(DataComponents.DAMAGE, 0);
                    int newDamage = Math.max(0, oldDamage - 1);

                    stack.set(DataComponents.DAMAGE, newDamage);
                    inputInventory.setStackInSlot(0, stack);

                    currentProcessTime = 0;
                }
            }
        }
        else
        {
            currentProcessTime = 0;

            if (!inputItem.isEmpty() && outputInventory.getStackInSlot(0).isEmpty())
            {
                ItemStack extracted = inputInventory.extractItem(0, outputInventory.getSlotLimit(0), false);
                outputInventory.insertItem(0, extracted, false);
            }
        }

        // Auto output item
        autoOutputItems(40, outputInventory);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) {}

    @Override
    public int getCurrentProcessTime()
    {
        return currentProcessTime;
    }

    @Override
    public void setCurrentProcessTime(int currentProcessTime)
    {
        this.currentProcessTime = currentProcessTime;
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.REPAIRER_OPERATION_TIME.getAsInt();
    }

    @Override
    public int getTicksPerOperation()
    {
        return machineSpeed;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.machineSpeed = ticksPerOperation;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, MachineUpgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);
        TimedProcessMachineBlockEntity.applyUpgrades(this, context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_KEY_PROGRESS, currentProcessTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        this.currentProcessTime = tag.getInt(TAG_KEY_PROGRESS);
    }
}