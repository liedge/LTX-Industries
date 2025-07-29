package liedge.ltxindustries.menu;

import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.registry.game.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.IOController;
import liedge.ltxindustries.blockentity.base.SidedAccessBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

import java.util.Objects;

public class IOControllerMenu extends LimaMenu<IOControllerMenu.MenuContext>
{
    public static final int BACK_BUTTON_ID = 0;
    public static final int CYCLE_FORWARD_BUTTON_ID = 1;
    public static final int CYCLE_BACKWARD_BUTTON_ID = 2;
    public static final int TOGGLE_AUTO_INPUT_BUTTON_ID = 3;
    public static final int TOGGLE_AUTO_OUTPUT_BUTTON_ID = 4;

    private IOControllerMenu(LimaMenuType<MenuContext, ?> type, int containerId, Inventory inventory, MenuContext menuContext)
    {
        super(type, containerId, inventory, menuContext);

        addDefaultPlayerInventoryAndHotbar();
    }

    public IOController getIOControl()
    {
        return menuContext.blockEntity.getIOController(menuContext.inputType);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(getIOControl().getOrCreateDataWatcher());
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        IOController ioControl = getIOControl();
        builder.handleUnitAction(BACK_BUTTON_ID, menuContext.blockEntity::returnToPrimaryMenuScreen);
        builder.handleAction(CYCLE_FORWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (sender, side) -> ioControl.cycleSideIOState(side, true));
        builder.handleAction(CYCLE_BACKWARD_BUTTON_ID, LimaCoreNetworkSerializers.RELATIVE_SIDE, (sender, side) -> ioControl.cycleSideIOState(side, false));
        builder.handleUnitAction(TOGGLE_AUTO_INPUT_BUTTON_ID, sender -> ioControl.toggleAutoInput());
        builder.handleUnitAction(TOGGLE_AUTO_OUTPUT_BUTTON_ID, sender -> ioControl.toggleAutoOutput());
    }

    @Override
    protected IItemHandlerModifiable menuContainer()
    {
        throw new UnsupportedOperationException();
    }

    public static class MenuType extends LimaMenuType<MenuContext, IOControllerMenu>
    {
        public MenuType(ResourceLocation registryId)
        {
            super(registryId, MenuContext.class, IOControllerMenu::new);
        }

        @Override
        public MutableComponent getMenuTitle(Object uncheckedContext)
        {
            return checkContext(uncheckedContext).inputType.translate();
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
            SidedAccessBlockEntity holder = Objects.requireNonNull(LimaBlockUtil.getSafeBlockEntity(inventory.player.level(), pos, SidedAccessBlockEntity.class));
            BlockEntityInputType inputType = BlockEntityInputType.STREAM_CODEC.decode(net);

            return new MenuContext(holder, inputType);
        }

        @Override
        public boolean canPlayerKeepUsing(MenuContext menuContext, Player player)
        {
            return menuContext.blockEntity.getAsLimaBlockEntity().canPlayerUse(player);
        }
    }

    public record MenuContext(SidedAccessBlockEntity blockEntity, BlockEntityInputType inputType) {}
}