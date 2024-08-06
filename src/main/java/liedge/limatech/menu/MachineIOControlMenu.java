package liedge.limatech.menu;

import liedge.limacore.inventory.menu.LimaMenu;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.registry.LimaCoreNetworkSerializers;
import liedge.limacore.util.LimaBlockUtil;
import liedge.limatech.blockentity.io.MachineIOControl;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.blockentity.io.SidedMachineIOHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class MachineIOControlMenu extends LimaMenu<SidedMachineIOHolder>
{
    private MachineIOControlMenu(LimaMenuType<SidedMachineIOHolder, ?> type, int containerId, Inventory inventory, SidedMachineIOHolder menuContext)
    {
        super(type, containerId, inventory, menuContext);
        addPlayerInventory(DEFAULT_INV_X, DEFAULT_INV_Y);
        addPlayerHotbar(DEFAULT_INV_X, DEFAULT_HOTBAR_Y);
    }

    public MachineIOControl getIOControl()
    {
        return menuContext.getIOControlsOrThrow(((MenuType) getType()).inputType);
    }

    @Override
    protected boolean quickMoveInternal(int index, ItemStack stack)
    {
        return false;
    }

    @Override
    public void defineDataWatchers(DataWatcherCollector collector)
    {
        collector.register(getIOControl().getOrCreateDataWatcher(() -> level().registryAccess()));
    }

    @Override
    protected void defineButtonEventHandlers(EventHandlerBuilder builder)
    {
        MachineIOControl ioControl = getIOControl();
        builder.handleUnitAction(0, menuContext::returnToPrimaryMenuScreen);
        builder.handleAction(1, LimaCoreNetworkSerializers.DIRECTION, (sender, side) -> ioControl.cycleSideIO(side, true));
        builder.handleAction(2, LimaCoreNetworkSerializers.DIRECTION, (sender, side) -> ioControl.cycleSideIO(side, false));
        builder.handleUnitAction(3, sender -> ioControl.toggleAutoInput());
        builder.handleUnitAction(4, sender -> ioControl.toggleAutoOutput());
    }

    public static class MenuType extends LimaMenuType<SidedMachineIOHolder, MachineIOControlMenu>
    {
        private final MachineInputType inputType;

        public MenuType(ResourceLocation registryId, MachineInputType inputType)
        {
            super(registryId, SidedMachineIOHolder.class, MachineIOControlMenu::new);
            this.inputType = inputType;
        }

        @Override
        public String descriptionId()
        {
            return inputType.descriptionId();
        }

        @Override
        public MutableComponent translate()
        {
            return inputType.translate();
        }

        @Override
        public void encodeContext(SidedMachineIOHolder menuContext, RegistryFriendlyByteBuf net)
        {
            net.writeBlockPos(menuContext.getAsLimaBlockEntity().getBlockPos());
        }

        @Override
        protected SidedMachineIOHolder decodeContext(RegistryFriendlyByteBuf net, Inventory inventory)
        {
            BlockPos pos = net.readBlockPos();
            return Objects.requireNonNull(LimaBlockUtil.getSafeBlockEntity(inventory.player.level(), pos, SidedMachineIOHolder.class));
        }

        @Override
        public boolean canPlayerKeepUsing(SidedMachineIOHolder menuContext, Player player)
        {
            return menuContext.getAsLimaBlockEntity().canPlayerUse(player);
        }
    }
}