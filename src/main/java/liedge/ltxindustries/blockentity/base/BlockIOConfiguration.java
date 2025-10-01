package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.lib.OrderedEnum;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public sealed interface BlockIOConfiguration permits BlockIOMap
{
    Codec<BlockIOConfiguration> CODEC = BlockIOMap.CODEC.xmap(Function.identity(), o -> (BlockIOMap) o);
    StreamCodec<FriendlyByteBuf, BlockIOConfiguration> STREAM_CODEC = BlockIOMap.STREAM_CODEC.map(Function.identity(), o -> (BlockIOMap) o);

    static @Nullable BlockIOConfiguration create(Map<RelativeHorizontalSide, IOAccess> map, boolean autoInput, boolean autoOutput, IOConfigurationRules rules)
    {
        boolean mapTest = map.entrySet().stream().allMatch(entry -> rules.validSides().contains(entry.getKey()) && rules.validIOAccesses().contains(entry.getValue()));
        boolean autoIOTest = (!autoInput || rules.allowsAutoInput()) && (!autoOutput || rules.allowsAutoOutput());

        return mapTest && autoIOTest ? new BlockIOMap(BlockIOMap.copyOrCreateMap(map), autoInput, autoOutput) : null;
    }

    static BlockIOConfiguration create(IOConfigurationRules rules)
    {
        EnumMap<RelativeHorizontalSide, IOAccess> map = new EnumMap<>(RelativeHorizontalSide.class);
        for (RelativeHorizontalSide side : rules.validSides())
        {
            map.put(side, rules.defaultIOAccess());
        }
        return new BlockIOMap(map, rules.defaultAutoInput(), rules.defaultAutoOutput());
    }

    static BlockIOConfiguration create(ConfigurableIOBlockEntityType<?> type, BlockEntityInputType inputType)
    {
        return create(type.getIOConfigRules(inputType));
    }

    IOAccess getIOAccess(RelativeHorizontalSide side);

    IOAccess getIOAccess(Direction facing, Direction absoluteSide);

    BlockIOConfiguration setIOAccess(RelativeHorizontalSide side, IOAccess access);

    default BlockIOConfiguration cycleIOAccess(RelativeHorizontalSide side, IOConfigurationRules rules, boolean forward)
    {
        IOAccess current = getIOAccess(side);
        IOAccess next = forward ? OrderedEnum.nextAvailable(rules.validIOAccesses(), current) : OrderedEnum.previousAvailable(rules.validIOAccesses(), current);
        return setIOAccess(side, next);
    }

    boolean autoInput();

    boolean autoOutput();

    BlockIOConfiguration toggleAutoInput();

    BlockIOConfiguration toggleAutoOutput();
}