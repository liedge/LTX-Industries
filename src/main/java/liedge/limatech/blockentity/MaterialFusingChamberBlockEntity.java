package liedge.limatech.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.recipe.MaterialFusingRecipe;
import liedge.limatech.registry.game.LimaTechBlockEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.limatech.util.config.LimaTechMachinesConfig.*;

public class MaterialFusingChamberBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, MaterialFusingRecipe>
{
    public MaterialFusingChamberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LimaTechBlockEntities.MATERIAL_FUSING_CHAMBER.get(), pos, state, MFC_ENERGY_CAPACITY.getAsInt(), 5);
    }

    @Override
    public RecipeType<MaterialFusingRecipe> machineRecipeType()
    {
        return LimaTechRecipeTypes.MATERIAL_FUSING.get();
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
        return new LimaRecipeInput(getItemHandler(), 3, 1);
    }

    @Override
    protected boolean isInputSlot(int slot)
    {
        return slot >= 1 && slot <= 4;
    }

    @Override
    protected int outputSlotIndex()
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