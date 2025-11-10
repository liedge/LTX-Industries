package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.GeoSynthesisRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GeoSynthesizerBlockEntity extends LTXIRecipeMachineBlockEntity.StateMachine<GeoSynthesisRecipe>
{
    public GeoSynthesizerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.GEO_SYNTHESIZER.get(), LTXIRecipeTypes.GEO_SYNTHESIS.get(), pos, state, 1, 1, 2, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.GEO_SYNTHESIZER_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.GEO_SYNTHESIZER_ENERGY_USAGE.getAsInt();
    }
}