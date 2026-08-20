package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.input.RecipeStackInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.recipe.result.RecipeResult;
import liedge.limacore.recipe.result.ResultCount;
import liedge.limacore.transfer.LimaTransferUtil;
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
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredientRenderer;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

abstract class LTXIBaseCategory<T> implements IRecipeCategory<T>
{
    static final int BG_COLOR = 0xff2e2e2e;

    protected final IGuiHelper helper;
    private final Component title;
    private final int width;
    private final int height;

    // Commonly used drawables
    protected final IDrawableStatic machineProgressBackground;
    protected final IDrawableAnimated machineProgress;

    protected LTXIBaseCategory(IGuiHelper helper, Component title, int width, int height)
    {
        this.helper = helper;
        this.title = title;
        this.width = width;
        this.height = height;

        this.machineProgressBackground = guiSpriteDrawable(MachineProgressWidget.BACKGROUND_SPRITE, MachineProgressWidget.BACKGROUND_WIDTH, MachineProgressWidget.BACKGROUND_HEIGHT).build();
        this.machineProgress = guiSpriteDrawable(MachineProgressWidget.FILL_SPRITE, MachineProgressWidget.FILL_WIDTH, MachineProgressWidget.FILL_HEIGHT).buildAnimated(100, IDrawableAnimated.StartDirection.LEFT, false);
    }

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
            slot.setOverlay(new ScaledFontDrawable(Component.literal("NC").withStyle(ChatFormatting.GREEN), 0.5f), 1, 1);
            slot.addRichTooltipCallback((_, lines) -> lines.add(LTXILangKeys.INPUT_NOT_CONSUMED_TOOLTIP.translate().withStyle(ChatFormatting.GREEN)));
        }
        else
        {
            Component chanceStr = Component.literal(LimaTextUtil.format1PlacePercentage(input.consumeChance())).withStyle(ChatFormatting.YELLOW);
            slot.setOverlay(new ScaledFontDrawable(chanceStr, 0.5f), 1, 1);
            slot.addRichTooltipCallback((_, lines) -> lines.add(LTXILangKeys.INPUT_CONSUME_CHANCE_TOOLTIP.translateArgs(chanceStr)));
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

    protected void itemInputSlot(IRecipeLayoutBuilder builder, RecipeItemInput input, int x, int y)
    {
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).addItemStacks(input.ingredient().display().resolve(makeContext(), input.displayResolver()).toList());

        if (input.isRandom()) randomInputOverlay(slot, input);
    }

    protected void itemInputSlotGrid(IRecipeLayoutBuilder builder, List<RecipeItemInput> inputs, int x, int y, int width)
    {
        for (int i = 0; i < inputs.size(); i++)
        {
            int sx = x + (i % width) * 18;
            int sy = y + (i / width) * 18;
            itemInputSlot(builder, inputs.get(i), sx, sy);
        }
    }

    protected void fluidInputSlot(IRecipeLayoutBuilder builder, RecipeFluidInput input, int x, int y)
    {
        IRecipeSlotBuilder slot = builder.addInputSlot(x, y).setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidWithCountRenderer.INSTANCE);
        input.ingredient().display().resolve(makeContext(), input.displayResolver()).forEach(stack -> addFluidStack(slot, stack));

        if (input.isRandom()) randomInputOverlay(slot, input);
    }

    protected void itemResultSlot(IRecipeLayoutBuilder builder, ItemResult result, int x, int y)
    {
        ResultCount count = result.count();

        int displayCount = count.isFixedCount() ? count.max() : 1;
        IRecipeSlotBuilder slot = builder.addOutputSlot(x, y).addItemStacks(List.of(result.display(displayCount)));

        List<Component> tooltipLines = new ObjectArrayList<>();
        resultRequirementTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(count, tooltipLines);
        IDrawable countOverlay = itemResultCountOverlay(count, tooltipLines);

        slot.addRichTooltipCallback((_, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    protected void fluidResultSlot(IRecipeLayoutBuilder builder, FluidResult result, int x, int y)
    {
        ResultCount count = result.count();

        IRecipeSlotBuilder slot = builder.addOutputSlot(x, y);
        addFluidStack(slot, result.display());

        List<Component> tooltipLines = new ObjectArrayList<>();
        resultRequirementTooltip(result, tooltipLines);
        IDrawable chanceOverlay = resultChanceOverlay(count, tooltipLines);
        IDrawable countOverlay = resultCountOverlay(count, tooltipLines, s -> Component.translatable("jei.tooltip.liquid.amount", s));
        IIngredientRenderer<FluidStack> renderer = count.isFixedCount() ? FluidWithCountRenderer.INSTANCE : FluidWithoutCountRenderer.INSTANCE;

        slot.setCustomRenderer(NeoForgeTypes.FLUID_STACK, renderer);
        slot.addRichTooltipCallback((_, lines) -> lines.addAll(tooltipLines));
        if (chanceOverlay != null || countOverlay != null) slot.setOverlay(new ResultOverlay(chanceOverlay, countOverlay), 0, 0);
    }

    private @Nullable IDrawable resultChanceOverlay(ResultCount count, List<Component> lines)
    {
        if (!count.isRandom()) return null;

        Component chanceStr = Component.literal(LimaTextUtil.format1PlacePercentage(count.chance())).withStyle(ChatFormatting.YELLOW);
        lines.add(LTXILangKeys.OUTPUT_CHANCE_TOOLTIP.translate().append(chanceStr));
        return new ScaledFontDrawable(chanceStr, 0.5f);
    }

    private @Nullable IDrawable itemResultCountOverlay(ResultCount count, List<Component> lines)
    {
        if (count.isFixedCount()) return null;

        String amountString = count.min() + "-" + count.max();
        lines.add(LTXILangKeys.OUTPUT_VARIABLE_COUNT_TOOLTIP.translate().append(amountString));
        return new ScaledFontDrawable(Component.literal(amountString), 0.5f);
    }

    private @Nullable IDrawable resultCountOverlay(ResultCount count, List<Component> lines, Function<String, Component> amountTooltip)
    {
        if (count.isFixedCount()) return null;

        String amountString = LimaTransferUtil.formatFullFluidAmount(count.min()) + "-" + LimaTransferUtil.formatFullFluidAmount(count.max());
        lines.add(LTXILangKeys.OUTPUT_VARIABLE_COUNT_TOOLTIP.translate().append(amountString));
        return new ScaledFontDrawable(Component.literal("VAR").withStyle(ChatFormatting.ITALIC), 0.75f);
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