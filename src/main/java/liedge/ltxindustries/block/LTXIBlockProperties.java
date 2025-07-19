package liedge.ltxindustries.block;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.IOAccessSets;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Map;

public final class LTXIBlockProperties
{
    private LTXIBlockProperties() {}

    public static final BooleanProperty MACHINE_WORKING = BooleanProperty.create("working");

    private static final Map<Direction, EnumProperty<IOAccess>> ESA_IO_PROPERTIES = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(Direction.class, side -> EnumProperty.create(side.getSerializedName() + "_io", IOAccess.class, IOAccessSets.INPUT_XOR_OUTPUT_OR_DISABLED));

    public static EnumProperty<IOAccess> getESASideIOProperty(Direction side)
    {
        return ESA_IO_PROPERTIES.get(side);
    }
}