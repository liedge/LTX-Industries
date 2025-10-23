package liedge.ltxindustries.blockentity.template;

import com.mojang.serialization.DynamicOps;
import liedge.limacore.LimaCommonConstants;
import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.capability.itemhandler.LimaBlockEntityItemHandler;
import liedge.limacore.capability.itemhandler.LimaItemHandlerUtil;
import liedge.limacore.util.LimaItemUtil;
import liedge.limacore.util.LimaNbtUtil;
import liedge.ltxindustries.blockentity.base.*;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Predicate;

public sealed abstract class LTXIMachineBlockEntity extends LimaBlockEntity implements ConfigurableIOBlockEntity, UpgradesHolderBlockEntity permits EnergyMachineBlockEntity
{
    // Recommended standard inventory indices for machines
    public static final int AUX_MODULE_ITEM_SLOT = 0;
    public static final int AUX_ENERGY_ITEM_SLOT = 1;

    private final ConfigurableIOBlockEntityType<?> type;
    private final Map<Direction, BlockCapabilityCache<IItemHandler, Direction>> itemConnections = new EnumMap<>(Direction.class);

    private final LimaBlockEntityItemHandler auxInventory;
    private BlockIOConfiguration itemIOConfig;
    private MachineUpgrades upgrades = MachineUpgrades.EMPTY;

    private int autoItemOutputTimer;

