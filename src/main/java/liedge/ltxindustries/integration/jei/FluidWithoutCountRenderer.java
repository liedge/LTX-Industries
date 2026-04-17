package liedge.ltxindustries.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.util.LimaRegistryUtil;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

final class FluidWithoutCountRenderer implements IIngredientRenderer<FluidStack>
{
    static final FluidWithoutCountRenderer INSTANCE = new FluidWithoutCountRenderer();

    private FluidWithoutCountRenderer() { }

    @Override
    public void render(GuiGraphicsExtractor graphics, FluidStack ingredient)
    {
        render(graphics, ingredient, 0, 0);
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, FluidStack ingredient, int posX, int posY)
    {
        LimaGuiUtil.fluidSprite(graphics, ingredient, posX, posY);
    }

    @Override
    public List<Component> getTooltip(FluidStack ingredient, TooltipFlag tooltipFlag)
    {
        Component name = ingredient.getHoverName();

        if (tooltipFlag.isAdvanced())
        {
            Identifier id = LimaRegistryUtil.getNonNullRegistryId(ingredient.typeHolder());
            return List.of(name, Component.literal(id.toString()).withStyle(ChatFormatting.DARK_GRAY));
        }
        else
        {
            return List.of(name);
        }
    }
}