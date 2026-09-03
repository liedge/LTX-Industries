package liedge.ltxindustries.blockentity.base;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.data.MapLikeData;
import liedge.limacore.lib.OrderedEnum;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class BlockIOConfiguration extends MapLikeData<RelativeHorizontalSide, IOAccess> implements Iterable<Map.Entry<RelativeHorizontalSide, IOAccess>>
{
    public static final Codec<BlockIOConfiguration> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.unboundedMap(RelativeHorizontalSide.CODEC, IOAccess.CODEC).fieldOf("sides").forGetter(BlockIOConfiguration::getMap),
            Codec.BOOL.optionalFieldOf("auto_input", false).forGetter(BlockIOConfiguration::autoInput),
            Codec.BOOL.optionalFieldOf("auto_output", false).forGetter(BlockIOConfiguration::autoOutput))
            .apply(i, BlockIOConfiguration::new));
    public static final StreamCodec<FriendlyByteBuf, BlockIOConfiguration> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(_ -> new EnumMap<>(RelativeHorizontalSide.class), RelativeHorizontalSide.STREAM_CODEC, IOAccess.STREAM_CODEC),
            MapLikeData::getMap,
            ByteBufCodecs.BOOL, BlockIOConfiguration::autoInput,
            ByteBufCodecs.BOOL, BlockIOConfiguration::autoOutput,
            BlockIOConfiguration::new);

    public static BlockIOConfiguration create(IOConfigurationRules rules, Function<RelativeHorizontalSide, IOAccess> mapper)
    {
        EnumMap<RelativeHorizontalSide, IOAccess> map = new EnumMap<>(RelativeHorizontalSide.class);

        for (RelativeHorizontalSide side : rules.validSides())
        {
            IOAccess access = mapper.apply(side);
            if (!rules.validIOAccesses().contains(access)) access = rules.defaultIOAccess();
            map.put(side, access);
        }

        return new BlockIOConfiguration(map, rules.defaultAutoInput(), rules.defaultAutoOutput());
    }

    public static BlockIOConfiguration create(IOConfigurationRules rules)
    {
        return create(rules, ignored -> rules.defaultIOAccess());
    }

    public static @Nullable BlockIOConfiguration create(ConfigurableIOBlockEntityType<?> type, BlockEntityInputType inputType)
    {
        if (type.getValidInputTypes().contains(inputType))
        {
            return create(type.getIOConfigRules(inputType));
        }
        else
        {
            return null;
        }
    }

    // Class def
    private final boolean autoInput;
    private final boolean autoOutput;

    private BlockIOConfiguration(Map<RelativeHorizontalSide, IOAccess> map, boolean autoInput, boolean autoOutput)
    {
        super(map);
        this.autoInput = autoInput;
        this.autoOutput = autoOutput;
    }

    public IOAccess getIOAccess(RelativeHorizontalSide side)
    {
        return getOrDefault(side, IOAccess.DISABLED);
    }

    public IOAccess getIOAccess(Direction facing, Direction absoluteSide)
    {
        return getIOAccess(RelativeHorizontalSide.of(facing, absoluteSide));
    }

    public BlockIOConfiguration setIOAccess(RelativeHorizontalSide side, IOAccess access)
    {
        IOAccess current = getIOAccess(side);
        if (current != access)
        {
            Map<RelativeHorizontalSide, IOAccess> newMap = new EnumMap<>(this.map);
            newMap.put(side, access);
            return new BlockIOConfiguration(newMap, autoInput, autoOutput);
        }

        return this;
    }

    public BlockIOConfiguration cycleIOAccess(RelativeHorizontalSide side, IOConfigurationRules rules, boolean forward)
    {
        IOAccess current = getIOAccess(side);
        IOAccess next = forward ? OrderedEnum.nextAvailable(rules.validIOAccesses(), current) : OrderedEnum.previousAvailable(rules.validIOAccesses(), current);
        return setIOAccess(side, next);
    }

    public boolean autoInput()
    {
        return autoInput;
    }

    public boolean autoOutput()
    {
        return autoOutput;
    }

    public BlockIOConfiguration setAutoInput(boolean autoInput)
    {
        if (this.autoInput == autoInput) return this;
        else return new BlockIOConfiguration(this.map, autoInput, this.autoOutput);
    }

    public BlockIOConfiguration setAutoOutput(boolean autoOutput)
    {
        if (this.autoOutput == autoOutput) return this;
        else return new BlockIOConfiguration(this.map, this.autoInput, autoOutput);
    }

    public BlockIOConfiguration toggleAutoInput()
    {
        return setAutoInput(!autoInput());
    }

    public BlockIOConfiguration toggleAutoOutput()
    {
        return setAutoOutput(!autoOutput());
    }

    public boolean isValidForRules(IOConfigurationRules rules)
    {
        boolean mapTest = map.entrySet().stream().allMatch(entry -> rules.validSides().contains(entry.getKey()) && rules.validIOAccesses().contains(entry.getValue()));
        boolean autoIOTest = (!autoInput || rules.allowsAutoInput()) && (!autoOutput || rules.allowsAutoOutput());

        return mapTest && autoIOTest;
    }

    @Override
    public int hashCode()
    {
        return hashCode(autoInput, autoOutput);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof BlockIOConfiguration other)) return false;

        return this.map.equals(other.map) &&
                this.autoInput == other.autoInput &&
                this.autoOutput == other.autoOutput;
    }

    @Override
    public Iterator<Map.Entry<RelativeHorizontalSide, IOAccess>> iterator()
    {
        return getMap().entrySet().iterator();
    }
}