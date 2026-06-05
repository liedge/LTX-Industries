package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.transfer.fluid.FluidHolderBlockEntity;
import liedge.limacore.transfer.fluid.LimaBlockEntityFluids;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.lib.upgrades.EffectRankPair;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.lib.upgrades.effect.MinimumMachineSpeed;
import liedge.ltxindustries.lib.upgrades.effect.ValueOperation;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.function.IntUnaryOperator;

public abstract class ProductionMachineBlockEntity extends LTXIMachineBlockEntity implements FluidHolderBlockEntity
{
    public static final int INPUT_TANK_CAPACITY = 32_000;
    public static final int OUTPUT_TANK_CAPACITY = 64_000;

    private final @Nullable LimaBlockEntityItems inputInventory;
    private final @Nullable LimaBlockEntityItems outputInventory;

    private final @Nullable LimaBlockEntityFluids inputFluids;
    private final @Nullable LimaBlockEntityFluids outputFluids;

    protected ProductionMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, int inputSlots, int outputSlots, int inputTanks, int outputTanks)
    {
        super(type, pos, state, auxInventorySize, null);

        this.inputInventory = inputSlots > 0 ? new LimaBlockEntityItems(this, BlockContentsType.INPUT, inputSlots) : null;
        this.outputInventory = outputSlots > 0 ? new LimaBlockEntityItems(this, BlockContentsType.OUTPUT, outputSlots) : null;
        this.inputFluids = inputTanks > 0 ? new LimaBlockEntityFluids(this, BlockContentsType.INPUT, inputTanks) : null;
        this.outputFluids = outputTanks > 0 ? new LimaBlockEntityFluids(this, BlockContentsType.OUTPUT, outputTanks) : null;
    }

    protected ProductionMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, int inputSlots, int outputSlots)
    {
        this(type, pos, state, auxInventorySize, inputSlots, outputSlots, 0, 0);
    }

    //#region Item holder impl

    public LimaBlockEntityItems getInputInventory()
    {
        return getItemsOrThrow(BlockContentsType.INPUT);
    }

    public LimaBlockEntityItems getOutputInventory()
    {
        return getItemsOrThrow(BlockContentsType.OUTPUT);
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType == BlockContentsType.INPUT ? isItemValidForInputInventory(index, resource) : super.isItemValid(contentsType, index, resource);
    }

    protected boolean isItemValidForInputInventory(int index, ItemResource resource)
    {
        return true;
    }

    @Override
    public @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case INPUT -> inputInventory;
            case OUTPUT -> outputInventory;
            default -> super.getItems(contentsType);
        };
    }

    //#endregion

    //#region Fluid holder impl

    @Override
    public @Nullable LimaBlockEntityFluids getFluids(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case INPUT -> inputFluids;
            case OUTPUT -> outputFluids;
            default -> null;
        };
    }

    @Override
    public int getBaseFluidCapacity(BlockContentsType contentsType)
    {
        return switch (contentsType)
        {
            case INPUT -> INPUT_TANK_CAPACITY;
            case OUTPUT -> OUTPUT_TANK_CAPACITY;
            default -> 0;
        };
    }

    @Override
    public int getBaseFluidTransferRate(BlockContentsType contentsType)
    {
        return getBaseFluidCapacity(contentsType) / 4;
    }

    //#endregion

    // Startup and Serialization
    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        loadFluidResources(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        saveFluidResources(output);
    }

    // Helper
    protected static IntUnaryOperator createCachedSpeedFunction(Upgrades upgrades, LootContext context)
    {
        List<EffectRankPair<ValueOperation>> list = upgrades.effectPairs(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION)
                .sorted(MathOperation.comparingPriority(o -> o.effect().operation()))
                .toList();
        if (list.isEmpty()) return IntUnaryOperator.identity();

        final int minSpeed = upgrades.effectStream(LTXIUpgradeEffectComponents.MINIMUM_MACHINE_SPEED).mapToInt(MinimumMachineSpeed::minimumSpeed).min().orElse(0);
        return base ->
        {
            if (base <= minSpeed) return base;

            double total = base;
            for (EffectRankPair<ValueOperation> pair : list)
            {
                total = pair.effect().apply(context, pair.upgradeRank(), base, total);
            }

            return Math.max(minSpeed, Mth.floor(total));
        };
    }
}