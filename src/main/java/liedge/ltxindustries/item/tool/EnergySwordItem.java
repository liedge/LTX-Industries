package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.lib.upgrades.Upgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EnergySwordItem extends BaseEnergyMiningItem
{
    @SuppressWarnings("deprecation")
    private static Tool swordTool()
    {
        HolderGetter<Block> holders = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);

        List<Tool.Rule> rules = List.of(
                Tool.Rule.minesAndDrops(HolderSet.direct(Blocks.COBWEB.builtInRegistryHolder()), 15f),
                Tool.Rule.overrideSpeed(holders.getOrThrow(BlockTags.SWORD_INSTANTLY_MINES), Float.MAX_VALUE),
                Tool.Rule.overrideSpeed(holders.getOrThrow(BlockTags.SWORD_EFFICIENT), 1.5f));

        return new Tool(rules, 1f, 1, false);
    }

    public EnergySwordItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties.component(DataComponents.TOOL, swordTool()), attackDamage, attackSpeedModifier(attackSpeed));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return Set.of();
    }

    @Override
    public @Nullable ResourceKey<Upgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.EPSILON_MELEE_DEFAULT;
    }
}