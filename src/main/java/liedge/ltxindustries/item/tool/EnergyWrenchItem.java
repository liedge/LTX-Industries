package liedge.ltxindustries.item.tool;

import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.item.LTXIItemAbilities;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbility;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class EnergyWrenchItem extends BaseEnergyMiningItem
{
    private static Tool toolProperties()
    {
        return new Tool(List.of(Tool.Rule.minesAndDrops(LTXITags.Blocks.WRENCH_BREAKABLE, 15f)),
                1f, 1);
    }

    public EnergyWrenchItem(Properties properties)
    {
        super(properties.component(DataComponents.TOOL, toolProperties()), 0f, ItemAttributeModifiers.EMPTY);
    }

    @Override
    protected Set<ItemAbility> getAvailableAbilities()
    {
        return LTXIItemAbilities.WRENCH_ABILITIES;
    }

    @Override
    public @Nullable ResourceKey<EquipmentUpgrade> getDefaultUpgradeKey()
    {
        return LTXIEquipmentUpgrades.LTX_WRENCH_DEFAULT;
    }

    @Override
    protected InteractionResult useToolOn(UseOnContext context, Level level, BlockPos pos, BlockState state, @Nullable Player player, ItemStack stack)
    {
        // Determine tool action
        boolean isSneaking = player != null && player.isShiftKeyDown();
        ItemAbility ability = isSneaking ? LTXIItemAbilities.WRENCH_DISMANTLE : LTXIItemAbilities.WRENCH_ROTATE;

        // See if we actually modified the state
        BlockState modified = state.getToolModifiedState(context, ability, false);
        if (modified == null) return InteractionResult.PASS;

        Holder<GameEvent> gameEvent = isSneaking ? GameEvent.BLOCK_DESTROY : GameEvent.BLOCK_CHANGE;

        if (!level.isClientSide())
        {
            level.setBlock(pos, modified, Block.UPDATE_ALL_IMMEDIATE);
            level.gameEvent(gameEvent, pos, GameEvent.Context.of(player, modified));

            if (isSneaking) consumeEnergyAction(player, stack); // Use energy for dismantling only
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}