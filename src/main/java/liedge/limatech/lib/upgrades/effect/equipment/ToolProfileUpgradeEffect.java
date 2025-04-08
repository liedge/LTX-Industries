package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.Codec;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record ToolProfileUpgradeEffect(Tool tool) implements EffectTooltipProvider
{
    public static Codec<ToolProfileUpgradeEffect> CODEC = Tool.CODEC.xmap(ToolProfileUpgradeEffect::new, ToolProfileUpgradeEffect::tool);

    public static ToolProfileUpgradeEffect alwaysMinesAndDrops(HolderSet<Block> blocks, float miningSpeed)
    {
        Tool.Rule rule = new Tool.Rule(blocks, Optional.of(miningSpeed), Optional.of(true));
        Tool tool = new Tool(List.of(rule), 1, 1);
        return new ToolProfileUpgradeEffect(tool);
    }

    public static ToolProfileUpgradeEffect minesAndDropsWithDenial(HolderSet<Block> blocks, HolderSet<Block> denials, float miningSpeed)
    {
        Tool.Rule allowRule = new Tool.Rule(blocks, Optional.of(miningSpeed), Optional.of(true));
        Tool.Rule denyRule = new Tool.Rule(denials, Optional.empty(), Optional.of(false));
        Tool tool = new Tool(List.of(allowRule, denyRule), 1, 1);
        return new ToolProfileUpgradeEffect(tool);
    }

    // Too complex to attempt. Use upgrade description
    @Override
    public void appendEffectLines(int upgradeRank, Consumer<Component> linesConsumer) {}
}