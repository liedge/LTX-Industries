package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.SievingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class HydroSieveBlockEntity extends LTXIRecipeMachineBlockEntity<SievingRecipe>
{
    // Remote properties
    private int spinSpeed = 0;
    private float impellerYRot0;
    private float impellerYRot;

    public HydroSieveBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.HYDROSIEVE.get(), LTXIRecipeTypes.SIEVING.get(), pos, state, 1, 6, 1, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.HYDROSIEVE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.HYDROSIEVE_ENERGY_USAGE.getAsInt();
    }

    @Override
    protected void tickClient(Level level, BlockPos pos, BlockState state)
    {
        if (LTXIBlockProperties.isMachineActive(state))
        {
            if (spinSpeed < 15) spinSpeed += 3;
        }
        else
        {
            if (spinSpeed > 0) spinSpeed = Math.max(0, spinSpeed - 3);
        }

        impellerYRot0 = impellerYRot;
        impellerYRot = (impellerYRot + spinSpeed) % 360;
    }

    public float lerpRotation(float partialTick)
    {
        return Mth.rotLerp(partialTick, impellerYRot0, impellerYRot);
    }
}