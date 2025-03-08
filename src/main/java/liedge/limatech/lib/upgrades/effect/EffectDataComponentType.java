package liedge.limatech.lib.upgrades.effect;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import liedge.limacore.lib.function.ObjectIntFunction;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.function.Supplier;

public abstract class EffectDataComponentType<T> implements DataComponentType<T>
{
    public static final Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(LimaTechRegistries.UPGRADE_COMPONENT_TYPES::byNameCodec);

    public static <T extends EffectTooltipProvider> EffectDataComponentType<T> createSingle(Codec<T> codec)
    {
        return new DefaultImpl<>(codec, (data, upgradeRank, lines) -> lines.add(data.getEffectTooltip(upgradeRank)));
    }

    public static <T extends EffectTooltipProvider> EffectDataComponentType<List<T>> createList(Codec<T> elementCodec)
    {
        return new DefaultImpl<>(elementCodec.listOf(), (data, upgradeRank, lines) -> {
            for (T effect : data)
            {
                lines.add(effect.getEffectTooltip(upgradeRank));
            }
        });
    }

    public static <T> EffectDataComponentType<List<T>> createList(Codec<T> elementCodec, ObjectIntFunction<? super T, ? extends Component> tooltipFunction)
    {
        return new DefaultImpl<>(elementCodec.listOf(), (data, upgradeRank, lines) -> {
            for (T effect : data)
            {
                lines.add(tooltipFunction.applyWithInt(effect, upgradeRank));
            }
        });
    }

    public static <T extends EffectTooltipProvider> EffectDataComponentType<List<ConditionalEffect<T>>> createConditionalList(Codec<T> elementCodec, LootContextParamSet paramSet)
    {
        return new DefaultImpl<>(ConditionalEffect.codec(elementCodec, paramSet).listOf(), (data, upgradeRank, lines) -> {
            for (ConditionalEffect<T> element : data)
            {
                lines.add(element.effect().getEffectTooltip(upgradeRank));
            }
        });
    }

    public static EffectDataComponentType<Unit> createSpecialUnit(Supplier<? extends Component> tooltipSupplier)
    {
        Supplier<Component> cachedTooltip = Suppliers.memoize(tooltipSupplier::get);
        return new DefaultImpl<>(Unit.CODEC, (data, upgradeRank, lines) -> lines.add(cachedTooltip.get()));
    }

    private final Codec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;

    public EffectDataComponentType(Codec<T> codec)
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
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec()
    {
        return streamCodec;
    }

    private static class DefaultImpl<T> extends EffectDataComponentType<T>
    {
        private final EffectComponentTooltipResolver<T> tooltipResolver;

        private DefaultImpl(Codec<T> codec, EffectComponentTooltipResolver<T> tooltipResolver)
        {
            super(codec);
            this.tooltipResolver = tooltipResolver;
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, List<Component> lines)
        {
            tooltipResolver.getDataTooltips(data, upgradeRank, lines);
        }
    }

    @FunctionalInterface
    public interface EffectComponentTooltipResolver<T>
    {
        void getDataTooltips(T data, int upgradeRank, List<Component> lines);
    }
}