package liedge.ltxindustries.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.component.Tool;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;
import java.util.Set;

public class EnergyDrillItem extends EnergyBreakerToolItem
{
    public EnergyDrillItem(Properties properties, float poweredAttackDamage, float attackSpeed)
    {
        super(properties, poweredAttackDamage, attackSpeed, Tool.Rule.deniesDrops(BlockTags.INCORRECT_FOR_DIAMOND_TOOL), speed -> List.of(
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_PICKAXE, speed),
                Tool.Rule.minesAndDrops(BlockTags.MINEABLE_WITH_SHOVEL, speed)));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS;
    }
}