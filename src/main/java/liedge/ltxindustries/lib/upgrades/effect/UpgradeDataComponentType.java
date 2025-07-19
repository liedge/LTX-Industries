package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaDataComponentType;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.TargetedConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.function.Consumer;

public abstract class UpgradeDataComponentType<T> extends LimaDataComponentType<T>
{
    public static final Codec<DataComponentType<?>> CODEC = Codec.lazyInitialized(LTXIRegistries.UPGRADE_COMPONENT_TYPES::byNameCodec);

    public static UpgradeDataComponentType<Unit> createUnit(Component tooltip)
    {
        return new UnitType(tooltip);
    }

    public static <T extends EffectTooltipProvider> UpgradeDataComponentType<T> createSingle(Codec<T> codec)
    {
        return new DirectSingleType<>(codec);
    }

    public static <T extends EffectTooltipProvider> UpgradeDataComponentType<List<T>> createList(Codec<T> elementCodec)
    {
        return new DirectListType<>(elementCodec.listOf());
    }

    public static <T extends EffectTooltipProvider> UpgradeDataComponentType<List<ConditionalEffect<T>>> createConditionalList(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new ConditionalListType<>(ConditionalEffect.codec(elementCodec, params).listOf());
    }

    public static <T extends EffectTooltipProvider> UpgradeDataComponentType<List<TargetedConditionalEffect<T>>> createTargetedList(Codec<T> elementCodec, LootContextParamSet params)
    {
        return new TargetedListType<>(TargetedConditionalEffect.codec(elementCodec, params).listOf());
    }

    protected UpgradeDataComponentType(Codec<T> codec)
    {
        super(codec);
    }

    public abstract void appendTooltipLines(T data, int upgradeRank, Consumer<Component> linesConsumer);

    public static class DirectSingleType<T extends EffectTooltipProvider> extends UpgradeDataComponentType<T>
    {
        public DirectSingleType(Codec<T> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(T data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            data.appendEffectLines(upgradeRank, linesConsumer);
        }
    }

    public static class DirectListType<T extends EffectTooltipProvider> extends UpgradeDataComponentType<List<T>>
    {
        public DirectListType(Codec<List<T>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<T> data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            for (T effect : data)
            {
                effect.appendEffectLines(upgradeRank, linesConsumer);
            }
        }
    }

    public static class ConditionalListType<T extends EffectTooltipProvider> extends UpgradeDataComponentType<List<ConditionalEffect<T>>>
    {
        public ConditionalListType(Codec<List<ConditionalEffect<T>>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<ConditionalEffect<T>> data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            for (ConditionalEffect<T> conditionalEffect : data)
            {
                conditionalEffect.effect().appendEffectLines(upgradeRank, linesConsumer);
            }
        }
    }

    public static class TargetedListType<T extends EffectTooltipProvider> extends UpgradeDataComponentType<List<TargetedConditionalEffect<T>>>
    {
        public TargetedListType(Codec<List<TargetedConditionalEffect<T>>> codec)
        {
            super(codec);
        }

        @Override
        public void appendTooltipLines(List<TargetedConditionalEffect<T>> data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            for (TargetedConditionalEffect<T> conditionalEffect : data)
            {
                conditionalEffect.effect().appendEffectLines(upgradeRank, linesConsumer);
            }
        }
    }

    public static class UnitType extends UpgradeDataComponentType<Unit>
    {
        private final Component tooltip;

        public UnitType(Component tooltip)
        {
            super(Unit.CODEC);
            this.tooltip = tooltip;
        }

        @Override
        public void appendTooltipLines(Unit data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            linesConsumer.accept(tooltip);
        }
    }
}