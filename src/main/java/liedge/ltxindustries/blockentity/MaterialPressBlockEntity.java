package liedge.ltxindustries.blockentity;

import liedge.limacore.lib.TickTimer;
import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.PressingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MaterialPressBlockEntity extends LTXIRecipeMachineBlockEntity<PressingRecipe>
{
    public final TickTimer animationTimer = new TickTimer();

    public MaterialPressBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MATERIAL_PRESS.get(), LTXIRecipeTypes.PRESSING.get(), pos, state, 2, 2, 1, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.MATERIAL_PRESS_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.MATERIAL_PRESS_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (LTXIBlockProperties.isMachineActive(state) && !animationTimer.isRunningClient())
        {
            animationTimer.startTimer(25);
        }

        animationTimer.tickTimer();
    }
}