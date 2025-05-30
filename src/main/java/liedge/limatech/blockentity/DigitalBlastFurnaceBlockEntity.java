package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalBlastFurnaceBlockEntity extends BaseCookingBlockEntity<BlastingRecipe>
{
    public DigitalBlastFurnaceBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.DIGITAL_BLAST_FURNACE.get(), RecipeType.BLASTING, pos, state);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return LimaTechMachinesConfig.DIGITAL_BLAST_FURNACE_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.DIGITAL_BLAST_FURNACE.get();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LimaTechMachinesConfig.DIGITAL_BLAST_FURNACE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LimaTechMachinesConfig.DIGITAL_BLAST_FURNACE_CRAFTING_TIME.getAsInt();
    }
}