package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.block.LTXIBlockProperties;
import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.AirScrubbingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class AirScrubberBlockEntity extends LTXIRecipeMachineBlockEntity<AirScrubbingRecipe>
{
    // Remote properties
    private int spinSpeed;
    private float impellerRot0;
    private float impellerRot;

    public AirScrubberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ATMOSPHERIC_SCRUBBER.get(), LTXIRecipeTypes.AIR_SCRUBBING.get(), pos, state, 0, 2, 0, 4);
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

        impellerRot0 = impellerRot;
        impellerRot = (impellerRot + spinSpeed) % 360;
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.AIR_SCRUBBER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.AIR_SCRUBBER_ENERGY_USAGE.getAsInt();
    }

    public float lerpImpellerRot(float partialTick)
    {
        return Mth.rotLerp(partialTick, impellerRot0, impellerRot);
    }
}