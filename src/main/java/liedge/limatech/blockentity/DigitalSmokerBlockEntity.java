package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalSmokerBlockEntity extends BaseCookingBlockEntity<SmokingRecipe>
{
    public DigitalSmokerBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.DIGITAL_SMOKER.get(), RecipeType.SMOKING, pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LimaTechMachinesConfig.DIGITAL_SMOKER_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.DIGITAL_SMOKER.get();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LimaTechMachinesConfig.DIGITAL_SMOKER_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LimaTechMachinesConfig.DIGITAL_SMOKER_CRAFTING_TIME.getAsInt();
    }
}