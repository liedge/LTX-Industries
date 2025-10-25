package liedge.ltxindustries.integration.jei;

import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.screen.RecipeLayoutScreen;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.recipe.LTXIRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class RecipeLayoutJeiCategory<R extends LTXIRecipe> extends LTXIJeiCategory<R>
{
    private static final int PADDING = 3;

    static <R extends LTXIRecipe> RecipeLayoutJeiCategory<R> create(IGuiHelper helper, Supplier<LimaRecipeType<R>> typeSupplier, RecipeType<RecipeHolder<R>> jeiRecipeType, RecipeLayout layout)
    {
        IntSummaryStatistics xss = layout.streamSlots().collect(Collectors.summarizingInt(LayoutSlot::x));
        IntSummaryStatistics yss = layout.streamSlots().collect(Collectors.summarizingInt(LayoutSlot::y));

        int width = (xss.getMax() - xss.getMin()) + 18 + PADDING * 2;
        int height = (yss.getMax() - yss.getMin()) + 29 + PADDING * 2;
        int xOffset = xss.getMin() - PADDING - 1;
        int yOffset = yss.getMin() - PADDING - 1;

        return new RecipeLayoutJeiCategory<>(helper, typeSupplier.get(), jeiRecipeType, layout, width, height, xOffset, yOffset);
    }

    private final RecipeType<RecipeHolder<R>> jeiRecipeType;
    private final RecipeLayout layout;
    private final int xOffset;
    private final int yOffset;

    private RecipeLayoutJeiCategory(IGuiHelper helper, LimaRecipeType<R> recipeType, RecipeType<RecipeHolder<R>> jeiRecipeType, RecipeLayout layout, int width, int height, int xOffset, int yOffset)
    {
        super(helper, recipeType, width, height);
        this.jeiRecipeType = jeiRecipeType;
        this.layout = layout;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        for (LayoutSlot.Type slotType : LayoutSlot.Type.values())
        {
            List<LayoutSlot> layoutSlots = layout.getSlotsForType(slotType);
            int slotMax = switch(slotType)
            {
                case ITEM_INPUT -> recipe.getItemIngredients().size();
                case FLUID_INPUT -> recipe.getFluidIngredients().size();
                case ITEM_OUTPUT -> recipe.getItemResults().size();
                case FLUID_OUTPUT -> recipe.getFluidResults().size();
                case RECIPE_MODE -> 1;
            };
            slotMax = Math.min(layoutSlots.size(), slotMax);

            for (int i = 0; i < slotMax; i++)
            {
                LayoutSlot s = layoutSlots.get(i);
                int sx = s.x() - xOffset;
                int sy = s.y() - yOffset;

                switch (s.type())
                {
                    case ITEM_INPUT -> sizedIngredientsSlot(builder, recipe, i, sx, sy);
                    case ITEM_OUTPUT -> itemResultSlot(builder, recipe, i, sx, sy);
                    case FLUID_INPUT -> fluidIngredientSlot(builder, recipe, i, sx, sy);
                    case FLUID_OUTPUT -> fluidResultSlot(builder, recipe, i, sx, sy);
                }
            }
        }
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<R> recipeHolder, IFocusGroup focuses)
    {
        int timeInTicks = recipeHolder.value().getCraftTime();
        String timeInSeconds = LimaTextUtil.format2PlaceDecimal(timeInTicks / 20d);

        Component craftTimeComponent = LTXILangKeys.JEI_CRAFTING_TIME_TOOLTIP.translateArgs(timeInSeconds, timeInTicks);
        builder.addText(craftTimeComponent, getWidth() - (PADDING * 2), 10)
                .setPosition(PADDING, getHeight() - PADDING - 10)
                .setTextAlignment(HorizontalAlignment.LEFT)
                .setTextAlignment(VerticalAlignment.BOTTOM)
                .setColor(LTXIConstants.LIME_GREEN.argb32());
    }

    @Override
    protected void drawRecipe(RecipeHolder<R> recipeHolder, IRecipeSlotsView view, GuiGraphics graphics, double mouseX, double mouseY)
    {
        RecipeLayoutScreen.renderLayout(graphics, -xOffset, -yOffset, layout);

        int px = layout.progressBarX() - xOffset;
        int py = layout.progressBarY() - yOffset;

        machineProgressBackground.draw(graphics, px, py);
        machineProgress.draw(graphics, px + 1, py + 1);
    }

    @Override
    public RecipeType<RecipeHolder<R>> getRecipeType()
    {
        return jeiRecipeType;
    }
}