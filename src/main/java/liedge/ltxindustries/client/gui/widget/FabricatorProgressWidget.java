package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.tooltip.ItemGridTooltip;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.Optional;

public class FabricatorProgressWidget extends FillBarWidget.VerticalBar
{
    public static final ResourceLocation BACKGROUND_SPRITE = LTXIndustries.RESOURCES.location("widget/fabricator_progress_background");
    public static final ResourceLocation FILL_SPRITE = LTXIndustries.RESOURCES.location("widget/fabricator_progress_fill");
    public static final int BACKGROUND_WIDTH = 5;
    public static final int BACKGROUND_HEIGHT = 22;
    public static final int FILL_WIDTH = 3;
    public static final int FILL_HEIGHT = 20;

    private final BaseFabricatorBlockEntity blockEntity;

    public FabricatorProgressWidget(int x, int y, BaseFabricatorBlockEntity blockEntity)
    {
        super(x, y, BACKGROUND_WIDTH, BACKGROUND_HEIGHT, FILL_WIDTH, FILL_HEIGHT);
        this.blockEntity = blockEntity;
    }

    @Override
    protected float getFillPercentage()
    {
        int recipeEnergy = blockEntity.getRecipeCheck().getLastUsedRecipe(Minecraft.getInstance().level).map(holder -> holder.value().getEnergyRequired()).orElse(0);
        return LimaCoreMath.divideFloat(blockEntity.getEnergyCraftProgress(), recipeEnergy);
    }

    @Override
    protected ResourceLocation getBackgroundSprite()
    {
        return BACKGROUND_SPRITE;
    }

    @Override
    protected ResourceLocation getForegroundSprite(float fillPercentage)
    {
        return FILL_SPRITE;
    }

    @Override
    public boolean hasTooltip()
    {
        return blockEntity.isCrafting();
    }

    @Override
    public void createWidgetTooltip(TooltipLineConsumer consumer)
    {
        Optional<RecipeHolder<FabricatingRecipe>> optional = blockEntity.getRecipeCheck().getLastUsedRecipe(Minecraft.getInstance().level);
        if (optional.isPresent())
        {
            FabricatingRecipe recipe = optional.get().value();
            float fill = LimaCoreMath.divideFloat(blockEntity.getEnergyCraftProgress(), recipe.getEnergyRequired());
            int progress = (int) (fill * 100f);

            consumer.accept(LTXILangKeys.CRAFTING_PROGRESS_TOOLTIP.translateArgs(progress).withStyle(ChatFormatting.GRAY));
            consumer.accept(ItemGridTooltip.createSingle(recipe.getFabricatingResultItem(), true));
        }
    }
}