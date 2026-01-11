package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.lib.OrderedEnum;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

public sealed interface BlockIOConfiguration extends Iterable<Map.Entry<RelativeHorizontalSide, IOAccess>> permits BlockIOMap
{
    Codec<BlockIOConfiguration> CODEC = BlockIOMap.CODEC.xmap(Function.identity(), o -> (BlockIOMap) o);
    StreamCodec<FriendlyByteBuf, BlockIOConfiguration> STREAM_CODEC = BlockIOMap.STREAM_CODEC.map(Function.identity(), o -> (BlockIOMap) o);

    static BlockIOConfiguration create(IOConfigurationRules rules, Function<RelativeHorizontalSide, IOAccess> mapper)
    {
        EnumMap<RelativeHorizontalSide, IOAccess> map = new EnumMap<>(RelativeHorizontalSide.class);

        for (RelativeHorizontalSide side : rules.validSides())
        {
            IOAccess access = mapper.apply(side);
            if (!rules.validIOAccesses().contains(access)) access = rules.defaultIOAccess();
            map.put(side, access);
        }

        return new BlockIOMap(map, rules.defaultAutoInput(), rules.defaultAutoOutput());
    }

    static BlockIOConfiguration create(IOConfigurationRules rules)
    {
        return create(rules, ignored -> rules.defaultIOAccess());
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

    BlockIOConfiguration setAutoInput(boolean autoInput);

    BlockIOConfiguration setAutoOutput(boolean autoOutput);

    default BlockIOConfiguration toggleAutoInput()
    {
        return setAutoInput(!autoInput());
    }

    default BlockIOConfiguration toggleAutoOutput()
    {
        return setAutoOutput(!autoOutput());
    }

    boolean isValidForRules(IOConfigurationRules rules);
}