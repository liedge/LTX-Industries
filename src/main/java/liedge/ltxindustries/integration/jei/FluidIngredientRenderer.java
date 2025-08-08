package liedge.ltxindustries.integration.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import liedge.limacore.capability.fluid.LimaFluidUtil;
import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limacore.util.LimaTextUtil;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

final class FluidIngredientRenderer implements IIngredientRenderer<FluidStack>
{
    static final FluidIngredientRenderer INSTANCE = new FluidIngredientRenderer();

    private FluidIngredientRenderer() {}

    @Override
    public void render(GuiGraphics guiGraphics, FluidStack ingredient)
    {
        LimaGuiUtil.blitTintedFluidSprite(guiGraphics, ingredient, 0, 0);

        PoseStack poseStack = guiGraphics.pose();
        poseStack.pushPose();

        String amountText = LimaFluidUtil.formatCompactFluidAmount(ingredient.getAmount());
        int textWidth = LimaGuiUtil.halfTextWidth(amountText);
        poseStack.translate(16 - textWidth, 11, 2);
        poseStack.scale(0.5f, 0.5f, 0);

        guiGraphics.drawString(Minecraft.getInstance().font, amountText, 0, 0, -1, true);

        poseStack.popPose();
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, FluidStack ingredient, TooltipFlag tooltipFlag)
    {
        tooltip.add(ingredient.getHoverName());
        tooltip.add(Component.translatable("jei.tooltip.liquid.amount", LimaTextUtil.formatWholeNumber(ingredient.getAmount())));

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