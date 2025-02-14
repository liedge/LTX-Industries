package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;

public abstract class UpgradeDataComponentType<T> implements DataComponentType<T>
{
    public static final Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(LimaTechRegistries.UPGRADE_COMPONENT_TYPES::byNameCodec);

    public static <T extends UpgradeEffect> UpgradeDataComponentType<T> singletonType(Codec<T> codec)
    {
        return new SingletonType<>(codec);
    }

    public static <T extends UpgradeEffect> UpgradeDataComponentType<List<T>> listType(Codec<T> elementCodec)
    {
        return new ListType<>(elementCodec.listOf());
    }

    private final Codec<T> codec;
    private final StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec;

    protected UpgradeDataComponentType(Codec<T> codec)
    {
        this.codec = codec;
        this.streamCodec = ByteBufCodecs.fromCodecWithRegistries(codec);
    }

    public abstract void appendTooltipLines(T data, int upgradeRank, List<Component> lines);

    @Override
    public Codec<T> codec()
    {
        return codec;
    }

    @Override
    public StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec()
    {
        return streamCodec;
    }

    private static class SingletonType<T extends UpgradeEffect> extends UpgradeDataComponentType<T>
    {
        private SingletonType(Codec<T> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, List<Component> lines)
        {
            lines.add(data.defaultEffectTooltip(upgradeRank));
        }
    }

    private static class ListType<T extends UpgradeEffect> extends UpgradeDataComponentType<List<T>>
    {
        private ListType(Codec<List<T>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<T> data, int upgradeRank, List<Component> lines)
        {
            for (T e : data)
            {
                lines.add(e.defaultEffectTooltip(upgradeRank));
            }
        }
    }
}