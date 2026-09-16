package liedge.ltxindustries.blockentity;

import com.google.common.base.Predicates;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.base.EnergyConsumerBlockEntity;
import liedge.ltxindustries.blockentity.base.TimedProcessBlockEntity;
import liedge.ltxindustries.blockentity.template.ProductionMachineBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.LTXITooltipUtil;
import liedge.ltxindustries.util.LTXIUpgradeUtil;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import java.util.function.IntUnaryOperator;

public class RepairStationBlockEntity extends ProductionMachineBlockEntity implements EnergyConsumerBlockEntity, TimedProcessBlockEntity
{
    private static final String KEY_SNAPSHOT = "input_snapshot";

    private int energyUsage;
    private ItemStack repairItemSnapshot = ItemStack.EMPTY;
    private IntUnaryOperator repairTimeFunction = IntUnaryOperator.identity();
    private int repairTime;
    private boolean active;
    private int currentProcessTime;

    private boolean checkItem;
    private boolean checkProcessTime;

    public RepairStationBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.REPAIR_STATION.get(), pos, state, 2, 1, 1);
    }

    private boolean canRepairItem(ItemStack stack)
    {
        return stack.isDamaged() && !stack.is(LTXITags.Items.REPAIR_BLACKLIST);
    }

    @Override
    public void appendStatsTooltips(TooltipLineConsumer consumer)
    {
        LTXITooltipUtil.appendEnergyUsagePerTickTooltip(consumer, getEnergyUsage());
    }

    @Override
    protected boolean isItemValidForInputInventory(int index, ItemResource resource)
    {
        return canRepairItem(resource.toStack());
    }

    @Override
    public void onEnergyChanged(int previousAmount)
    {
        setChanged();
        if (previousAmount < getEnergyUsage() && hasMinimumEnergy()) checkItem = true;
    }

    @Override
    public void onItemChanged(BlockContentsType contentsType, int index, ItemStack previousContents)
    {
        setChanged();

        if (contentsType == BlockContentsType.INPUT || (contentsType == BlockContentsType.OUTPUT && !active))
        {
            checkItem = true;
        }
    }

    @Override
    protected void tickServer(ServerLevel level, BlockPos pos, BlockState state)
    {
        pullEnergyFromAux();

        LimaBlockEntityItems inputInventory = getItemsOrThrow(BlockContentsType.INPUT);
        LimaBlockEntityItems outputInventory = getItemsOrThrow(BlockContentsType.OUTPUT);

        if (checkItem)
        {
            ItemStack itemToRepair = inputInventory.getResource(0).toStack();

            boolean isValid = false;

            if (canRepairItem(itemToRepair))
            {
                if (!ItemStack.isSameItemSameComponents(itemToRepair, repairItemSnapshot))
                {
                    currentProcessTime = 0;
                    checkProcessTime = true;
                }

                isValid = outputInventory.getResource(0).isEmpty();
                if (isValid && checkProcessTime)
                {
                    int repairTime = Math.min(5 * itemToRepair.getDamageValue(), 4800);

                    setTicksPerOperation(repairTimeFunction.applyAsInt(repairTime));
                    checkProcessTime = false;
                }
            }

            if (!isValid) currentProcessTime = 0;

            repairItemSnapshot = itemToRepair.copy();
            checkItem = false;
            setActive(isValid && hasMinimumEnergy());
        }

        if (active)
        {
            if (consumeUsageEnergy())
            {
                currentProcessTime++;

                if (currentProcessTime >= getTicksPerOperation())
                {
                    try (Transaction tx = Transaction.openRoot())
                    {
                        boolean repaired = false;
                        ResourceStack<ItemResource> toRepair = ResourceHandlerUtil.extractFirst(inputInventory, Predicates.alwaysTrue(), 1, tx);

                        if (toRepair != null && !toRepair.isEmpty())
                        {
                            ItemStack stack = toRepair.resource().toStack();
                            if (ItemStack.isSameItemSameComponents(stack, repairItemSnapshot))
                            {
                                stack.set(DataComponents.DAMAGE, 0);
                                repaired = outputInventory.insert(ItemResource.of(stack), 1, tx) > 0;
                            }
                        }

                        if (repaired) tx.commit();
                    }

                    currentProcessTime = 0;
                    checkItem = true;
                }
            }
            else
            {
                setActive(false);
            }
        }

        tickAutoResourceInput(40, inputInventory, null);
        tickAutoResourceOutput(40, outputInventory, null);
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector) { }

    @Override
    public boolean isActive()
    {
        return active;
    }

    @Override
    public void setActive(boolean active)
    {
        if (this.active != active)
        {
            setChanged();
            this.active = active;
        }

        LTXIBlockProperties.updateBinaryState(nonNullLevel(), getBlockPos(), getBlockState(), active);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.REPAIR_STATION_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.REPAIR_STATION_ENERGY_USAGE.getAsInt();
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
    public int getTicksPerOperation()
    {
        return repairTime;
    }

    @Override
    public void setTicksPerOperation(int ticksPerOperation)
    {
        this.repairTime = ticksPerOperation;
    }

    @Override
    public void onUpgradeRefresh(LootContext context, Upgrades upgrades)
    {
        super.onUpgradeRefresh(context, upgrades);

        EnergyConsumerBlockEntity.applyUpgrades(this, context, upgrades);
        this.repairTimeFunction = LTXIUpgradeUtil.createMachineSpeedFunction(upgrades, context);

        this.checkItem = true;
        this.checkProcessTime = true;
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        currentProcessTime = input.getIntOr(TAG_KEY_PROGRESS, 0);
        repairItemSnapshot = input.read(KEY_SNAPSHOT, ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        output.putInt(TAG_KEY_PROGRESS, currentProcessTime);
        output.store(KEY_SNAPSHOT, ItemStack.OPTIONAL_CODEC, repairItemSnapshot);
    }
}