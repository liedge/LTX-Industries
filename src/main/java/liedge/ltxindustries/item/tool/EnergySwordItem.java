package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.item.LTXIItemAbilities;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EnergySwordItem extends EnergyBreakerToolItem
{
    public EnergySwordItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties, attackDamage, attackSpeed, null, ignored -> List.of(
                Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15f),
                Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5f)));
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return LTXIItemAbilities.SWORD_NO_SWEEP;
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.LTX_MELEE_DEFAULT;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player)
    {
        return !player.isCreative();
    }
}