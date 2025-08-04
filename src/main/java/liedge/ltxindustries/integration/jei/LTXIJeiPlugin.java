package liedge.ltxindustries.integration.jei;

import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.screen.GrinderScreen;
import liedge.ltxindustries.client.gui.screen.MaterialFusingChamberScreen;
import liedge.ltxindustries.client.gui.screen.BaseCookingMenuScreen;
import liedge.ltxindustries.item.UpgradeModuleItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
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

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.*;

@JeiPlugin
public class LTXIJeiPlugin implements IModPlugin
{
    private static final ResourceLocation PLUGIN_ID = LTXIndustries.RESOURCES.location("jei_plugin");

    static final RecipeType<RecipeHolder<GrindingRecipe>> GRINDING_JEI = createType(GRINDING);
    static final RecipeType<RecipeHolder<MaterialFusingRecipe>> MATERIAL_FUSING_JEI = createType(MATERIAL_FUSING);
    static final RecipeType<RecipeHolder<FabricatingRecipe>> FABRICATING_JEI = createType(FABRICATING);

    @Override
    public ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration)
    {
        registerUpgradeModuleSubtype(registration, LTXIItems.EQUIPMENT_UPGRADE_MODULE.get());
        registerUpgradeModuleSubtype(registration, LTXIItems.MACHINE_UPGRADE_MODULE.get());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();

        registration.addRecipeCategories(new GrindingJEICategory(helper, GRINDING));
        registration.addRecipeCategories(new MaterialFusingJEICategory(helper, MATERIAL_FUSING));
        registration.addRecipeCategories(new FabricatingJEICategory(helper, FABRICATING));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager manager = level.getRecipeManager();

        registration.addRecipes(GRINDING_JEI, manager.getAllRecipesFor(GRINDING.get()));
        registration.addRecipes(MATERIAL_FUSING_JEI, manager.getAllRecipesFor(MATERIAL_FUSING.get()));
        List<RecipeHolder<FabricatingRecipe>> fabricatingRecipes = LimaRecipesUtil.getSortedRecipesForType(level, FABRICATING, Comparator.comparing(holder -> holder.value().getGroup()), Comparator.comparing(holder -> holder.id().getPath()));
        registration.addRecipes(FABRICATING_JEI, fabricatingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_FURNACE.toStack(), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_SMOKER.toStack(), RecipeTypes.SMOKING);
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_BLAST_FURNACE.toStack(), RecipeTypes.BLASTING);
        registration.addRecipeCatalyst(LTXIBlocks.GRINDER.toStack(), GRINDING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.MATERIAL_FUSING_CHAMBER.toStack(), MATERIAL_FUSING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.FABRICATOR.toStack(), FABRICATING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.AUTO_FABRICATOR.toStack(), FABRICATING_JEI);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(BaseCookingMenuScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(BaseCookingMenuScreen containerScreen, double guiMouseX, double guiMouseY)
            {
                BaseCookingMenuScreen.CookingType cookingType = containerScreen.getCookingType();
                IGuiClickableArea clickableArea = IGuiClickableArea.createBasic(75, 39, 24, 6, cookingRecipeType(cookingType));
                return List.of(clickableArea);
            }
        });

        registration.addRecipeClickArea(GrinderScreen.class, 69, 41, 24, 6, GRINDING_JEI);
        registration.addRecipeClickArea(MaterialFusingChamberScreen.class, 81, 41, 24, 6, MATERIAL_FUSING_JEI);
    }

    private void registerUpgradeModuleSubtype(ISubtypeRegistration registration, UpgradeModuleItem<?, ?> item)
    {
        registration.registerSubtypeInterpreter(item, (stack, $) -> {
            UpgradeBaseEntry<?> entry = stack.get(item.entryComponentType());
            return entry != null ? entry.toString() : IIngredientSubtypeInterpreter.NONE;
        });
    }

    private static RecipeType<?> cookingRecipeType(BaseCookingMenuScreen.CookingType cookingType)
    {
        return switch (cookingType)
        {
            case SMELTING -> RecipeTypes.SMELTING;
            case SMOKING -> RecipeTypes.SMOKING;
            case BLASTING -> RecipeTypes.BLASTING;
        };
    }

    @SuppressWarnings("unchecked")
    private static <R extends Recipe<?>> RecipeType<RecipeHolder<R>> createType(DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, ? extends net.minecraft.world.item.crafting.RecipeType<R>> deferredHolder)
    {
        Class<RecipeHolder<R>> holderClass = (Class<RecipeHolder<R>>) (Object) RecipeHolder.class;
        return new RecipeType<>(deferredHolder.getId(), holderClass);
    }
}