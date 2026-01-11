package liedge.ltxindustries.blockentity.base;

import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

record BlockIOMap(Map<RelativeHorizontalSide, IOAccess> map, boolean autoInput, boolean autoOutput) implements BlockIOConfiguration
{
    static final Codec<BlockIOMap> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(RelativeHorizontalSide.CODEC, IOAccess.CODEC).fieldOf("sides").forGetter(BlockIOMap::map),
            Codec.BOOL.fieldOf("auto_input").forGetter(BlockIOMap::autoInput),
            Codec.BOOL.fieldOf("auto_output").forGetter(BlockIOMap::autoOutput))
            .apply(instance, BlockIOMap::new));

    static final StreamCodec<FriendlyByteBuf, BlockIOMap> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(size -> new EnumMap<>(RelativeHorizontalSide.class), RelativeHorizontalSide.STREAM_CODEC, IOAccess.STREAM_CODEC),
            blockIOMap -> blockIOMap.map,
            ByteBufCodecs.BOOL,
            BlockIOMap::autoInput,
            ByteBufCodecs.BOOL,
            BlockIOMap::autoOutput,
            BlockIOMap::new);

    static Map<RelativeHorizontalSide, IOAccess> copyOrCreateMap(Map<RelativeHorizontalSide, IOAccess> map)
    {
        return map.isEmpty() ? new EnumMap<>(RelativeHorizontalSide.class) : new EnumMap<>(map);
    }

    @Override
    public IOAccess getIOAccess(RelativeHorizontalSide side)
    {
        return map.getOrDefault(side, IOAccess.DISABLED);
    }

    @Override
    public IOAccess getIOAccess(Direction facing, Direction absoluteSide)
    {
        return getIOAccess(RelativeHorizontalSide.of(facing, absoluteSide));
    }

    @Override
    public BlockIOConfiguration setIOAccess(RelativeHorizontalSide side, IOAccess access)
    {
        IOAccess current = getIOAccess(side);
        if (current == access) return this;
        else
        {
            Map<RelativeHorizontalSide, IOAccess> newMap = copyOrCreateMap(this.map);
            newMap.put(side, access);
            return new BlockIOMap(newMap, this.autoInput, this.autoOutput);
        }
    }

    @Override
    public BlockIOConfiguration setAutoInput(boolean autoInput)
    {
        if (this.autoInput == autoInput) return this;
        else return new BlockIOMap(this.map, autoInput, this.autoOutput);
    }

    @Override
    public BlockIOConfiguration setAutoOutput(boolean autoOutput)
    {
        if (this.autoOutput == autoOutput) return this;
        else return new BlockIOMap(this.map, this.autoInput, autoOutput);
    }

    @Override
    public boolean isValidForRules(IOConfigurationRules rules)
    {
        boolean mapTest = map.entrySet().stream().allMatch(entry -> rules.validSides().contains(entry.getKey()) && rules.validIOAccesses().contains(entry.getValue()));
        boolean autoIOTest = (!autoInput || rules.allowsAutoInput()) && (!autoOutput || rules.allowsAutoOutput());

        return mapTest && autoIOTest;
    }

    @Override
    public Iterator<Map.Entry<RelativeHorizontalSide, IOAccess>> iterator()
    {
        return Iterators.unmodifiableIterator(map.entrySet().iterator());
    }
}