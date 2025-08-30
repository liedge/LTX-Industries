package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaDataComponentType;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class UpgradeDataComponentType<T> extends LimaDataComponentType<T>
{
    public static final Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(LTXIRegistries.UPGRADE_COMPONENT_TYPES::byNameCodec);

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<T> of(Codec<T> codec)
    {
        return new DirectSingleType<>(codec);
    }

    public static <T> UpgradeDataComponentType<T> of(Codec<T> codec, @Nullable BiConsumer<T, Consumer<Component>> tooltipFunction)
    {
        return new CustomSingleType<>(codec, tooltipFunction);
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<T>> listOf(Codec<T> elementCodec)
    {
        return new DirectListType<>(elementCodec.listOf());
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<ConditionalEffect<T>>> conditionalListOf(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new ConditionalListType<>(ConditionalEffect.codec(elementCodec, params).listOf());
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<TargetedConditionalEffect<T>>> targetedConditionalListOf(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new TargetedListType<>(TargetedConditionalEffect.codec(elementCodec, params).listOf());
    }

    private UpgradeDataComponentType(Codec<T> codec)
    {
        super(codec);
    }

    public abstract void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines);

    private static class DirectSingleType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<T>
    {
        public DirectSingleType(Codec<T> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines)
        {
            data.addUpgradeTooltips(upgradeRank, lines);
        }
    }

    private static class CustomSingleType<T> extends UpgradeDataComponentType<T>
    {
        private static final BiConsumer<Object, Consumer<Component>> NO_TOOLTIP_OP = (p1, p2) -> {};

        private final BiConsumer<? super T, Consumer<Component>> tooltipFunction;

        CustomSingleType(Codec<T> codec, @Nullable BiConsumer<T, Consumer<Component>> tooltipFunction)
        {
            super(codec);
            this.tooltipFunction = tooltipFunction != null ? tooltipFunction : NO_TOOLTIP_OP;
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines)
        {
            tooltipFunction.accept(data, lines);
        }
    }

    private static class DirectListType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<List<T>>
    {
        public DirectListType(Codec<List<T>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<T> data, int upgradeRank, Consumer<Component> lines)
        {
            for (T effect : data)
            {
                effect.addUpgradeTooltips(upgradeRank, lines);
            }
        }
    }

    private static class ConditionalListType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<List<ConditionalEffect<T>>>
    {
        public ConditionalListType(Codec<List<ConditionalEffect<T>>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<ConditionalEffect<T>> data, int upgradeRank, Consumer<Component> lines)
        {
            for (ConditionalEffect<T> conditionalEffect : data)
            {
                conditionalEffect.effect().addUpgradeTooltips(upgradeRank, lines);
            }
        }
    }

    private static class TargetedListType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<List<TargetedConditionalEffect<T>>>
    {
        public TargetedListType(Codec<List<TargetedConditionalEffect<T>>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<TargetedConditionalEffect<T>> data, int upgradeRank, Consumer<Component> lines)
        {
            for (TargetedConditionalEffect<T> conditionalEffect : data)
            {
                conditionalEffect.effect().addUpgradeTooltips(upgradeRank, lines);
            }
        }
    }
}