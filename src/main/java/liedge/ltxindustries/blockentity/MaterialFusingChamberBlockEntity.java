package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import static liedge.ltxindustries.util.config.LTXIMachinesConfig.*;

public class MaterialFusingChamberBlockEntity extends SimpleRecipeMachineBlockEntity<LimaRecipeInput, MaterialFusingRecipe>
{
    public MaterialFusingChamberBlockEntity(BlockPos pos, BlockState state)
    {
        super(LTXIBlockEntities.MATERIAL_FUSING_CHAMBER.get(), LTXIRecipeTypes.MATERIAL_FUSING.get(), pos, state, 5);
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
        return LTXIMenus.MATERIAL_FUSING_CHAMBER.get();
    }
}