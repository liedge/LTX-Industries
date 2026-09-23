package liedge.ltxindustries.blockentity.base;

import com.google.common.base.Preconditions;
import com.mojang.serialization.Codec;
import liedge.limacore.data.MapLikeData;
import net.minecraft.util.ExtraCodecs;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public final class IORuleSet extends MapLikeData<ResourceType, IORules>
{
    public static final Codec<IORuleSet> CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(ResourceType.CODEC, IORules.CODEC)).xmap(IORuleSet::new, MapLikeData::getMap);

    public static Builder builder()
    {
        return new Builder();
    }

    private IORuleSet(Map<ResourceType, IORules> map)
    {
        super(map);
    }

    public Set<ResourceType> getResourceTypes()
    {
        return Collections.unmodifiableSet(map.keySet());
    }

    public static final class Builder
    {
        private final Map<ResourceType, IORules> map = new EnumMap<>(ResourceType.class);

        private Builder() { }

        public Builder items(IORules rules)
        {
            map.put(ResourceType.ITEMS, rules);
            return this;
        }

        public Builder items(IORules.Builder builder)
        {
            return items(builder.build());
        }

        public Builder energy(IORules rules)
        {
            map.put(ResourceType.ENERGY, rules);
            return this;
        }

        public Builder energy(IORules.Builder builder)
        {
            return energy(builder.build());
        }

        public Builder fluids(IORules rules)
        {
            map.put(ResourceType.FLUIDS, rules);
            return this;
        }

        public Builder fluids(IORules.Builder builder)
        {
            return fluids(builder.build());
        }

        public IORuleSet build()
        {
            Preconditions.checkState(!map.isEmpty(), "IO rule set cannot be empty");
            return new IORuleSet(map);
        }
    }
}