package liedge.ltxindustries.blockentity.base;

import liedge.limacore.menu.LimaMenuProvider;
import liedge.ltxindustries.menu.IOControllerMenu;
import liedge.ltxindustries.registry.game.LTXIMenus;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

public interface SidedAccessBlockEntity extends SubMenuProviderBlockEntity
{
    /**
     * Retrieves the {@link IOController} for the given {@code inputType}. Implementations of this interface must
     * provide a valid controller for all {@link BlockEntityInputType} in {@link SidedAccessBlockEntityType#getValidInputTypes()}.
     * @param inputType The input type
     * @throws UnsupportedOperationException If the block entity does not support the provided input type
     * @return The IO controller
     */
    IOController getIOController(BlockEntityInputType inputType) throws UnsupportedOperationException;

    Direction getFacing();

    SidedAccessBlockEntityType<?> getType();

    void onIOControlsChanged(BlockEntityInputType inputType);

    default void openIOControlMenuScreen(Player player, BlockEntityInputType inputType)
    {
        IOControllerMenu.MenuContext context = new IOControllerMenu.MenuContext(this, inputType);
        LimaMenuProvider.create(LTXIMenus.MACHINE_IO_CONTROL.get(), context, context.inputType().getMenuTitle().translate(), false).openMenuScreen(player);
    }
}