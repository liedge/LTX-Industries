package liedge.ltxindustries.blockentity.template;

import liedge.limacore.blockentity.BlockContentsType;
import liedge.limacore.transfer.item.LimaBlockEntityItems;
import liedge.limacore.util.LimaItemUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntityType;
import liedge.ltxindustries.blockentity.base.UpgradesHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public abstract class MachineBaseBlockEntity extends ConfigurableSidesBlockEntity implements UpgradesHolderBlockEntity
{
    // Recommended standard inventory indices for machines
    public static final int AUX_MODULE_ITEM_SLOT = 0;
    public static final int AUX_ENERGY_ITEM_SLOT = 1;

    private final LimaBlockEntityItems auxInventory;
    private Upgrades upgrades = Upgrades.EMPTY;

    protected MachineBaseBlockEntity(ConfigurableIOBlockEntityType<?> type, BlockPos pos, BlockState state, int auxInventorySize)
    {
        super(type, pos, state);
        this.auxInventory = new LimaBlockEntityItems(this, BlockContentsType.AUXILIARY, auxInventorySize);
    }

    // Item holder defaults

    @Override
    public @Nullable LimaBlockEntityItems getItems(BlockContentsType contentsType)
    {
        return contentsType == BlockContentsType.AUXILIARY ? this.auxInventory : null;
    }

    @Override
    public boolean isItemValid(BlockContentsType contentsType, int index, ItemResource resource)
    {
        return contentsType != BlockContentsType.AUXILIARY || isItemValidForAuxInventory(index, resource);
    }

    protected boolean isItemValidForAuxInventory(int index, ItemResource resource)
    {
        return switch (index)
        {
            case AUX_MODULE_ITEM_SLOT -> resource.is(LTXIItems.UPGRADE_MODULE.asItem()) && resource.has(LTXIDataComponents.UPGRADE_ENTRY);
            case AUX_ENERGY_ITEM_SLOT -> LimaItemUtil.hasEnergyCapability(ItemAccess.forStack(resource.toStack()));
            default -> false;
        };
    }

    // Upgrades holder implementation

    @Override
    public final Upgrades getUpgrades()
    {
        return upgrades;
    }

    @Override
    public final void setUpgrades(Upgrades upgrades)
    {
        this.upgrades = upgrades;

        if (level instanceof ServerLevel serverLevel)
        {
            setChanged();
            onUpgradeRefresh(createUpgradeContext(serverLevel), upgrades);
        }
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components)
    {
        setUpgrades(components.getOrDefault(LTXIDataComponents.UPGRADES, Upgrades.EMPTY));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components)
    {
        components.set(LTXIDataComponents.UPGRADES, upgrades);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output)
    {
        output.discard(LTXIConstants.KEY_UPGRADES_CONTAINER);
    }

    @Override
    protected void loadAdditional(ValueInput input)
    {
        super.loadAdditional(input);

        this.upgrades = input.read(LTXIConstants.KEY_UPGRADES_CONTAINER, Upgrades.CODEC).orElse(Upgrades.EMPTY);
        loadItemResources(input);
    }

    @Override
    protected void saveAdditional(ValueOutput output)
    {
        super.saveAdditional(output);

        output.store(LTXIConstants.KEY_UPGRADES_CONTAINER, Upgrades.CODEC, upgrades);
        saveItemResources(output);
    }

    @Override
    protected void onLoadServer(ServerLevel level)
    {
        super.onLoadServer(level);

        onUpgradeRefresh(createUpgradeContext(level), upgrades);
    }
}