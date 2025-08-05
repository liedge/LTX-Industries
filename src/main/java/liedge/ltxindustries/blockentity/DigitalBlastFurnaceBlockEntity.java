package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalBlastFurnaceBlockEntity extends CookingBlockEntity<BlastingRecipe>
{
    public DigitalBlastFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.DIGITAL_BLAST_FURNACE.get(), RecipeType.BLASTING, pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.DIGITAL_BLAST_FURNACE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.DIGITAL_BLAST_FURNACE_CRAFTING_TIME.getAsInt();
    }
}