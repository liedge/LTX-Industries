package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limacore.util.LimaTextUtil;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

final class FluidWithCountRenderer implements IIngredientRenderer<FluidStack>
{
    static final FluidWithCountRenderer INSTANCE = new FluidWithCountRenderer();

    private FluidWithCountRenderer() {}

    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient)
    {
        render(guiGraphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient, int posX, int posY)
    {
        LimaGuiUtil.renderFluidWithAmount(guiGraphics, ingredient, posX, posY);
    }

    @Override
    public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag)
    {
        List<Component> lines = new ObjectArrayList<>();

        lines.add(ingredient.getHoverName());
        lines.add(Component.translatable("jei.tooltip.liquid.amount", LimaTextUtil.formatWholeNumber(ingredient.getAmount())));

        if (tooltipFlag.isAdvanced())
        {
            Identifier id = LimaRegistryUtil.getNonNullRegistryId(ingredient.getFluidHolder());
            lines.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        return lines;
    }
}