package liedge.ltxindustries.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.client.gui.screen.RecipeLayoutScreen;
import liedge.ltxindustries.lib.MachineLocation;
import liedge.ltxindustries.menu.layout.LayoutSlot;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import liedge.ltxindustries.recipe.AirScrubbingRecipe;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.ILevelExtension;

import java.util.Optional;

final class AirScrubbingCategory extends LTXIRecipeHolderCategory<AirScrubbingRecipe>
{
    static AirScrubbingCategory create(IGuiHelper helper)
    {
        RecipeLayout layout = RecipeLayouts.AIR_SCRUBBING;
        return new AirScrubbingCategory(helper, LTXIRecipeTypes.AIR_SCRUBBING.get(), layout, RecipeLayoutJeiCategory.layoutBounds(layout, 0));
    }

    private final RecipeLayout layout;
    private final ScreenRectangle bounds;
    private final ScreenPosition modePos;
    private final IDrawableStatic modeBackground;

    private AirScrubbingCategory(IGuiHelper helper, LimaRecipeType<AirScrubbingRecipe> gameRecipeType, RecipeLayout layout, ScreenRectangle bounds)
    {
        super(helper, gameRecipeType, bounds.width(), bounds.height());
        this.layout = layout;
        this.bounds = bounds;
        this.modePos = layout.streamSlots()
                .filter(o -> o.type() == LayoutSlot.Type.RECIPE_MODE)
                .map(o -> new ScreenPosition(o.x() - bounds.left(), o.y() - bounds.top()))
                .findFirst().orElseThrow();
        this.modeBackground = guiSpriteDrawable(LayoutSlot.Type.RECIPE_MODE.getSprite(), 18, 18).build();
    }

    @Override
    public void draw(RecipeHolder<AirScrubbingRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY)
    {
        super.draw(holder, recipeSlotsView, graphics, mouseX, mouseY);
        RecipeLayoutScreen.renderLayout(graphics, -bounds.left(), -bounds.top(), layout);
    }

    @Override
    protected void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AirScrubbingRecipe> holder, AirScrubbingRecipe recipe, IFocusGroup focuses, RegistryAccess registries)
    {
        RecipeLayoutJeiCategory.addLayoutInputs(builder, layout, bounds, LayoutSlot.Type.ITEM_OUTPUT, recipe, LimaCustomRecipe::getItemResults, this::itemResultSlot);
        RecipeLayoutJeiCategory.addLayoutInputs(builder, layout, bounds, LayoutSlot.Type.FLUID_OUTPUT, recipe, LimaCustomRecipe::getFluidResults, this::fluidResultSlot);
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, RecipeHolder<AirScrubbingRecipe> recipe, IFocusGroup focuses)
    {
        builder.addWidget(new ModeWidget(modePos, modeBackground, recipe.value().getMode(), recipe.value().getLocation()));
    }

    @Override
    public IRecipeHolderType<AirScrubbingRecipe> getRecipeType()
    {
        return LTXIJeiPlugin.AIR_SCRUBBING_JEI;
    }

    private record ModeWidget(ScreenPosition position, IDrawable background, Holder<RecipeMode> mode, MachineLocation location) implements IRecipeWidget
    {
        @Override
        public ScreenPosition getPosition()
        {
            return position;
        }

        @Override
        public void drawWidget(GuiGraphicsExtractor graphics, double mouseX, double mouseY)
        {
            background.draw(graphics, -1, -1);
            ItemLikeIconsRenderer.render(graphics, mode.value().icon(), 0, 0);
        }

        @Override
        public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY)
        {
            if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 0, 0, 16, 16))
            {
                tooltip.add(LTXILangKeys.JEI_RECIPE_MODE_NEEDED.translateArgs(mode.value().title()));

                Optional<ResourceKey<Level>> dimension = location.dimension();
                if (dimension.isPresent())
                {
                    Identifier id = dimension.get().identifier();
                    Component text = Component.translatableWithFallback(id.toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), id.toString());
                    tooltip.add(LTXILangKeys.JEI_LOCATION_DIMENSION_TOOLTIP.translateArgs(text));
                }

                Optional<HolderSet<Biome>> biomes = location.biomes();
                if (biomes.isPresent() && biomes.get() instanceof HolderSet.Named<Biome> namedSet)
                {
                    String langKey = Tags.getTagTranslationKey(namedSet.key());
                    Component text = Component.translatableWithFallback(langKey, '#' + namedSet.key().location().toString());
                    tooltip.add(LTXILangKeys.JEI_LOCATION_BIOMES_TOOLTIP.translateArgs(text));
                }

                if (location.needsWaterlog())
                {
                    tooltip.add(LTXILangKeys.JEI_LOCATION_WATERLOG_TOOLTIP.translate());
                }
            }
        }
    }
}