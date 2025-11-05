package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.AssemblingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class AssemblerBlockEntity extends LTXIRecipeMachineBlockEntity.StateMachine<AssemblingRecipe>
{
    public AssemblerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ASSEMBLER.get(), LTXIRecipeTypes.ASSEMBLING.get(), pos, state, 6, 1, 1, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.ASSEMBLER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.ASSEMBLER_ENERGY_USAGE.getAsInt();
    }
}