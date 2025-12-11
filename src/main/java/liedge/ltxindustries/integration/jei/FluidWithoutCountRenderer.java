package liedge.ltxindustries.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.util.LimaRegistryUtil;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

final class FluidWithoutCountRenderer implements IIngredientRenderer<FluidStack>
{
    static final FluidWithoutCountRenderer INSTANCE = new FluidWithoutCountRenderer();

    private FluidWithoutCountRenderer() { }

    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient)
    {
        render(guiGraphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient, int posX, int posY)
    {
        LimaGuiUtil.renderFluid(guiGraphics, ingredient, posX, posY);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidStack ingredient, TooltipFlag tooltipFlag)
    {
        tooltip.add(ingredient.getHoverName());

        if (tooltipFlag.isAdvanced())
        {
            ResourceLocation id = LimaRegistryUtil.getNonNullRegistryId(ingredient.getFluidHolder());
            tooltip.add(Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @SuppressWarnings("removal")
    @Override
    public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag)
    {
        return List.of();
    }
}