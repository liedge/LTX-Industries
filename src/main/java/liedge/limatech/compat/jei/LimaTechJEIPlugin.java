package liedge.limatech.compat.jei;

import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.screen.GrinderScreen;
import liedge.limatech.client.gui.screen.MaterialFusingChamberScreen;
import liedge.limatech.client.gui.screen.RecomposerScreen;
import liedge.limatech.recipe.*;
import liedge.limatech.registry.LimaTechBlocks;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.upgradesystem.EquipmentUpgradeEntry;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static liedge.limatech.registry.LimaTechRecipeTypes.*;

@JeiPlugin
public class LimaTechJEIPlugin implements IModPlugin
{
    private static final ResourceLocation PLUGIN_ID = LimaTech.RESOURCES.location("jei_plugin");

    static final RecipeType<RecipeHolder<GrindingRecipe>> GRINDING_JEI = createType(GRINDING);
    static final RecipeType<RecipeHolder<RecomposingRecipe>> RECOMPOSING_JEI = createType(RECOMPOSING);
    static final RecipeType<RecipeHolder<MaterialFusingRecipe>> MATERIAL_FUSING_JEI = createType(MATERIAL_FUSING);
    static final RecipeType<RecipeHolder<BaseFabricatingRecipe>> FABRICATING_JEI = createType(FABRICATING);

    @Override
    public ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration)
    {
        registration.registerSubtypeInterpreter(LimaTechItems.EQUIPMENT_UPGRADE_ITEM.get(), (stack, $) -> {
            EquipmentUpgradeEntry entry = EquipmentUpgradeEntry.getFromItem(stack);
            return entry != null ? entry.toString() : IIngredientSubtypeInterpreter.NONE;
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new GrindingJEICategory(helper, GRINDING));
        registration.addRecipeCategories(new RecomposingJEICategory(helper, RECOMPOSING));
        registration.addRecipeCategories(new MaterialFusingJEICategory(helper, MATERIAL_FUSING));
        registration.addRecipeCategories(new FabricatingJEICategory(helper, FABRICATING));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager manager = level.getRecipeManager();

        registration.addRecipes(GRINDING_JEI, manager.getAllRecipesFor(GRINDING.get()));
        registration.addRecipes(RECOMPOSING_JEI, manager.getAllRecipesFor(RECOMPOSING.get()));
        registration.addRecipes(MATERIAL_FUSING_JEI, manager.getAllRecipesFor(MATERIAL_FUSING.get()));
        List<RecipeHolder<BaseFabricatingRecipe>> fabricatingRecipes = LimaRecipesUtil.getSortedRecipesForType(level, FABRICATING, Comparator.comparing(holder -> holder.value().getGroup()), Comparator.comparing(holder -> holder.id().getPath()));
        registration.addRecipes(FABRICATING_JEI, fabricatingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(LimaTechBlocks.DIGITAL_FURNACE.toStack(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(LimaTechBlocks.GRINDER.toStack(), GRINDING_JEI);
        registration.addRecipeCatalyst(LimaTechBlocks.RECOMPOSER.toStack(), RECOMPOSING_JEI);
        registration.addRecipeCatalyst(LimaTechBlocks.MATERIAL_FUSING_CHAMBER.toStack(), MATERIAL_FUSING_JEI);
        registration.addRecipeCatalyst(LimaTechBlocks.FABRICATOR.toStack(), FABRICATING_JEI);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addRecipeClickArea(GrinderScreen.class, 75, 39, 24, 6, GRINDING_JEI);
        registration.addRecipeClickArea(RecomposerScreen.class, 75, 39, 24, 6, RECOMPOSING_JEI);
        registration.addRecipeClickArea(MaterialFusingChamberScreen.class, 81, 41, 24, 6, MATERIAL_FUSING_JEI);
    }

    @SuppressWarnings("unchecked")
    private static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createType(DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, ? extends net.minecraft.world.item.crafting.RecipeType<R>> deferredHolder)
    {
        Class<RecipeHolder<R>> holderClass = (Class<RecipeHolder<R>>) (Object) RecipeHolder.class;
        return new RecipeType<>(deferredHolder.getId(), holderClass);
    }
}