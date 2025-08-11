package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIClientRecipes;
import liedge.ltxindustries.client.gui.screen.BaseCookingMenuScreen;
import liedge.ltxindustries.client.gui.screen.GrinderScreen;
import liedge.ltxindustries.client.gui.screen.MaterialFusingChamberScreen;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.recipe.GrindingRecipe;
import liedge.ltxindustries.recipe.MaterialFusingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.*;

@JeiPlugin
public class LTXIJeiPlugin implements IModPlugin
{
    private static final ResourceLocation PLUGIN_ID = LTXIndustries.RESOURCES.location("jei_plugin");
    private static final Map<ResourceLocation, RecipeType<?>> JEI_RECIPE_TYPES = new Object2ObjectOpenHashMap<>();

    private static <T> RecipeType<T> registerExisting(RecipeType<T> recipeType)
    {
        JEI_RECIPE_TYPES.put(recipeType.getUid(), recipeType);
        return recipeType;
    }

    private static <R extends LimaCustomRecipe<?>> RecipeType<RecipeHolder<R>> registerType(DeferredHolder<net.minecraft.world.item.crafting.RecipeType<?>, LimaRecipeType<R>> holder)
    {
        RecipeType<RecipeHolder<R>> type = RecipeType.createRecipeHolderType(holder.getId());
        JEI_RECIPE_TYPES.put(type.getUid(), type);
        return type;
    }

    static final RecipeType<RecipeHolder<SmeltingRecipe>> SMELTING_JEI = registerExisting(RecipeTypes.SMELTING);
    static final RecipeType<RecipeHolder<SmokingRecipe>> SMOKING_JEI = registerExisting(RecipeTypes.SMOKING);
    static final RecipeType<RecipeHolder<BlastingRecipe>> BLASTING_JEI = registerExisting(RecipeTypes.BLASTING);
    static final RecipeType<RecipeHolder<GrindingRecipe>> GRINDING_JEI = registerType(GRINDING);
    static final RecipeType<RecipeHolder<MaterialFusingRecipe>> MATERIAL_FUSING_JEI = registerType(MATERIAL_FUSING);
    static final RecipeType<RecipeHolder<FabricatingRecipe>> FABRICATING_JEI = registerType(FABRICATING);

    @Override
    public ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration)
    {
        registration.registerSubtypeInterpreter(LTXIItems.EQUIPMENT_UPGRADE_MODULE.get(), new ModuleSubtypeInterpreter<>(LTXIDataComponents.EQUIPMENT_UPGRADE_ENTRY));
        registration.registerSubtypeInterpreter(LTXIItems.MACHINE_UPGRADE_MODULE.get(), new ModuleSubtypeInterpreter<>(LTXIDataComponents.MACHINE_UPGRADE_ENTRY));
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
        List<RecipeHolder<FabricatingRecipe>> fabricatingRecipes = LimaRecipesUtil.getSortedRecipesForType(level, FABRICATING, LTXIClientRecipes.comparingFabricationRecipes());
        registration.addRecipes(FABRICATING_JEI, fabricatingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_FURNACE, SMELTING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_SMOKER, SMOKING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_BLAST_FURNACE, BLASTING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.GRINDER, GRINDING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.MATERIAL_FUSING_CHAMBER, MATERIAL_FUSING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.FABRICATOR, FABRICATING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.AUTO_FABRICATOR, FABRICATING_JEI);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(BaseCookingMenuScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(BaseCookingMenuScreen containerScreen, double guiMouseX, double guiMouseY)
            {
                ResourceLocation typeId;
                net.minecraft.world.item.crafting.RecipeType<?> gameType = containerScreen.getMenu().menuContext().getRecipeCheck().getRecipeType();
                if (gameType instanceof LimaRecipeType<?> limaType) typeId = limaType.id();
                else typeId = LimaRegistryUtil.getNonNullRegistryId(gameType, BuiltInRegistries.RECIPE_TYPE);

                IGuiClickableArea clickableArea = IGuiClickableArea.createBasic(75, 39, 24, 6, Objects.requireNonNull(JEI_RECIPE_TYPES.get(typeId)));
                return List.of(clickableArea);
            }
        });

        registration.addRecipeClickArea(GrinderScreen.class, 69, 41, 24, 6, GRINDING_JEI);
        registration.addRecipeClickArea(MaterialFusingChamberScreen.class, 104, 40, 24, 6, MATERIAL_FUSING_JEI);
    }
}