package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class DigitalFurnaceBlockEntity extends SimpleRecipeMachineBlockEntity<SingleRecipeInput, SmeltingRecipe>
{
    public DigitalFurnaceBlockEntity(LimaBlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, DIGITAL_FURNACE_ENERGY_CAPACITY.getAsInt(), 3);
    }

    @Override
    public RecipeType<SmeltingRecipe> machineRecipeType()
    {
        return RecipeType.SMELTING;
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return DIGITAL_FURNACE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return DIGITAL_FURNACE_CRAFTING_TIME.getAsInt();
    }

    @Override
    protected SingleRecipeInput getRecipeInput(Level level)
    {
        return new SingleRecipeInput(getItemHandler().getStackInSlot(1));
    }

    @Override
    protected boolean isInputSlot(int slot)
    {
        return slot == 1;
    }

    @Override
    protected int outputSlotIndex()
    {
        return 2;
    }

    @Override
    protected void consumeIngredients(SingleRecipeInput recipeInput, SmeltingRecipe recipe, Level level)
    {
        getItemHandler().extractItem(1, 1, false);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.DIGITAL_FURNACE.get();
    }
}