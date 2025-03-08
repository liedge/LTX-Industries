package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limatech.lib.upgrades.effect.value.ComplexValueUpgradeEffect;
import liedge.limatech.lib.upgrades.effect.value.SimpleValueUpgradeEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;

public abstract class ValueEffectDataComponentType<T> extends EffectDataComponentType<T> implements Translatable
{
    private static final Codec<List<SimpleValueUpgradeEffect>> SIMPLE_VALUE_LIST = SimpleValueUpgradeEffect.CODEC.listOf();

    public static ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>> createSimpleList(ResourceLocation registryId, boolean beneficial)
    {
        return new ListType(registryId, beneficial);
    }

    public static ValueEffectDataComponentType<List<ConditionalEffect<ComplexValueUpgradeEffect>>> createComplexList(LootContextParamSet paramSet, ResourceLocation registryId, boolean beneficial)
    {
        return new ComplexListType(paramSet, registryId, beneficial);
    }

    private final String descriptionId;
    protected final boolean beneficial;

    private ValueEffectDataComponentType(Codec<T> codec, ResourceLocation registryId, boolean beneficial)
    {
        super(codec);
        this.descriptionId = ModResources.prefixIdTranslationKey("value_effect", registryId);
        this.beneficial = beneficial;
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    private static class ListType extends ValueEffectDataComponentType<List<SimpleValueUpgradeEffect>>
    {
        private ListType(ResourceLocation registryId, boolean beneficial)
        {
            super(SIMPLE_VALUE_LIST, registryId, beneficial);
        }

        @Override
        public void appendTooltipLines(List<SimpleValueUpgradeEffect> data, int upgradeRank, List<Component> lines)
        {
            for (SimpleValueUpgradeEffect effect : data)
            {
                lines.add(translateArgs(effect.getValueTooltip(upgradeRank, this.beneficial)));
            }
        }
    }

    private static class ComplexListType extends ValueEffectDataComponentType<List<ConditionalEffect<ComplexValueUpgradeEffect>>>
    {
        private ComplexListType(LootContextParamSet paramSet, ResourceLocation registryId, boolean beneficial)
        {
            super(ConditionalEffect.codec(ComplexValueUpgradeEffect.CODEC, paramSet).listOf(), registryId, beneficial);
        }

        @Override
        public void appendTooltipLines(List<ConditionalEffect<ComplexValueUpgradeEffect>> data, int upgradeRank, List<Component> lines)
        {
            for (ConditionalEffect<ComplexValueUpgradeEffect> conditional : data)
            {
                ComplexValueUpgradeEffect effect = conditional.effect();
                Component valueTooltip = effect.tooltip().getValueTooltip(upgradeRank, effect.operation(), this.beneficial);
                lines.add(translateArgs(valueTooltip));
            }
        }
    }
}