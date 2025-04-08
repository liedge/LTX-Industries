package liedge.limatech.lib.upgrades;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
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
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public interface UpgradeBase<CTX, U extends UpgradeBase<CTX, U>>
{
    int MAX_UPGRADE_RANK = 10;
    Codec<Integer> UPGRADE_RANK_CODEC = Codec.intRange(1, MAX_UPGRADE_RANK);
    StreamCodec<ByteBuf, Integer> UPGRADE_RANK_STREAM_CODEC = LimaStreamCodecs.varIntRange(1, MAX_UPGRADE_RANK);
    Codec<DataComponentMap> COMPONENT_MAP_CODEC = DataComponentMap.makeCodec(UpgradeDataComponentType.CODEC);

    static <CTX, U extends UpgradeBase<CTX, U>> Codec<U> createDirectCodec(ResourceKey<Registry<CTX>> contextRegistryKey, ResourceKey<Registry<U>> upgradesRegistryKey, UpgradeFactory<CTX, U> factory)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                UpgradeDisplayInfo.CODEC.fieldOf("display").forGetter(UpgradeBase::display),
                UPGRADE_RANK_CODEC.optionalFieldOf("max_rank", 1).forGetter(UpgradeBase::maxRank),
                RegistryCodecs.homogeneousList(contextRegistryKey).fieldOf("supported_set").forGetter(UpgradeBase::supportedSet),
                RegistryCodecs.homogeneousList(upgradesRegistryKey).fieldOf("exclusive_set").forGetter(UpgradeBase::exclusiveSet),
                COMPONENT_MAP_CODEC.fieldOf("effects").forGetter(UpgradeBase::effects))
                .apply(instance, factory));
    }

    static <U extends UpgradeBase<?, U>> Comparator<Holder<U>> comparingCategoryThenId()
    {
        return Comparator.<Holder<U>, String>comparing(holder -> holder.value().display().category(), Comparator.comparing(String::isEmpty).thenComparing(Comparator.naturalOrder()))
                .thenComparing(holder -> LimaRegistryUtil.getNonNullRegistryId(holder));
    }

    UpgradeDisplayInfo display();

    int maxRank();

    HolderSet<CTX> supportedSet();

    HolderSet<U> exclusiveSet();

    DataComponentMap effects();

    default void applyEffectsTooltips(int upgradeRank, Consumer<Component> consumer)
    {
        for (TypedDataComponent<?> dataComponent : effects())
        {
            processBuiltInTooltips(dataComponent, upgradeRank, component -> consumer.accept(LimaComponentUtil.BULLET_1_INDENT.copy().withStyle(ChatFormatting.GRAY).append(component)));
        }
    }

    private <T> void processBuiltInTooltips(TypedDataComponent<T> dataComponent, int upgradeRank, Consumer<Component> consumer)
    {
        if (dataComponent.type() instanceof UpgradeDataComponentType<T> type)
        {
            type.appendTooltipLines(dataComponent.value(), upgradeRank, consumer);
        }
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

    @FunctionalInterface
    interface UpgradeFactory<CTX, U extends UpgradeBase<CTX, U>> extends Function5<UpgradeDisplayInfo, Integer, HolderSet<CTX>, HolderSet<U>, DataComponentMap, U>
    {
        U create(UpgradeDisplayInfo displayInfo, int maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, DataComponentMap effects);

        @Override
        default U apply(UpgradeDisplayInfo display, Integer maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, DataComponentMap effects)
        {
            return create(display, maxRank, supportedSet, exclusiveSet, effects);
        }
    }
}