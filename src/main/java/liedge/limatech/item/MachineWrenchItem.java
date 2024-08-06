package liedge.limatech.item;

import liedge.limatech.block.BaseWrenchEntityBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MachineWrenchItem extends Item
{
    public MachineWrenchItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Player player = context.getPlayer();
        Level level = context.getLevel();

        if (player != null)
        {
            BlockPos pos = context.getClickedPos();
            BlockState state = level.getBlockState(pos);

            if (state.getBlock() instanceof BaseWrenchEntityBlock wrenchEntityBlock)
            {
                return wrenchEntityBlock.useWrenchOnBlock(context, player, level, pos, state);
            }
        }

        return super.useOn(context);
    }
}