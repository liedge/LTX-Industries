package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.GRINDER_ENERGY_CAPACITY;
import static liedge.ltxindustries.util.config.LTXIMachinesConfig.GRINDER_ENERGY_USAGE;

public class GrinderBlockEntity extends LTXIRecipeMachineBlockEntity.StateMachine<GrindingRecipe>
{
    private int spinSpeed;
    private float crushersRot0;
    private float crushersRot;

    public GrinderBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.GRINDER.get(), LTXIRecipeTypes.GRINDING.get(), pos, state, 1, 3, 0, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return GRINDER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return GRINDER_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (state.getValue(LTXIBlockProperties.BINARY_MACHINE_STATE).isActive())
        {
            if (spinSpeed < 30) spinSpeed += 3;
        }
        else
        {
            if (spinSpeed > 0) spinSpeed = Math.max(0, spinSpeed - 2);
        }

        crushersRot0 = crushersRot;
        crushersRot = (crushersRot + spinSpeed) % 360;
    }

    public float lerpCrushersRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, crushersRot0, crushersRot);
    }
}