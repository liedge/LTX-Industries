package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.recipe.MixingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MixerBlockEntity extends LimaRecipeMachineBlockEntity<MixingRecipe>
{
    private int spinSpeed = 0;
    private float impellerYRot0;
    private float impellerYRot;

    public MixerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MIXER.get(), LTXIRecipeTypes.MIXING.get(), pos, state, 4, 1, 2, 1);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.MIXER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.MIXER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.MIXER_CRAFTING_TIME.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (state.getValue(LTXIBlockProperties.MACHINE_WORKING))
        {
            if (spinSpeed < 40) spinSpeed += 4;

            impellerYRot0 = impellerYRot;
            impellerYRot = (impellerYRot + spinSpeed) % 360;
        }
        else
        {
            if (spinSpeed > 5)
            {
                spinSpeed--;

                impellerYRot0 = impellerYRot;
                impellerYRot = (impellerYRot + spinSpeed) % 360;
            }
            else
            {
                spinSpeed = 0;
                impellerYRot0 = impellerYRot;

                if (impellerYRot % 90 != 0)
                {
                    int n = 90 * Mth.ceil(impellerYRot / 90f);
                    impellerYRot = Mth.approachDegrees(impellerYRot, n, 5);
                }
            }
        }
    }

    public float lerpImpellerYRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, impellerYRot0, impellerYRot);
    }
}