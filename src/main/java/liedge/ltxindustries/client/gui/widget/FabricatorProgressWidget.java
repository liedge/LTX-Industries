package liedge.ltxindustries.client.gui.widget;

import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.client.gui.FillBarWidget;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.lib.math.LimaCoreMath;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.BaseFabricatorBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.menu.tooltip.ItemStacksTooltip;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;

public class FabricatorProgressWidget extends FillBarWidget.VerticalBar
{
    public static final Identifier BACKGROUND_SPRITE = LTXIndustries.RESOURCES.id("widget/fabricator_progress_background");
    public static final Identifier FILL_SPRITE = LTXIndustries.RESOURCES.id("widget/fabricator_progress_fill");
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
        RecipeHolder<FabricatingRecipe> lastUsedRecipe = LimaCoreClient.getClientRecipes().byKey(LTXIRecipeTypes.FABRICATING, blockEntity.getRecipeCheck().getLastUsedRecipeKey());
        int recipeEnergy = lastUsedRecipe != null ? lastUsedRecipe.value().getEnergyRequired() : 0;
        return LimaCoreMath.divideFloat(blockEntity.getEnergyCraftProgress(), recipeEnergy);
    }

    @Override
    protected Identifier getBackgroundSprite()
    {
        return BACKGROUND_SPRITE;
    }

    @Override
    protected Identifier getForegroundSprite(float fillPercentage)
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
        RecipeHolder<FabricatingRecipe> lastUsedRecipe = LimaCoreClient.getClientRecipes().byKey(LTXIRecipeTypes.FABRICATING, blockEntity.getRecipeCheck().getLastUsedRecipeKey());
        if (lastUsedRecipe != null)
        {
            FabricatingRecipe recipe = lastUsedRecipe.value();
            float fill = LimaCoreMath.divideFloat(blockEntity.getEnergyCraftProgress(), recipe.getEnergyRequired());
            int progress = (int) (fill * 100f);

            consumer.accept(LTXILangKeys.CRAFTING_PROGRESS_TOOLTIP.translateArgs(progress).withStyle(ChatFormatting.GRAY));
            consumer.accept(ItemStacksTooltip.createSingle(recipe.getResultPreview(), true));
        }
    }
}