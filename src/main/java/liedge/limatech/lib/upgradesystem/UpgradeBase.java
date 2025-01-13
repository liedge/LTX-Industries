package liedge.limatech.lib.upgradesystem;

import com.mojang.datafixers.util.Function7;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limatech.LimaTech;
import liedge.limatech.client.UpgradeIcon;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public interface UpgradeBase<CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>>
{
    int MAX_UPGRADE_RANK = 10;
    int MAX_EFFECTS_PER_UPGRADE = 5;
    ResourceLocation DEFAULT_UPGRADE_ICON = LimaTech.RESOURCES.location("generic");
    Codec<Integer> UPGRADE_RANK_CODEC = Codec.intRange(1, MAX_UPGRADE_RANK);
    StreamCodec<ByteBuf, Integer> UPGRADE_RANK_STREAM_CODEC = LimaStreamCodecs.varIntRange(1, MAX_UPGRADE_RANK);

    static <CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>> Codec<U> createDirectCodec(ResourceKey<Registry<CTX>> contextRegistryKey, ResourceKey<Registry<U>> upgradesRegistryKey, Codec<E> effectCodec, UpgradeFactory<CTX, E, U> factory)
    {
        return RecordCodecBuilder.create(instance -> instance.group(
                ComponentSerialization.CODEC.fieldOf("title").forGetter(UpgradeBase::title),
                ComponentSerialization.CODEC.fieldOf("description").forGetter(UpgradeBase::description),
                UPGRADE_RANK_CODEC.optionalFieldOf("max_rank", 1).forGetter(UpgradeBase::maxRank),
                RegistryCodecs.homogeneousList(contextRegistryKey).fieldOf("supported_set").forGetter(UpgradeBase::supportedSet),
                RegistryCodecs.homogeneousList(upgradesRegistryKey).fieldOf("exclusive_set").forGetter(UpgradeBase::exclusiveSet),
                effectCodec.listOf(1, MAX_EFFECTS_PER_UPGRADE).fieldOf("effects").forGetter(UpgradeBase::effects),
                UpgradeIcon.CODEC.optionalFieldOf("icon", UpgradeIcon.DEFAULT_ICON).forGetter(UpgradeBase::icon))
                .apply(instance, factory));
    }

    private void testing4(Enchantment enchantment)
    {
    }

    Component title();

    Component description();

    int maxRank();

    HolderSet<CTX> supportedSet();

    HolderSet<U> exclusiveSet();

    List<E> effects();

    UpgradeIcon icon();

    default MutableComponent getEffectsTooltip(int upgradeRank)
    {
        List<Component> lines = new ObjectArrayList<>();
        effects().forEach(effect -> effect.appendEffectTooltip(upgradeRank, lines));
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

    @FunctionalInterface
    interface UpgradeFactory<CTX, E extends UpgradeEffectBase, U extends UpgradeBase<CTX, E, U>> extends Function7<Component, Component, Integer, HolderSet<CTX>, HolderSet<U>, List<E>, UpgradeIcon, U>
    {
        U newInstance(Component title, Component description, int maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, List<E> effects, UpgradeIcon icon);

        @Override
        default U apply(Component title, Component description, Integer maxRank, HolderSet<CTX> supportedSet, HolderSet<U> exclusiveSet, List<E> effects, UpgradeIcon icon)
        {
            return newInstance(title, description, maxRank, supportedSet, exclusiveSet, effects, icon);
        }
    }
}