package liedge.limatech.blockentity.io;

import liedge.limacore.blockentity.LimaBlockEntityAccess;
import liedge.limacore.inventory.menu.LimaMenuProvider;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limatech.menu.MachineIOControlMenu;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public interface SidedMachineIOHolder extends LimaBlockEntityAccess
{
    @Nullable MachineIOControl getIOControls(MachineInputType inputType);

    default MachineIOControl getIOControlsOrThrow(MachineInputType inputType)
    {
        return Objects.requireNonNull(getIOControls(inputType), "Machine does not support IO controls for " + inputType);
    }

    void onIOControlsChanged(MachineInputType inputType);

    boolean allowsAutoInput(MachineInputType inputType);

    boolean allowsAutoOutput(MachineInputType inputType);

    default void returnToPrimaryMenuScreen(Player player)
    {
        LimaCoreUtil.castOrThrow(LimaMenuProvider.class, this).openMenuScreen(player);
    }

    default void openIOControlMenuScreen(Player player, MachineInputType inputType)
    {
        MachineIOControlMenu.MenuContext context = new MachineIOControlMenu.MenuContext(this, inputType);
        LimaMenuProvider.openStandaloneMenu(player, LimaTechMenus.MACHINE_IO_CONTROL.get(), context);
    }
}