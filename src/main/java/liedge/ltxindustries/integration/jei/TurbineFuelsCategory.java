package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import liedge.limacore.transfer.LimaTransferUtil;
import liedge.limacore.util.LimaTextUtil;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.data.TurbineFuel;
import liedge.ltxindustries.registry.LTXIDataMaps;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.placement.HorizontalAlignment;
import mezz.jei.api.gui.placement.VerticalAlignment;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.runtime.IIngredientManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Collection;
import java.util.List;

final class TurbineFuelsCategory extends LTXIBaseCategory<TurbineFuelsCategory.Entry>
{
    static List<Entry> getAllRecipes(IIngredientManager manager)
    {
        Collection<FluidStack> fluids = manager.getAllIngredients(NeoForgeTypes.FLUID_STACK);
        List<Entry> recipes = new ObjectArrayList<>();

        for (FluidStack stack : fluids)
        {
            if (!isSourceFluid(stack)) continue;

            TurbineFuel fuel = stack.getData(LTXIDataMaps.TURBINE_FUELS);
            if (fuel != null) recipes.add(new Entry(stack, fuel.energy()));
        }

        return recipes;
    }

    TurbineFuelsCategory(IGuiHelper helper, IRecipeType<Entry> recipeType)
    {
        super(helper, recipeType, LTXILangKeys.JEI_TURBINE_FUELS_TITLE.translate(), null, maxWidth(), 20);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, Entry recipe, IFocusGroup focuses)
    {
        builder.addInputSlot(2, 2)
                .setBackground(fluidSlotSprite, -1, -1)
                .setCustomRenderer(NeoForgeTypes.FLUID_STACK, FluidWithoutCountRenderer.INSTANCE)
                .add(recipe.fluid.getFluid(), 1000, recipe.fluid.getComponentsPatch());
    }

    @Override
    public void createRecipeExtras(IRecipeExtrasBuilder builder, Entry recipe, IFocusGroup focuses)
    {
        builder.addText(energyDensityTooltip(recipe.energy), getWidth() - 20, getHeight())
                .setPosition(20, 0)
                .setColor(0xff505050)
                .setTextAlignment(HorizontalAlignment.CENTER)
                .setTextAlignment(VerticalAlignment.CENTER);
    }

    private static int maxWidth()
    {
        Component maxLen = energyDensityTooltip(Integer.MAX_VALUE);
        int width = Minecraft.getInstance().font.width(maxLen);
        return 20 + width;
    }

    private static Component energyDensityTooltip(int energy)
    {
        String text = LimaTextUtil.formatWholeNumber(energy) + " CE / " + LimaTransferUtil.MILLIBUCKET_UNIT;
        return Component.literal(text);
    }

    record Entry(FluidStack fluid, int energy) { }
}