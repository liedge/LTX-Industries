package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalSmokerBlockEntity extends CookingBlockEntity<SmokingRecipe>
{
    public DigitalSmokerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.DIGITAL_SMOKER.get(), RecipeType.SMOKING, pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.DIGITAL_SMOKER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.DIGITAL_SMOKER_ENERGY_USAGE.getAsInt();
    }
}