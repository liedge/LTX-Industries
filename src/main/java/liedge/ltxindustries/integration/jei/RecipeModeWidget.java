package liedge.ltxindustries.integration.jei;

import liedge.limacore.client.gui.LimaGuiUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.client.gui.ItemLikeIconsRenderer;
import liedge.ltxindustries.lib.MachineLocation;
import liedge.ltxindustries.recipe.RecipeMode;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

record RecipeModeWidget(ScreenPosition position,
                        IDrawable background,
                        IDrawable overlay,
                        @Nullable Holder<RecipeMode> mode,
                        @Nullable MachineLocation location) implements IRecipeWidget
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

        if (mode == null || ItemLikeIconsRenderer.render(graphics, mode.value().icon(), 0, 0) == 0)
            overlay.draw(graphics, 0, 0);
    }

    @Override
    public void getTooltip(ITooltipBuilder tooltip, double mouseX, double mouseY)
    {
        if (LimaGuiUtil.isMouseWithinArea(mouseX, mouseY, 0, 0, 16, 16))
        {
            if (mode != null)
                tooltip.add(LTXILangKeys.JEI_RECIPE_MODE_NEEDED.translateArgs(mode.value().title()));
            else
                tooltip.add(LTXILangKeys.JEI_NO_RECIPE_MODE_NEEDED.translate().withStyle(ChatFormatting.GRAY));

            if (location != null)
            {
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