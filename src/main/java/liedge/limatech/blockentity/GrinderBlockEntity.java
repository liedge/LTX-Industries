package liedge.limatech.blockentity;

import liedge.limacore.blockentity.LimaBlockEntityType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limacore.util.LimaItemUtil;
import liedge.limatech.recipe.GrindingRecipe;
import liedge.limatech.registry.LimaTechCrafting;
import liedge.limatech.registry.LimaTechMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;

public class GrinderBlockEntity extends BasicRecipeMachineBlockEntity.LimaRecipeMachine<GrindingRecipe>
{
    public GrinderBlockEntity(LimaBlockEntityType type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, 250_000, 10_000, 3);
    }

    @Override
    public RecipeType<GrindingRecipe> machineRecipeType()
    {
        return LimaTechCrafting.GRINDING_TYPE.get();
    }

    @Override
    public int machineEnergyUse()
    {
        return 1000;
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
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return slot != 0 || LimaItemUtil.ENERGY_ITEMS.test(stack);
    }

    @Override
    public LimaMenuType<?, ?> getMenuType()
    {
        return LimaTechMenus.GRINDER.get();
    }

    @Override
    public int getMachineProcessTime()
    {
        return 200;
    }

    @Override
    public int inventorySlotForIngredient(int ingredientIndex)
    {
        return 1;
    }

    @Override
    public int size()
    {
        return 1;
    }
}