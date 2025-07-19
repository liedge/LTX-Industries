package liedge.ltxindustries.blockentity;

import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.ltxindustries.registry.game.LTXIBlockEntities;
import liedge.ltxindustries.registry.game.LTXIMenus;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;

public class DigitalFurnaceBlockEntity extends BaseCookingBlockEntity<SmeltingRecipe>
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
    public LimaMenuType<?, ?> getMenuType()
    {
        return LTXIMenus.DIGITAL_FURNACE.get();
    }

    @Override
    public int getBaseEnergyUsage()
    {
        return LTXIMachinesConfig.DIGITAL_FURNACE_ENERGY_USAGE.getAsInt();
    }

    @Override
    public int getBaseTicksPerOperation()
    {
        return LTXIMachinesConfig.DIGITAL_FURNACE_CRAFTING_TIME.getAsInt();
    }
}