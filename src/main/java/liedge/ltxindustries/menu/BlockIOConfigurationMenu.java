package liedge.ltxindustries.menu;

import liedge.limacore.menu.LimaMenu;
import liedge.limacore.menu.LimaMenuType;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.blockentity.base.IOConfigurationRules;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class BlockIOConfigurationMenu extends LimaMenu<BlockIOConfigurationMenu.MenuContext>
{
    public static final int BACK_BUTTON_ID = 0;
    public static final int CYCLE_FORWARD_BUTTON_ID = 1;
    public static final int CYCLE_BACKWARD_BUTTON_ID = 2;
    public static final int TOGGLE_AUTO_INPUT_BUTTON_ID = 3;
    public static final int TOGGLE_AUTO_OUTPUT_BUTTON_ID = 4;

    private BlockIOConfigurationMenu(LimaMenuType<MenuContext, ?> type, int containerId, Inventory inventory, MenuContext menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addDefaultPlayerInventoryAndHotbar();
    }

    public BlockIOConfiguration getIOConfiguration()
    {
        return menuContext.blockEntity.getIOConfigurationOrThrow(menuContext.inputType);
    }

    private void setIOConfiguration(BlockIOConfiguration configuration)
    {
        menuContext.blockEntity.setIOConfiguration(menuContext.inputType, configuration);
    }

    public IOConfigurationRules getIOConfigRules()
    {
        return menuContext.blockEntity.getIOConfigRules(menuContext.inputType);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(AutomaticDataWatcher.keepSynced(LTXINetworkSerializers.BLOCK_IO_CONFIG, this::getIOConfiguration, this::setIOConfiguration));
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        builder.handleUnitAction(BACK_BUTTON_ID, menuContext.blockEntity::returnToPrimaryMenuScreen);
        builder.handleAction(CYCLE_FORWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (sender, side) ->
                setIOConfiguration(getIOConfiguration().cycleIOAccess(side, getIOConfigRules(), true)));
        builder.handleAction(CYCLE_BACKWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (sender, side) ->
                setIOConfiguration(getIOConfiguration().cycleIOAccess(side, getIOConfigRules(), false)));
        builder.handleUnitAction(TOGGLE_AUTO_INPUT_BUTTON_ID, sender -> setIOConfiguration(getIOConfiguration().toggleAutoInput()));
        builder.handleUnitAction(TOGGLE_AUTO_OUTPUT_BUTTON_ID, sender -> setIOConfiguration(getIOConfiguration().toggleAutoOutput()));
    }

    public static final class MenuType extends LimaMenuType<MenuContext, BlockIOConfigurationMenu>
    {
        public MenuType(ResourceLocation id)
        {
            super(MenuContext.class, BlockIOConfigurationMenu::new, defaultMenuTitle(id));
        }

        @Override
        public void encodeContext(MenuContext menuContext, RegistryFriendlyByteBuf net)
        {
            net.writeBlockPos(menuContext.blockEntity.getAsLimaBlockEntity().getBlockPos());
            BlockEntityInputType.STREAM_CODEC.encode(net, menuContext.inputType);
        }

        @Override
        protected MenuContext decodeContext(RegistryFriendlyByteBuf net, Inventory inventory)
        {
            BlockPos pos = net.readBlockPos();
            ConfigurableIOBlockEntity holder = Objects.requireNonNull(LimaBlockUtil.getSafeBlockEntity(inventory.player.level(), pos, ConfigurableIOBlockEntity.class));
            BlockEntityInputType inputType = BlockEntityInputType.STREAM_CODEC.decode(net);

            return new MenuContext(holder, inputType);
        }

        @Override
        public boolean canPlayerKeepUsing(MenuContext menuContext, Player player)
        {
            return menuContext.blockEntity.getAsLimaBlockEntity().canPlayerUse(player);
        }
    }

    public record MenuContext(ConfigurableIOBlockEntity blockEntity, BlockEntityInputType inputType) {}
}