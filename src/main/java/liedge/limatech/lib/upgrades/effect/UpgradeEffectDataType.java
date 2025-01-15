package liedge.limatech.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public interface UpgradeEffectDataType<T>
{
    Codec<UpgradeEffectDataType<?>> CODEC = Codec.lazyInitialized(LimaTechRegistries.UPGRADE_DATA_TYPE::byNameCodec);

    static <T extends UpgradeEffect> UpgradeEffectDataType<T> of(ResourceLocation id, Codec<T> codec)
    {
        return new Simple<>(id, codec);
    }

    static <T extends UpgradeEffect> UpgradeEffectDataType<List<T>> listOf(ResourceLocation id, Codec<T> codec)
    {
        return new SimpleList<>(id, codec.listOf());
    }

    ResourceLocation id();

    Codec<T> codec();

    void appendTooltipLines(T effectData, int upgradeRank, List<Component> lines);

    record Simple<T extends UpgradeEffect>(ResourceLocation id, Codec<T> codec) implements UpgradeEffectDataType<T>
    {
        @Override
        public void appendTooltipLines(T effectData, int upgradeRank, List<Component> lines)
        {
            lines.add(effectData.defaultEffectTooltip(upgradeRank));
        }
    }

    record SimpleList<T extends UpgradeEffect>(ResourceLocation id, Codec<List<T>> codec) implements UpgradeEffectDataType<List<T>>
    {
        @Override
        public void appendTooltipLines(List<T> effectData, int upgradeRank, List<Component> lines)
        {
            effectData.forEach(effect -> lines.add(effect.defaultEffectTooltip(upgradeRank)));
        }
    }
}