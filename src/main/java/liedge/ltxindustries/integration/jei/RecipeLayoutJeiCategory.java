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
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.Nullable;

import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class RecipeLayoutJeiCategory<R extends LTXIRecipe> extends LTXIRecipeHolderCategory<R>
{
    private static final int PADDING = 3;

    static ScreenRectangle layoutBounds(RecipeLayout layout)
    {
        IntSummaryStatistics xss = layout.streamSlots().collect(Collectors.summarizingInt(LayoutSlot::x));
        IntSummaryStatistics yss = layout.streamSlots().collect(Collectors.summarizingInt(LayoutSlot::y));

        int width = (xss.getMax() - xss.getMin()) + 18 + PADDING * 2;
        int height = (yss.getMax() - yss.getMin()) + 29 + PADDING * 2;
        int xOffset = xss.getMin() - PADDING - 1;
        int yOffset = yss.getMin() - PADDING - 1;

        return new ScreenRectangle(xOffset, yOffset, width, height);
    }

    static <R, I> void addLayoutInputs(IRecipeLayoutBuilder builder, RecipeLayout layout, ScreenRectangle bounds, LayoutSlot.Type slotType, R recipe, Function<R, List<I>> inputsFunction, LayoutInputAcceptor<I> inputAcceptor)
    {
        List<LayoutSlot> slots = layout.getSlotsForType(slotType);
        List<I> inputs = inputsFunction.apply(recipe);

        int max = Math.min(slots.size(), inputs.size());
        for (int i = 0; i < max; i++)
        {
            LayoutSlot slot = slots.get(i);
            I input = inputs.get(i);
            inputAcceptor.accept(builder, input, slot.x() - bounds.left(), slot.y() - bounds.top());
        }
    }

    static <R extends LTXIRecipe> RecipeLayoutJeiCategory<R> create(IGuiHelper helper, Supplier<LimaRecipeType<R>> typeSupplier, IRecipeHolderType<R> jeiRecipeType, RecipeLayout layout)
    {
        return new RecipeLayoutJeiCategory<>(helper, typeSupplier.get(), jeiRecipeType, layout, layoutBounds(layout));
    }

    private final IRecipeHolderType<R> jeiRecipeType;
    private final RecipeLayout layout;
    private final ScreenRectangle bounds;

    // Mode stuff
    private final IDrawableStatic modeBackground;
    private final IDrawableStatic modeOverlay;
    private final @Nullable ScreenPosition modePos;

    private RecipeLayoutJeiCategory(IGuiHelper helper, LimaRecipeType<R> recipeType, IRecipeHolderType<R> jeiRecipeType, RecipeLayout layout, ScreenRectangle bounds)
    {
        super(helper, recipeType, bounds.width(), bounds.height());
        this.jeiRecipeType = jeiRecipeType;
        this.layout = layout;
        this.bounds = bounds;

        this.modeBackground = guiSpriteDrawable(LayoutSlot.Type.RECIPE_MODE.getSprite(), 18, 18).build();
        this.modeOverlay = guiSpriteDrawable(RecipeLayoutScreen.MODE_OVERLAY_SPRITE, 16, 16).build();
        this.modePos = layout.streamSlots().filter(o -> o.type() == LayoutSlot.Type.RECIPE_MODE).findFirst().map(o -> new ScreenPosition(o.x() - bounds.left(), o.y() - bounds.top())).orElse(null);
    }

    private <T> void addLayoutInputs(IRecipeLayoutBuilder builder, LayoutSlot.Type slotType, R recipe, Function<R, List<T>> inputsExtractor, LayoutInputAcceptor<T> acceptor)
    {
        addLayoutInputs(builder, layout, bounds, slotType, recipe, inputsExtractor, acceptor);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        addLayoutInputs(builder, LayoutSlot.Type.ITEM_INPUT, recipe, R::getItemInputs, this::itemInputSlot);
        addLayoutInputs(builder, LayoutSlot.Type.FLUID_INPUT, recipe, R::getFluidInputs, this::fluidInputSlot);
        addLayoutInputs(builder, LayoutSlot.Type.ITEM_OUTPUT, recipe, R::getItemResults, this::itemResultSlot);
        addLayoutInputs(builder, LayoutSlot.Type.FLUID_OUTPUT, recipe, R::getFluidResults, this::fluidResultSlot);
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

        if (modePos != null) builder.addWidget(new RecipeModeWidget(modePos, modeBackground, modeOverlay, recipeHolder.value().getMode()));
    }

    @Override
    public void draw(RecipeHolder<R> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        super.draw(holder, recipeSlotsView, graphics, mouseX, mouseY);

        RecipeLayoutScreen.renderLayout(graphics, -bounds.left(), -bounds.top(), layout);

        int px = layout.progressBarX() - bounds.left();
        int py = layout.progressBarY() - bounds.top();

        machineProgressBackground.draw(graphics, px, py);
        machineProgress.draw(graphics, px + 1, py + 1);
    }

    @Override
    public IRecipeHolderType<R> getRecipeType()
    {
        return jeiRecipeType;
    }

    @FunctionalInterface
    public interface LayoutInputAcceptor<T>
    {
        void accept(IRecipeLayoutBuilder builder, T input, int x, int y);
    }
}