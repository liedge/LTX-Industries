package liedge.ltxindustries.block;

import net.minecraft.world.level.block.state.properties.EnumProperty;

public final class LTXIBlockProperties
{
    private LTXIBlockProperties() {}

    public static final EnumProperty<MachineState> BINARY_MACHINE_STATE = EnumProperty.create("machine_state", MachineState.class, MachineState.IDLE, MachineState.ACTIVE);
}