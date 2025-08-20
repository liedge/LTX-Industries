package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.recipe.ElectroCentrifugingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ElectroCentrifugeBlockEntity extends LTXIRecipeMachineBlockEntity<ElectroCentrifugingRecipe>
{
    private int spinSpeed = 0;
    private float tubesYRot0;
    private float tubesYRot;

    public ElectroCentrifugeBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ELECTROCENTRIFUGE.get(), LTXIRecipeTypes.ELECTRO_CENTRIFUGING.get(), pos, state, 1, 4, 1, 2);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.ELECTROCENTRIFUGE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.ELECTROCENTRIFUGE_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (state.getValue(LTXIBlockProperties.MACHINE_WORKING))
        {
            if (spinSpeed < 30) spinSpeed += 3;

            tubesYRot0 = tubesYRot;
            tubesYRot = (tubesYRot + spinSpeed) % 360;
        }
        else
        {
            if (spinSpeed > 5)
            {
                spinSpeed--;

                tubesYRot0 = tubesYRot;
                tubesYRot = (tubesYRot + spinSpeed) % 360;
            }
            else
            {
                spinSpeed = 0;
                tubesYRot0 = tubesYRot;

                if (tubesYRot % 90 != 0)
                {
                    int n = 90 * Mth.ceil(tubesYRot / 90f);
                    tubesYRot = Mth.approachDegrees(tubesYRot, n, 5);
                }
            }
        }
    }

    public float lerpTubesYRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, tubesYRot0, tubesYRot);
    }
}