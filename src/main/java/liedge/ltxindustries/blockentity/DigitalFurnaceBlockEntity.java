package liedge.ltxindustries.blockentity;

import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalFurnaceBlockEntity extends CookingBlockEntity<SmeltingRecipe>
{
    public DigitalFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.DIGITAL_FURNACE.get(), RecipeType.SMELTING, pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LTXIMachinesConfig.DIGITAL_FURNACE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.DIGITAL_FURNACE_ENERGY_USAGE.getAsInt();
    }
}