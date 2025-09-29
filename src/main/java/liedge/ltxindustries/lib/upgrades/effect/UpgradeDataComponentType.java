package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaDataComponentType;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class UpgradeDataComponentType<T> extends LimaDataComponentType<T>
{
    public static final Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(LTXIRegistries.UPGRADE_COMPONENT_TYPES::byNameCodec);

    public static <T> UpgradeDataComponentType<T> custom(Codec<T> codec)
    {
        return new CustomType<>(codec, CustomType.NO_TOOLTIP_OP);
    }

    public static <T> UpgradeDataComponentType<T> custom(Codec<T> codec, BiConsumer<? super T, Consumer<Component>> tooltipFunction)
    {
        return new CustomType<>(codec, tooltipFunction);
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<T> of(Codec<T> codec)
    {
        return new SingleType<>(codec);
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<T>> listOf(Codec<T> elementCodec)
    {
        return new ListType<>(elementCodec.listOf());
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<ConditionalEffect<T>>> conditionalListOf(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new ConditionalListType<>(ConditionalEffect.codec(elementCodec, params).listOf());
    }

    public static <T extends UpgradeTooltipsProvider> UpgradeDataComponentType<List<TargetedConditionalEffect<T>>> targetedConditionalListOf(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new TargetedConditionalListType<>(TargetedConditionalEffect.codec(elementCodec, params).listOf());
    }

    private UpgradeDataComponentType(Codec<T> codec)
    {
        super(codec);
    }

    public abstract void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines);

    private static class CustomType<T> extends UpgradeDataComponentType<T>
    {
        private static final BiConsumer<Object, Consumer<Component>> NO_TOOLTIP_OP = (p1, p2) -> {};

        private final BiConsumer<? super T, Consumer<Component>> tooltipFunction;

        CustomType(Codec<T> codec, BiConsumer<? super T, Consumer<Component>> tooltipFunction)
        {
            super(codec);
            this.tooltipFunction = tooltipFunction;
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines)
        {
            tooltipFunction.accept(data, lines);
        }
    }

    private static class SingleType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<T>
    {
        public SingleType(Codec<T> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, Consumer<Component> lines)
        {
            data.addUpgradeTooltips(upgradeRank, lines);
        }
    }

    private static class ListType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<List<T>>
    {
        public ListType(Codec<List<T>> codec)
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

    private static class TargetedConditionalListType<T extends UpgradeTooltipsProvider> extends UpgradeDataComponentType<List<TargetedConditionalEffect<T>>>
    {
        public TargetedConditionalListType(Codec<List<TargetedConditionalEffect<T>>> codec)
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