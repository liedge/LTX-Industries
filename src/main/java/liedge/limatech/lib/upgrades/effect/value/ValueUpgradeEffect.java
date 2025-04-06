package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.List;

public final class ValueUpgradeEffect implements EffectTooltipProvider
{
    // Codecs
    private static final MapCodec<NumberSource> SOURCE_MAP_CODEC = LimaCoreCodecs.xorSubclassMapCodec(SimpleSource.CODEC, ContextSource.CODEC, SimpleSource.class, ContextSource.class);
    public static final Codec<ValueUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            SOURCE_MAP_CODEC.forGetter(o -> o.source),
            CompoundValueOperation.CODEC.fieldOf("op").forGetter(ValueUpgradeEffect::getOperation),
            ValueEffectTooltip.CODEC.fieldOf("tooltip").forGetter(o -> o.tooltip))
            .apply(instance, ValueUpgradeEffect::new));
    public static final Codec<List<ValueUpgradeEffect>> LIST_CODEC = CODEC.listOf();

    // Helper factories
    public static ValueUpgradeEffect createSimple(DoubleLevelBasedValue value, CompoundValueOperation operation, boolean invertColors)
    {
        return new ValueUpgradeEffect(new SimpleSource(value), operation, new SimpleValueTooltip(value, invertColors));
    }

    public static ValueUpgradeEffect createSimple(DoubleLevelBasedValue value, CompoundValueOperation operation)
    {
        return createSimple(value, operation, false);
    }

    public static ValueUpgradeEffect create(NumberProvider value, CompoundValueOperation operation, ValueEffectTooltip tooltip)
    {
        return new ValueUpgradeEffect(new ContextSource(value), operation, tooltip);
    }

    // Class definition
    private final NumberSource source;
    private final CompoundValueOperation operation;
    private final ValueEffectTooltip tooltip;

    private ValueUpgradeEffect(NumberSource source, CompoundValueOperation operation, ValueEffectTooltip tooltip)
    {
        this.source = source;
        this.operation = operation;
        this.tooltip = tooltip;
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return tooltip.get(upgradeRank, operation);
    }

    public CompoundValueOperation getOperation()
    {
        return operation;
    }

    public double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation.computeDouble(base, total, source.get(context, upgradeRank));
    }

    private interface NumberSource
    {
        double get(LootContext context, int upgradeRank);
    }

    private record SimpleSource(DoubleLevelBasedValue value) implements NumberSource
    {
        private static final MapCodec<SimpleSource> CODEC = DoubleLevelBasedValue.CODEC.xmap(SimpleSource::new, SimpleSource::value).fieldOf("simple_value");

        @Override
        public double get(LootContext context, int upgradeRank)
        {
            return value.calculate(upgradeRank);
        }
    }

    private record ContextSource(NumberProvider value) implements NumberSource
    {
        private static final MapCodec<ContextSource> CODEC = NumberProviders.CODEC.xmap(ContextSource::new, ContextSource::value).fieldOf("context_value");

        @Override
        public double get(LootContext context, int upgradeRank)
        {
            return value.getFloat(context);
        }
    }
}