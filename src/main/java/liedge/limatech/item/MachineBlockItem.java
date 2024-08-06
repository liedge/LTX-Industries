package liedge.limatech.item;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class MachineBlockItem extends BlockItem implements LimaContainerItem
{
    private final boolean showEnergy;
    private final boolean showItems;

    public MachineBlockItem(Block block, Properties properties, boolean showEnergy, boolean showItems)
    {
        super(block, properties);
        this.showEnergy = showEnergy;
        this.showItems = showItems;
    }

    @Override
    public boolean supportsEnergy(ItemStack stack)
    {
        return showEnergy;
    }

    @Override
    public boolean supportsItemStorage(ItemStack stack)
    {
        return showItems;
    }
}