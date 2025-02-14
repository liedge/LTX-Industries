package liedge.limatech.lib.upgrades;

import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limatech.lib.upgrades.effect.UpgradeDataComponentType;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.List;

public interface UpgradeBase<CTX, U extends UpgradeBase<CTX, U>>
{
    int MAX_UPGRADE_RANK = 10;
    Codec<Integer> UPGRADE_RANK_CODEC = Codec.intRange(1, MAX_UPGRADE_RANK);
    StreamCodec<ByteBuf, Integer> UPGRADE_RANK_STREAM_CODEC = LimaStreamCodecs.varIntRange(1, MAX_UPGRADE_RANK);
    Codec<DataComponentMap> COMPONENT_MAP_CODEC = DataComponentMap.makeCodec(UpgradeDataComponentType.CODEC);

    static <CTX, U extends UpgradeBase<CTX, U>> Codec<U> createDirectCodec(ResourceKey<Registry<CTX>> contextRegistryKey, ResourceKey<Registry<U>> upgradesRegistryKey, UpgradeFactory<CTX, U> factory)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC.fieldOf("title").forGetter(UpgradeBase::title),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(UpgradeBase::description),
                UPGRADE_RANK_CODEC.optionalFieldOf("max_rank", 1).forGetter(UpgradeBase::maxRank),
                RegistryCodecs.homogeneousList(contextRegistryKey).fieldOf("supported_set").forGetter(UpgradeBase::supportedSet),
                RegistryCodecs.homogeneousList(upgradesRegistryKey).fieldOf("exclusive_set").forGetter(UpgradeBase::exclusiveSet),
                COMPONENT_MAP_CODEC.fieldOf("effects").forGetter(UpgradeBase::effects),
                UpgradeIcon.CODEC.optionalFieldOf("icon", UpgradeIcon.DEFAULT_ICON).forGetter(UpgradeBase::icon))
                .apply(instance, factory));
    }

    Component title();

    Component description();

    int maxRank();

    HolderSet<CTX> supportedSet();

    HolderSet<U> exclusiveSet();

    DataComponentMap effects();

    UpgradeIcon icon();

    default MutableComponent getEffectsTooltip(int upgradeRank)
    {
        List<Component> lines = new ObjectArrayList<>();

        for (TypedDataComponent<?> component : effects())
        {
            appendTooltipLine(component, upgradeRank, lines);
        }

        return LimaComponentUtil.bulletPointList(LimaComponentUtil.BULLET_1_INDENT.copy().withStyle(ChatFormatting.GRAY), lines);
    }

    default boolean canBeInstalledOn(Holder<CTX> upgradeContext)
    {
        return supportedSet().contains(upgradeContext);
    }

    default boolean canBeInstalledAlongside(Holder<U> otherUpgrade)
    {
        return !exclusiveSet().contains(otherUpgrade);
    }

    default <T> List<T> getListEffect(DataComponentType<List<T>> type)
    {
        return effects().getOrDefault(type, List.of());
    }

    private <T> void appendTooltipLine(TypedDataComponent<T> component, int upgradeRank, List<Component> lines)
    {
        UpgradeDataComponentType<T> type = (UpgradeDataComponentType<T>) component.type();
        type.appendTooltipLines(component.value(), upgradeRank, lines);
    }

    @FunctionalInterface
    interface UpgradeFactory<CTX, U extends UpgradeBase<CTX, U>> extends Function7<Component, Component, Integer, HolderSet<CTX>, HolderSet<U>, DataComponentMap, UpgradeIcon, U>
    {
        U newInstance(Component title, Component description, int maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, DataComponentMap effects, UpgradeIcon icon);

        @Override
        default U apply(Component title, Component description, Integer maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, DataComponentMap effects, UpgradeIcon icon)
        {
            return newInstance(title, description, maxRank, supportedSet, exclusiveSet, effects, icon);
        }
    }
}