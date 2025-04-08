package liedge.limatech.item.tool;

import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.Set;

public class EnergyPickaxeItem extends EnergyMiningToolItem
{
    public EnergyPickaxeItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties, attackDamage, attackSpeed, createDefaultTool(BlockTags.MINEABLE_WITH_PICKAXE));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_PICKAXE_ACTIONS;
    }
}