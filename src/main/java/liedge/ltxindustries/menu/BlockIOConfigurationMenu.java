package liedge.ltxindustries.menu;

import liedge.limacore.blockentity.LimaBlockEntity;
import liedge.limacore.menu.BlockEntityMenu;
import liedge.limacore.network.sync.SimpleValueTracker;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.blockentity.base.IORules;
import liedge.ltxindustries.blockentity.base.ResourceType;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class BlockIOConfigurationMenu extends BlockEntityMenu<ConfigurableIOBlockEntity>
{
    public static final int CYCLE_FORWARD_BUTTON_ID = 1;
    public static final int CYCLE_BACKWARD_BUTTON_ID = 2;
    public static final int TOGGLE_AUTO_INPUT_BUTTON_ID = 3;
    public static final int TOGGLE_AUTO_OUTPUT_BUTTON_ID = 4;

    private final ResourceType resourceType;

    public BlockIOConfigurationMenu(int containerId, Inventory inventory, ConfigurableIOBlockEntity blockEntity, ResourceType resourceType)
    {
        super(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), containerId, inventory, blockEntity);
        this.resourceType = resourceType;

        addDefaultPlayerInventoryAndHotbar();

        addDataWatcher(SimpleValueTracker.create(LTXINetworkSerializers.BLOCK_IO_CONFIG, this::getIOConfiguration, this::setConfig).setAutomatic());

        handleUnitButton(SharedMenuButtons.EXIT_SUB_MENU, blockEntity::returnToPrimaryMenuScreen);
        handleButton(CYCLE_FORWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (_, side) ->
                setConfigLogged(getIOConfiguration().cycleIOAccess(side, getIOConfigRules(), true)));
        handleButton(CYCLE_BACKWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (_, side) ->
                setConfigLogged(getIOConfiguration().cycleIOAccess(side, getIOConfigRules(), false)));
        handleUnitButton(TOGGLE_AUTO_INPUT_BUTTON_ID, _ -> setConfigLogged(getIOConfiguration().toggleAutoInput()));
        handleUnitButton(TOGGLE_AUTO_OUTPUT_BUTTON_ID, _ -> setConfigLogged(getIOConfiguration().toggleAutoOutput()));
    }

    public BlockIOConfigurationMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf net)
    {
        this(containerId, inventory, decodeBlockEntity(net, inventory, ConfigurableIOBlockEntity.class), ResourceType.STREAM_CODEC.decode(net));
    }

    public BlockIOConfiguration getIOConfiguration()
    {
        return menuContext.getIOConfigurationOrThrow(resourceType);
    }

    private boolean setConfig(BlockIOConfiguration configuration)
    {
        return menuContext.setIOConfiguration(resourceType, configuration);
    }

    private void setConfigLogged(BlockIOConfiguration configuration)
    {
        if (!setConfig(configuration))
        {
            LimaBlockEntity be = menuContext.getAsLimaBlockEntity();
            LTXIndustries.LOGGER.warn("Attempted to apply an invalid IO configuration in menu screen for block entity type {} at {}. {} Configuration: {}",
                    LimaRegistryUtil.getNonNullRegistryId(be.getType(), BuiltInRegistries.BLOCK_ENTITY_TYPE),
                    be.getBlockPos(),
                    resourceType.getSerializedName(),
                    configuration);
        }
    }

    public IORules getIOConfigRules()
    {
        return menuContext.getIOConfigRules(resourceType);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        return false;
    }
}