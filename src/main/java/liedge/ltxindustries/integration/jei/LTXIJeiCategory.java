package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.input.RecipeStackInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.RecipeResult;
import liedge.limacore.recipe.result.ResultCount;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.widget.MachineProgressWidget;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableBuilder;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.display.ForFluidStacks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class LTXIJeiCategory<R extends LimaCustomRecipe<?>> implements IRecipeCategory<RecipeHolder<R>>
{
    protected final IGuiHelper helper;
    private final int width;
    private final int height;
    private final Component title;

    // Commonly used drawables
    protected final IDrawableStatic machineProgressBackground;
    protected final IDrawableAnimated machineProgress;

    protected LTXIJeiCategory(IGuiHelper helper, LimaRecipeType<R> gameRecipeType, int width, int height)
    {
        this.helper = helper;
        this.width = width;
        this.height = height;
        this.title = gameRecipeType.translate();

        this.machineProgressBackground = guiSpriteDrawable(MachineProgressWidget.BACKGROUND_SPRITE, MachineProgressWidget.BACKGROUND_WIDTH, MachineProgressWidget.BACKGROUND_HEIGHT).build();
        this.machineProgress = guiSpriteDrawable(MachineProgressWidget.FILL_SPRITE, MachineProgressWidget.FILL_WIDTH, MachineProgressWidget.FILL_HEIGHT).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

    protected LTXIJeiCategory(IGuiHelper helper, Supplier<? extends LimaRecipeType<R>> typeSupplier, int width, int height)
    {
        this(helper, typeSupplier.get(), width, height);
    }

    protected abstract void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, R recipe, IFocusGroup focuses, RegistryAccess registries);

    protected abstract void drawRecipe(RecipeHolder<R> recipeHolder, IRecipeSlotsView view, GuiGraphicsExtractor graphics, double mouseX, double mouseY);

    protected final RegistryAccess localRegistryAccess()
    {
        return Objects.requireNonNull(Minecraft.getInstance().level, "Minecraft local level is null").registryAccess();
    }

    protected Identifier unwrapGuiSpriteTexture(Identifier spriteLocation)
    {
        return spriteLocation.withPath(path -> String.format("textures/gui/sprites/%s.png", path));
    }

    protected IDrawableBuilder guiSpriteDrawable(Identifier spriteLocation, int width, int height)
    {
        return helper.drawableBuilder(unwrapGuiSpriteTexture(spriteLocation), 0, 0, width, height).setTextureSize(width, height);
    }

    private void randomInputOverlay(IRecipeSlotBuilder slot, RecipeStackInput<?, ?> input)
    {
        if (input.consumeChance() == 0)
        {
            slot.setOverlay(ScaledFontDrawable.withStyle("NC", ChatFormatting.GREEN, 0.5f), 1, 1)
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.INPUT_NOT_CONSUMED_TOOLTIP.translate().withStyle(ChatFormatting.GREEN)));
        }
        else
        {
            String formattedChance = LimaTextUtil.format1PlacePercentage(input.consumeChance());
            slot.setOverlay(ScaledFontDrawable.withStyle(formattedChance, ChatFormatting.YELLOW, 0.5f), 1, 1)
                    .addRichTooltipCallback((view, lines) -> lines.add(LTXILangKeys.INPUT_CONSUME_CHANCE_TOOLTIP.translateArgs(formattedChance).withStyle(ChatFormatting.YELLOW)));
        }
    }

    private ContextMap makeContext()
    {
        return SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));
    }

    private void addFluidStack(IRecipeSlotBuilder slot, FluidStack stack)
    {
        slot.add(stack.getFluid(), stack.getAmount(), stack.getComponentsPatch());
    }

    private <T> void addFluidStacks(IRecipeSlotBuilder slot, T source, Function<T, SlotDisplay> displayGetter, Function<T, ForFluidStacks<FluidStack>> resolverGetter)
    {
        SlotDisplay display = displayGetter.apply(source);
        ForFluidStacks<FluidStack> resolver = resolverGetter.apply(source);
        display.resolve(makeContext(), resolver).forEach(stack -> addFluidStack(slot, stack));
    }

    protected void itemInputSlot(IRecipeLayoutBuilder builder, R recipe, int index, int x, int y)
    {
        RecipeItemInput input = recipe.getItemInput(index);
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).addItemStacks(input.ingredient().display().resolve(makeContext(), input.displayResolver()).toList());

        if (input.isRandom()) randomInputOverlay(slot, input);
    }

    protected void itemInputSlotGrid(IRecipeLayoutBuilder builder, R recipe, int x, int y, int columns)
    {
        List<RecipeItemInput> inputs = recipe.getItemInputs();
        for (int i = 0; i < inputs.size(); i++)
        {
            int sx = x + (i % columns) * 18;
            int sy = y + (i / columns) * 18;
            itemInputSlot(builder, recipe, i, sx, sy);
        }
    }

    protected void fluidInputSlot(IRecipeLayoutBuilder builder, R recipe, int index, int x, int y)
    {
        RecipeFluidInput input = recipe.getFluidInput(index);
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidWithCountRenderer.INSTANCE);

        addFluidStacks(slot, input, o -> o.ingredient().display(), RecipeFluidInput::displayResolver);

        if (input.isRandom()) randomInputOverlay(slot, input);
    }

    protected void itemResultSlot(IRecipeLayoutBuilder builder, R recipe, int index, int x, int y)
    {
        ItemResult result = recipe.getItemResult(index);
        ResultCount count = result.count();

        int displayCount = count.isConstant() ? count.max() : 1;
        IRecipeSlotBuilder slot = builder.addOutputSlot(x, y).addItemStacks(List.of(result.display(displayCount)));

        List<Component> tooltipLines = new ObjectArrayList<>();
        resultRequirementTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(count, tooltipLines);
        IDrawable countOverlay = resultCountOverlay(count, tooltipLines, Component::literal);

        slot.addRichTooltipCallback((_, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    protected void fluidResultSlot(IRecipeLayoutBuilder builder, R recipe, int index, int x, int y)
    {
        FluidResult result = recipe.getFluidResult(index);
        ResultCount count = result.count();

        IRecipeSlotBuilder slot = builder.addOutputSlot(x, y);
        addFluidStack(slot, result.display());

        List<Component> tooltipLines = new ObjectArrayList<>();
        resultRequirementTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(count, tooltipLines);
        IDrawable countOverlay = resultCountOverlay(count, tooltipLines, s -> Component.translatable("jei.tooltip.liquid.amount", s));
        IIngredientRenderer<FluidStack> renderer = count.isConstant() ? FluidWithCountRenderer.INSTANCE : FluidWithoutCountRenderer.INSTANCE;

        slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, renderer);
        slot.addRichTooltipCallback((_, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    private @Nullable IDrawable resultChanceOverlay(ResultCount count, List<Component> lines)
    {
        if (!count.isRandom()) return null;

        String formattedChance = LimaTextUtil.format1PlacePercentage(count.chance());
        lines.add(LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(formattedChance).withStyle(ChatFormatting.YELLOW));
        return ScaledFontDrawable.withStyle(formattedChance, ChatFormatting.YELLOW, 0.5f);
    }

    private @Nullable IDrawable resultCountOverlay(ResultCount count, List<Component> lines, Function<String, Component> amountTooltip)
    {
        if (count.isConstant()) return null;

        String formattedAmount = String.format("%s-%s", LimaTextUtil.formatWholeNumber(count.min()), LimaTextUtil.formatWholeNumber(count.max()));
        lines.add(LTXILangKeys.OUTPUT_VARIABLE_COUNT_TOOLTIP.translate().append(amountTooltip.apply(formattedAmount)));
        return ScaledFontDrawable.plainText("VAR", 0.75f);
    }

    private void resultRequirementTooltip(RecipeResult<?, ?> result, List<Component> lines)
    {
        if (result.required())
        {
            lines.add(LTXILangKeys.OUTPUT_REQUIRED_TOOLTIP.translate().withStyle(ChatFormatting.GREEN));
        }
        if (!result.required())
        {
            lines.add(LTXILangKeys.OUTPUT_OPTIONAL_TOOLTIP.translate().withStyle(LTXIConstants.OUTPUT_ORANGE.chatStyle()));
        }
    }

    @Override
    public abstract IRecipeHolderType<R> getRecipeType();

    @Override
    public int getWidth()
    {
        return width;
    }

    @Override
    public int getHeight()
    {
        return height;
    }

    @Override
    public final void draw(RecipeHolder<R> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        graphics.fill(0, 0, width, height, 0xff2e2e2e);
        drawRecipe(recipe, recipeSlotsView, graphics, mouseX, mouseY);
    }

    @Override
    public final void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> recipe, IFocusGroup focuses)
    {
        setRecipe(builder, recipe, recipe.value(), focuses, localRegistryAccess());
    }

    @Override
    public Component getTitle()
    {
        return title;
    }

    @Override
    public @Nullable IDrawable getIcon()
    {
        return null;
    }

    private record ResultOverlay(@Nullable IDrawable chance, @Nullable IDrawable count) implements IDrawable
    {
        @Override
        public int getWidth()
        {
            return 16;
        }

        @Override
        public int getHeight()
        {
            return 16;
        }

        @Override
        public void draw(GuiGraphicsExtractor graphics, int xOffset, int yOffset)
        {
            if (chance != null) chance.draw(graphics, xOffset + 1, yOffset + 1);
            if (count != null) count.draw(graphics, xOffset + 16 - count.getWidth(), yOffset + 17 - count.getHeight());
        }
    }
}