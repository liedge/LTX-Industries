package liedge.limatech.blockentity.io;

import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limatech.blockentity.SubMenuProviderBlockEntity;
import liedge.limatech.menu.IOControlMenu;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface SidedMachineIOHolder extends SubMenuProviderBlockEntity
{
    @Nullable MachineIOControl getIOControls(MachineInputType inputType);

    default MachineIOControl getIOControlsOrThrow(MachineInputType inputType)
    {
        return Objects.requireNonNull(getIOControls(inputType), "Machine does not support IO controls for " + inputType);
    }

    default void updateFacingForAllIO(Direction newFacing)
    {
        for (MachineInputType type : MachineInputType.values())
        {
            MachineIOControl control = getIOControls(type);
            if (control != null) control.setFacing(newFacing);
        }
    }

    void onIOControlsChanged(MachineInputType inputType);

    boolean allowsAutoInput(MachineInputType inputType);

    boolean allowsAutoOutput(MachineInputType inputType);

    default void openIOControlMenuScreen(Player player, MachineInputType inputType)
    {
        IOControlMenu.MenuContext context = new IOControlMenu.MenuContext(this, inputType);
        LimaMenuProvider.openStandaloneMenu(player, LimaTechMenus.MACHINE_IO_CONTROL.get(), context);
    }
}