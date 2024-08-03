package liedge.limatech.item;

import com.mojang.datafixers.util.Either;
import liedge.limacore.lib.energy.LimaEnergyUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.menu.ItemGridTooltip;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static liedge.limacore.registry.LimaCoreDataComponents.ENERGY;
import static liedge.limacore.registry.LimaCoreDataComponents.ITEM_CONTAINER;
import static liedge.limatech.client.LimaTechLangKeys.ITEM_ENERGY_TOOLTIP;
import static liedge.limatech.client.LimaTechLangKeys.ITEM_INVENTORY_TOOLTIP;

public class MachineBlockItem extends BlockItem implements TooltipShiftHintItem
{
    public MachineBlockItem(Block block, Properties properties)
    {
        super(block, properties);
    }

    @Override
    public void appendTooltipHintComponents(@Nullable Level level, ItemStack stack, Consumer<Either<FormattedText, TooltipComponent>> consumer)
    {
        int energy = stack.getOrDefault(ENERGY, 0);
        if (energy > 0)
        {
            consumer.accept(Either.left(ITEM_ENERGY_TOOLTIP.translateArgs(LimaEnergyUtil.formatEnergyWithSuffix(energy)).withStyle(LimaTechConstants.REM_BLUE::applyStyle)));
        }

        stack.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream();
        List<ItemStack> inventory = stack.getOrDefault(ITEM_CONTAINER, ItemContainerContents.EMPTY).nonEmptyStream().toList();
        if (!inventory.isEmpty())
        {
            consumer.accept(Either.left(ITEM_INVENTORY_TOOLTIP.translate()));
            ItemGridTooltip tooltip = new ItemGridTooltip(inventory, 6);
            consumer.accept(Either.right(tooltip));
        }
    }
}