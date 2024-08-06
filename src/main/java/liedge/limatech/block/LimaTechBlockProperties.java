package liedge.limatech.block;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.util.LimaCollectionsUtil;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Map;

public final class LimaTechBlockProperties
{
    private LimaTechBlockProperties() {}

    public static final BooleanProperty MACHINE_WORKING = BooleanProperty.create("working");

    private static final Map<Direction, EnumProperty<IOAccess>> ESA_IO_PROPERTIES = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(Direction.class, side -> EnumProperty.create(side.getSerializedName() + "_io", IOAccess.class, IOAccess.INPUT_OR_OUTPUT_ONLY_AND_DISABLED));

    public static EnumProperty<IOAccess> getESASideIOProperty(Direction side)
    {
        return ESA_IO_PROPERTIES.get(side);
    }
}