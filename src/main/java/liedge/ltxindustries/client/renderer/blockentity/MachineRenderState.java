package liedge.ltxindustries.client.renderer.blockentity;

import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.client.model.custom.EnergyBoltData;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import org.jspecify.annotations.Nullable;

public class MachineRenderState extends BlockEntityRenderState
{
    Direction facing = Direction.NORTH;

    @Nullable ItemStackRenderState previewItem;
    float machineSpin;
    @Nullable EnergyBoltData machineBolt;

    LimaColor energyColor = LimaColor.WHITE;
    float energyFill;
}