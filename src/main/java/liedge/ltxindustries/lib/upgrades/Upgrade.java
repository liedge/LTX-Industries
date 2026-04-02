package liedge.ltxindustries.lib.upgrades;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeDataComponentType;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeComponentLike;
import liedge.ltxindustries.registry.LTXIRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.TypedInstance;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public record Upgrade(UpgradeDisplayInfo display, int maxRank, UpgradeUsers users, HolderSet<Upgrade> exclusiveSet, DataComponentMap effects)
{
    public static final int MAX_RANK = 10;
    public static final Codec<Integer> RANK_CODEC = Codec.intRange(1, MAX_RANK);
    public static final StreamCodec<ByteBuf, Integer> RANK_STREAM_CODEC = LimaStreamCodecs.varIntRange(1, MAX_RANK);
    public static final Codec<DataComponentMap> EFFECTS_CODEC = DataComponentMap.makeCodec(UpgradeDataComponentType.CODEC);

    public static final Codec<Upgrade> DIRECT_CODEC = RecordCodecBuilder.create(i -> i.group(
            UpgradeDisplayInfo.CODEC.fieldOf("display").forGetter(Upgrade::display),
            RANK_CODEC.optionalFieldOf("max_rank", 1).forGetter(Upgrade::maxRank),
            UpgradeUsers.CODEC.fieldOf("users").forGetter(Upgrade::users),
            RegistryCodecs.homogeneousList(LTXIRegistries.Keys.UPGRADES).optionalFieldOf("exclusive_set", HolderSet.empty()).forGetter(Upgrade::exclusiveSet),
            EFFECTS_CODEC.fieldOf("effects").forGetter(Upgrade::effects))
            .apply(i, Upgrade::new));

    public static final Codec<Holder<Upgrade>> CODEC = RegistryFixedCodec.create(LTXIRegistries.Keys.UPGRADES);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Upgrade>> STREAM_CODEC = ByteBufCodecs.holderRegistry(LTXIRegistries.Keys.UPGRADES);

    public static final Comparator<Holder<Upgrade>> BY_CATEGORY_THEN_ID = Comparator
            .<Holder<Upgrade>, String>comparing(holder -> holder.value().display.category(), Comparator.comparing(String::isEmpty).thenComparing(Comparator.naturalOrder()))
            .thenComparing(holder -> LimaRegistryUtil.getNonNullRegistryId(holder));

    public static UpgradeBuilder builder(ResourceKey<Upgrade> key)
    {
        return new UpgradeBuilder(key);
    }

    // Class def
    public boolean canBeInstalledOn(TypedInstance<?> user)
    {
        return users.test(user);
    }

    public boolean canBeInstalledAlongside(Holder<Upgrade> otherUpgrade)
    {
        return !exclusiveSet.contains(otherUpgrade);
    }

    public <T> List<T> getListEffect(DataComponentType<List<T>> type)
    {
        return effects.getOrDefault(type, List.of());
    }

    public <T> List<T> getListEffect(Supplier<? extends DataComponentType<List<T>>> typeSupplier)
    {
        return getListEffect(typeSupplier.get());
    }

    public void appendEffectTooltips(int upgradeRank, Consumer<Component> consumer)
    {
        for (UpgradeComponentLike tooltip : display.tooltips())
        {
            consumer.accept(LimaComponentUtil.BULLET_1_INDENT.copy().withStyle(ChatFormatting.GRAY).append(tooltip.get(upgradeRank)));
        }

        for (TypedDataComponent<?> dataComponent : effects)
        {
            appendGeneratedTooltips(dataComponent, upgradeRank, component -> consumer.accept(LimaComponentUtil.BULLET_1_INDENT.copy().withStyle(ChatFormatting.GRAY).append(component)));
        }
    }

    private <T> void appendGeneratedTooltips(TypedDataComponent<T> typedComponent, int upgradeRank, Consumer<Component> consumer)
    {
        if (typedComponent.type() instanceof UpgradeDataComponentType<T> upgradeType)
        {
            upgradeType.appendTooltipLines(typedComponent.value(), upgradeRank, consumer);
        }
    }
}