package liedge.ltxindustries.blockentity;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.FixedTimedProcessBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;

public class MolecularReconstructorBlockEntity extends ProductionMachineBlockEntity implements EnergyConsumerBlockEntity, FixedTimedProcessBlockEntity
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
    protected boolean isItemValidForInputInventory(int index, ItemResource resource)
    {
        ItemStack stack = resource.toStack();
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
        LimaBlockEntityItems inputInventory = getItemsOrThrow(BlockContentsType.INPUT);
        LimaBlockEntityItems outputInventory = getItemsOrThrow(BlockContentsType.OUTPUT);

        // Fill internal energy buffer
        pullEnergyFromAux();

        // Try repairing item
        ItemResource inputResource = inputInventory.getResource(0);
        ItemStack inputStack = inputResource.toStack();

        if (inputStack.isDamaged() && !inputStack.is(LTXITags.Items.REPAIR_BLACKLIST))
        {
            if (consumeUsageEnergy())
            {
                currentProcessTime++;

                if (currentProcessTime >= getTicksPerOperation())
                {
                    int oldDamage = inputStack.getDamageValue();
                    int newDamage = Math.max(0, oldDamage - 1);

                    inputStack.set(DataComponents.DAMAGE, newDamage);
                    ItemResource toInsert = ItemResource.of(inputStack);
                    inputInventory.set(0, toInsert, inputStack.getCount());

                    currentProcessTime = 0;
                }
            }
        }
        else
        {
            currentProcessTime = 0;

            if (!inputStack.isEmpty() && outputInventory.getResource(0).isEmpty())
            {
                ResourceHandlerUtil.move(inputInventory, outputInventory, LimaTransferUtil.ALL_ITEMS, Item.DEFAULT_MAX_STACK_SIZE, null);
            }
        }

        // Auto output item
        tickItemAutoOutput(40, outputInventory);
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
        FixedTimedProcessBlockEntity.applyUpgrades(this, context, upgrades);
        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        this.currentProcessTime = input.getIntOr(TAG_KEY_PROGRESS, 0);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putInt(TAG_KEY_PROGRESS, currentProcessTime);
    }
}