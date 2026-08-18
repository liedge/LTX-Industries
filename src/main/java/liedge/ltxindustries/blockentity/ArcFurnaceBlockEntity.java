package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.blockentity.template.LTXIRecipeMachineBlockEntity;
import liedge.ltxindustries.recipe.ArcSmeltingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.ARC_FURNACE_ENERGY_CAPACITY;
import static liedge.ltxindustries.util.config.LTXIMachinesConfig.ARC_FURNACE_ENERGY_USAGE;

public class ArcFurnaceBlockEntity extends LTXIRecipeMachineBlockEntity<ArcSmeltingRecipe>
{
    public ArcFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.ARC_FURNACE.get(), LTXIRecipeTypes.ARC_SMELTING.get(), pos, state, 3, 1, 1, 0);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return ARC_FURNACE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return ARC_FURNACE_ENERGY_USAGE.getAsInt();
    }
}