    protected LTXIMachineBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize)
    {
        super(type, pos, state);

        this.type = type;
        this.itemIOConfig = BlockIOConfiguration.create(type, BlockEntityInputType.ITEMS);
        this.auxInventory = new LimaBlockEntityItemHandler(this, auxInventorySize, BlockContentsType.AUXILIARY);
    }

    public LimaBlockEntityItemHandler getAuxInventory()
    {
        return auxInventory;
    }

    //#region Sided IO implementation

    @Override
    public final Direction getFacing()
    {
        return getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public Collection<BlockEntityInputType> getConfigurableInputTypes()
    {
        return type.getValidInputTypes();
    }

    @Override
    public IOConfigurationRules getIOConfigRules(BlockEntityInputType inputType)
    {
        return type.getIOConfigRules(inputType);
    }

    @Override
    public final @Nullable BlockIOConfiguration getIOConfiguration(BlockEntityInputType inputType)
    {
        return switch (inputType)
        {
            case ITEMS -> getItemIOConfiguration();
            case ENERGY -> getEnergyIOConfiguration();
            case FLUIDS -> getFluidIOConfiguration();
        };
    }

    @Override
    public void setIOConfiguration(BlockEntityInputType inputType, BlockIOConfiguration configuration)
    {
        switch (inputType)
        {
            case ITEMS -> setItemIOConfiguration(configuration);
            case ENERGY -> setEnergyIOConfiguration(configuration);
            case FLUIDS -> setFluidIOConfiguration(configuration);
        }

        if (level != null && !level.isClientSide())
        {
            invalidateCapabilities();
            setChanged();
        }
    }

    @Override
    public IOAccess getSideIOForItems(@Nullable Direction side)
    {
        return side != null ? itemIOConfig.getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    public IOAccess getSideIOForEnergy(@Nullable Direction side)
    {
        return side != null ? getIOConfigurationOrThrow(BlockEntityInputType.ENERGY).getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    public IOAccess getSideIOForFluids(@Nullable Direction side)
    {
        return side != null ? getIOConfigurationOrThrow(BlockEntityInputType.FLUIDS).getIOAccess(getFacing(), side) : IOAccess.DISABLED;
    }

    protected BlockIOConfiguration getItemIOConfiguration()
    {
        return itemIOConfig;
    }

    protected void setItemIOConfiguration(BlockIOConfiguration configuration)
    {
        this.itemIOConfig = configuration;
    }

    protected @Nullable BlockIOConfiguration getEnergyIOConfiguration()
    {
        return null;
    }

    protected void setEnergyIOConfiguration(BlockIOConfiguration configuration) { }

    protected @Nullable BlockIOConfiguration getFluidIOConfiguration()
    {
        return null;
    }

    protected void setFluidIOConfiguration(BlockIOConfiguration configuration) { }

    public @Nullable IItemHandler getNeighborItemHandler(Direction side)
    {
        return itemConnections.get(side).getCapability();
    }

    public @Nullable IEnergyStorage getNeighborEnergyStorage(Direction side)
    {
        return null;
    }

    public @Nullable IFluidHandler getNeighborFluidHandler(Direction side)
    {
        return null;
    }

    protected void createConnectionCaches(ServerLevel level, Direction side)
    {
        itemConnections.put(side, createCapabilityCache(Capabilities.ItemHandler.BLOCK, level, side));
    }

    //#endregion

    //#region Upgrades holder implementation

    @Override
    public MachineUpgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public void setUpgrades(MachineUpgrades upgrades)
    {
        this.upgrades = upgrades;
        if (level instanceof ServerLevel serverLevel)
        {
            setChanged();
            onUpgradeRefresh(createUpgradeContext(serverLevel), upgrades);
        }
    }

    //#endregion

    //#region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput)
    {
        setUpgrades(componentInput.getOrDefault(LTXIDataComponents.MACHINE_UPGRADES, MachineUpgrades.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(LTXIDataComponents.MACHINE_UPGRADES, getUpgrades());
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag)
    {
        tag.remove(TAG_KEY_UPGRADES);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.loadAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

        // Load inventories
        CompoundTag inventoriesTag = tag.getCompound(LimaCommonConstants.KEY_ITEM_CONTAINER);
        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null && inventoriesTag.contains(type.getSerializedName())) handler.deserializeNBT(registries, inventoriesTag.getCompound(type.getSerializedName()));
        }

        // Load upgrades
        this.upgrades = LimaNbtUtil.tryDecode(MachineUpgrades.CODEC, ops, tag, TAG_KEY_UPGRADES, MachineUpgrades.EMPTY);

        // Load IO configurations
        CompoundTag ioConfigsTag = tag.getCompound(KEY_IO_CONFIGS);
        for (BlockEntityInputType inputType : getConfigurableInputTypes())
        {
            BlockIOConfiguration config = LimaNbtUtil.tryDecode(BlockIOConfiguration.CODEC, ops, ioConfigsTag, inputType.getSerializedName());
            if (config != null && config.isValidForRules(type.getIOConfigRules(inputType))) setIOConfiguration(inputType, config);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries)
    {
        super.saveAdditional(tag, registries);
        DynamicOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, registries);

        // Save inventories
        CompoundTag inventoriesTag = new CompoundTag();
        for (BlockContentsType type : BlockContentsType.values())
        {
            LimaBlockEntityItemHandler handler = getItemHandler(type);
            if (handler != null) inventoriesTag.put(type.getSerializedName(), handler.serializeNBT(registries));
        }
        tag.put(LimaCommonConstants.KEY_ITEM_CONTAINER, inventoriesTag);

        // Save upgrades
        LimaNbtUtil.tryEncodeTo(MachineUpgrades.CODEC, ops, upgrades, tag, TAG_KEY_UPGRADES);

        // Save IO configurations
        CompoundTag ioConfigsTag = new CompoundTag();
        for (BlockEntityInputType inputType : getConfigurableInputTypes())
        {
            BlockIOConfiguration configuration = getIOConfigurationOrThrow(inputType);
            LimaNbtUtil.tryEncodeTo(BlockIOConfiguration.CODEC, ops, configuration, ioConfigsTag, inputType.getSerializedName());
        }
        tag.put(KEY_IO_CONFIGS, ioConfigsTag);
    }

    //#endregion

    // Misc/Helpers

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int slot, ItemStack stack)
    {
        return contentsType != BlockContentsType.AUXILIARY || isItemValidForAuxInventory(slot, stack);
    }

    protected boolean isItemValidForAuxInventory(int slot, ItemStack stack)
    {
        return switch (slot)
        {
            case AUX_MODULE_ITEM_SLOT -> stack.is(LTXIItems.MACHINE_UPGRADE_MODULE);
            case AUX_ENERGY_ITEM_SLOT -> LimaItemUtil.hasEnergyCapability(stack);
            default -> false;
        };
    }

    protected void autoOutputItems(IItemHandler internalInventory, Predicate<ItemStack> predicate)
    {
        if (itemIOConfig.autoOutput())
        {
            for (Direction side : Direction.values())
            {
                if (itemIOConfig.getIOAccess(getFacing(), side).allowsOutput())
                {
                    IItemHandler neighborInv = getNeighborItemHandler(side);
                    if (neighborInv != null) LimaItemHandlerUtil.transferItemsBetween(internalInventory, neighborInv, predicate);
                }
            }
        }
    }

    protected void autoOutputItems(int frequency, IItemHandler internalInventory, Predicate<ItemStack> predicate)
    {
        if (autoItemOutputTimer >= frequency)
        {
            autoOutputItems(internalInventory, predicate);
            autoItemOutputTimer = 0;
        }
        else
        {
            autoItemOutputTimer++;
        }
    }

    protected void autoOutputItems(int frequency, IItemHandler internalInventory)
    {
        autoOutputItems(frequency, internalInventory, LimaItemUtil.ALWAYS_TRUE);
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        onUpgradeRefresh(createUpgradeContext(level), getUpgrades());

        for (Direction side : Direction.values())
        {
            createConnectionCaches(level, side);
        }
    }
}