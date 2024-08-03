package liedge.limatech.compat.jei;

import liedge.limatech.LimaTech;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.LimaTechBlocks;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

import static liedge.limatech.registry.LimaTechCrafting.FABRICATING_TYPE;

@JeiPlugin
public class LimaTechJEIPlugin implements IModPlugin
{
    private static final ResourceLocation PLUGIN_ID = LimaTech.RESOURCES.location("jei_plugin");

    public static final RecipeType<FabricatingRecipe> FABRICATING_JEI = new RecipeType<>(FABRICATING_TYPE.getId(), FabricatingRecipe.class);

    @Override
    public ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new FabricatingJEICategory(helper, FABRICATING_TYPE.value()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        RecipeManager manager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        registration.addRecipes(FABRICATING_JEI, unwrapHolders(manager, FABRICATING_TYPE.value()));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(LimaTechBlocks.FABRICATOR.toStack(), FABRICATING_JEI);
    }

    private <C extends RecipeInput, R extends Recipe<C>> List<R> unwrapHolders(RecipeManager manager, net.minecraft.world.item.crafting.RecipeType<R> recipeType)
    {
        return manager.getAllRecipesFor(recipeType).stream().map(RecipeHolder::value).toList();
    }
}