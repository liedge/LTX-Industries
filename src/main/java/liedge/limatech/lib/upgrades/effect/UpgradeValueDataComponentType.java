package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limacore.lib.ModResources;
import liedge.limacore.lib.Translatable;
import liedge.limatech.lib.upgrades.effect.value.ValueUpgradeEffect;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;

import java.util.List;
import java.util.function.Consumer;

public abstract class UpgradeValueDataComponentType<T> extends UpgradeDataComponentType<T> implements Translatable
{
    private final String descriptionId;

    private UpgradeValueDataComponentType(ResourceLocation id, Codec<T> codec)
    {
        super(codec);
        this.descriptionId = ModResources.prefixIdTranslationKey("value_effect", id);
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    public static class DirectValueList extends UpgradeValueDataComponentType<List<ValueUpgradeEffect>>
    {
        public DirectValueList(ResourceLocation id)
        {
            super(id, ValueUpgradeEffect.LIST_CODEC);
        }

        @Override
        public void appendTooltipLines(List<ValueUpgradeEffect> data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            for (ValueUpgradeEffect effect : data)
            {
                linesConsumer.accept(this.translateArgs(effect.getEffectTooltip(upgradeRank)));
            }
        }
    }

    public static class ConditionalValueList extends UpgradeValueDataComponentType<List<ConditionalEffect<ValueUpgradeEffect>>>
    {
        public ConditionalValueList(ResourceLocation id, LootContextParamSet params)
        {
            super(id, ConditionalEffect.codec(ValueUpgradeEffect.CODEC, params).listOf());
        }

        @Override
        public void appendTooltipLines(List<ConditionalEffect<ValueUpgradeEffect>> data, int upgradeRank, Consumer<Component> linesConsumer)
        {
            for (ConditionalEffect<ValueUpgradeEffect> effect : data)
            {
                linesConsumer.accept(this.translateArgs(effect.effect().getEffectTooltip(upgradeRank)));
            }
        }
    }
}