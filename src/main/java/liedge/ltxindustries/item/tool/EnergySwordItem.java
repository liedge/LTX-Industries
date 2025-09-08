package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EnergySwordItem extends EnergyMiningToolItem
{
    public EnergySwordItem(Properties properties, float attackDamage, float attackSpeed)
    {
        super(properties.component(DataComponents.TOOL, SwordItem.createToolProperties()), attackDamage, attackSpeed);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS;
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