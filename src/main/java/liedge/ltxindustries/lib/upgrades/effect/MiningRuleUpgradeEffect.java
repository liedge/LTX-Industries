package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;

public record MiningRuleUpgradeEffect(Optional<HolderSet<Block>> effectiveBlocks, Optional<HolderSet<Block>> deniedBlocks, Optional<Float> miningSpeed, int priority) implements UpgradeTooltipsProvider, Comparable<MiningRuleUpgradeEffect>
{
    public static final Codec<MiningRuleUpgradeEffect> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("effective_blocks").forGetter(MiningRuleUpgradeEffect::effectiveBlocks),
            RegistryCodecs.homogeneousList(Registries.BLOCK).optionalFieldOf("denied_blocks").forGetter(MiningRuleUpgradeEffect::deniedBlocks),
            Codec.floatRange(1f, 99f).optionalFieldOf("mining_speed").forGetter(MiningRuleUpgradeEffect::miningSpeed),
            Codec.intRange(1, 99).fieldOf("priority").forGetter(MiningRuleUpgradeEffect::priority))
            .apply(instance, MiningRuleUpgradeEffect::new));

    public static MiningRuleUpgradeEffect miningLevelAndSpeed(HolderSet<Block> deniedBlocks, float miningSpeed, int priority)
    {
        return new MiningRuleUpgradeEffect(Optional.empty(), Optional.of(deniedBlocks), Optional.of(miningSpeed), priority);
    }

    @Override
    public int compareTo(@NotNull MiningRuleUpgradeEffect o)
    {
        return Integer.compare(o.priority, this.priority);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        effectiveBlocks.ifPresent(set -> lines.accept(LTXILangKeys.MINING_EFFECTIVE_BLOCKS_EFFECT.translateArgs(LTXITooltipUtil.translateHolderSet(set).withStyle(ChatFormatting.GREEN))));
        miningSpeed.ifPresent(speed -> lines.accept(LTXILangKeys.MINING_BASE_SPEED_EFFECT.translateArgs(LTXITooltipUtil.flatNumberWithoutSign(speed).withStyle(ChatFormatting.GREEN))));
    }
}