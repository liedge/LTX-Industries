package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class MaterialFusingChamberBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, MaterialFusingRecipe>
{
    public MaterialFusingChamberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER.get(), LimaTechRecipeTypes.MATERIAL_FUSING.get(), pos, state, 5);
    }

    @Override
    public int getBaseEnergyCapacity()
    {
        return MFC_ENERGY_CAPACITY.getAsInt();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return MFC_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return MFC_CRAFTING_TIME.getAsInt();
    }

    @Override
    protected LimaRecipeInput getRecipeInput(Level level)
    {
        return LimaRecipeInput.createWithSize(getItemHandler(), 1, 3);
    }

    @Override
    public boolean isInputSlot(int slot)
    {
        return slot > 0 && slot < 4;
    }

    @Override
    public int getOutputSlot()
    {
        return 4;
    }

    @Override
    protected void consumeIngredients(LimaRecipeInput recipeInput, MaterialFusingRecipe recipe, Level level)
    {
        recipe.consumeIngredientsLenientSlots(recipeInput, false);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.MATERIAL_FUSING_CHAMBER.get();
    }
}