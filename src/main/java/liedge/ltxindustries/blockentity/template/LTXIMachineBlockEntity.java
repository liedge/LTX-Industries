package liedge.ltxindustries.blockentity.template;

import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.transfer.energy.EnergyHolderBlockEntity;
import liedge.limacore.transfer.energy.LimaBlockEntityEnergy;
import liedge.limacore.transfer.energy.LimaEnergyHandler;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import org.jspecify.annotations.Nullable;

public abstract class LTXIMachineBlockEntity extends MachineBaseBlockEntity implements EnergyHolderBlockEntity
{
    private final LimaEnergyHandler energy;

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize, @Nullable LimaEnergyHandler energy)
    {
        super(type, pos, state, auxInventorySize);
        this.energy = energy != null ? energy : new LimaBlockEntityEnergy(this);
    }

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, @Nullable LimaEnergyHandler energyStorage)
    {
        this(type, pos, state, 2, energyStorage);
    }

    //#region Energy holder implementation

    @Override
    public final LimaEnergyHandler getEnergy()
    {
        return energy;
    }

    @Override
    public int getBaseEnergyTransferRate()
    {
        return getBaseEnergyCapacity() / 20;
    }

    //#endregion

    //#region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentGetter components)
    {
        super.applyImplicitComponents(components);
        energy.readComponents(components);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        super.collectImplicitComponents(components);
        energy.writeComponents(components);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        super.removeComponentsFromTag(output);
        output.discard(LimaCommonConstants.KEY_ENERGY_CONTAINER);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);
        loadEnergyStorage(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);
        saveEnergyStorage(output);
    }

    //#endregion

    // Misc/Helpers

    protected void pullEnergyFromAux()
    {
        if (!EnergyHandlerUtil.isFull(energy))
        {
            ItemAccess access = ItemAccess.forHandlerIndexStrict(getItemsOrThrow(BlockContentsType.AUXILIARY), AUX_ENERGY_ITEM_SLOT);
            EnergyHandler itemEnergy = access.getCapability(Capabilities.Energy.ITEM);

            if (itemEnergy != null) EnergyHandlerUtil.move(itemEnergy, energy, energy.getTransferRate(), null);
        }
    }

    protected void pushEnergyToSides()
    {
        BlockIOConfiguration configuration = getIOConfigurationOrThrow(BlockEntityInputType.ENERGY);
        if (configuration.autoOutput())
        {
            Direction front = getFacing();
            for (var entry : configuration)
            {
                if (entry.getValue().allowsOutput())
                {
                    EnergyHandler neighborEnergy = getNeighborEnergyHandler(entry.getKey().resolveAbsoluteSide(front));
                    if (neighborEnergy != null) EnergyHandlerUtil.move(energy, neighborEnergy, energy.getTransferRate(), null);
                }
            }
        }
    }

}