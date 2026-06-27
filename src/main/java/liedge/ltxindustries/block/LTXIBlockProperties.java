package liedge.ltxindustries.block;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class LTXIBlockProperties
{
    private LTXIBlockProperties() {}

    public static final EnumProperty<MachineState> BINARY_MACHINE_STATE = EnumProperty.create("machine_state", MachineState.class, MachineState.IDLE, MachineState.ACTIVE);

    public static boolean isMachineActive(BlockState state)
    {
        return state.getValue(BINARY_MACHINE_STATE) == MachineState.ACTIVE;
    }
}