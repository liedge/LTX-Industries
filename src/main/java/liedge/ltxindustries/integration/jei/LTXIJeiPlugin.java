package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXIClientRecipes;
import liedge.ltxindustries.client.gui.screen.RecipeLayoutScreen;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import liedge.ltxindustries.menu.layout.RecipeMenuLayout;
import liedge.ltxindustries.recipe.*;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.*;

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
    static final RecipeType<RecipeHolder<ElectroCentrifugingRecipe>> ELECTRO_CENTRIFUGING_JEI = registerType(ELECTRO_CENTRIFUGING);
    static final RecipeType<RecipeHolder<MixingRecipe>> MIXING_JEI = registerType(MIXING);
    static final RecipeType<RecipeHolder<EnergizingRecipe>> ENERGIZING_JEI = registerType(ENERGIZING);
    static final RecipeType<RecipeHolder<ChemicalReactingRecipe>> CHEMICAL_REACTING_JEI = registerType(CHEMICAL_REACTING);
    static final RecipeType<RecipeHolder<GardenSimulatingRecipe>> GARDEN_SIMULATING_JEI = registerType(GARDEN_SIMULATING);
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
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, GRINDING, GRINDING_JEI, RecipeLayouts.GRINDER));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, MATERIAL_FUSING, MATERIAL_FUSING_JEI, RecipeLayouts.MATERIAL_FUSING_CHAMBER));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, ELECTRO_CENTRIFUGING, ELECTRO_CENTRIFUGING_JEI, RecipeLayouts.ELECTROCENTRIFUGE));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, MIXING, MIXING_JEI, RecipeLayouts.MIXER));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, ENERGIZING, ENERGIZING_JEI, RecipeLayouts.COOKING_LAYOUT));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, CHEMICAL_REACTING, CHEMICAL_REACTING_JEI, RecipeLayouts.CHEM_LAB));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, GARDEN_SIMULATING, GARDEN_SIMULATING_JEI, RecipeLayouts.DIGITAL_GARDEN));
        registration.addRecipeCategories(new FabricatingJeiCategory(helper, FABRICATING));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        Level level = Objects.requireNonNull(Minecraft.getInstance().level);
        RecipeManager manager = level.getRecipeManager();

        registration.addRecipes(GRINDING_JEI, manager.getAllRecipesFor(GRINDING.get()));
        registration.addRecipes(MATERIAL_FUSING_JEI, manager.getAllRecipesFor(MATERIAL_FUSING.get()));
        registration.addRecipes(ELECTRO_CENTRIFUGING_JEI, manager.getAllRecipesFor(ELECTRO_CENTRIFUGING.get()));
        registration.addRecipes(MIXING_JEI, manager.getAllRecipesFor(MIXING.get()));
        registration.addRecipes(ENERGIZING_JEI, manager.getAllRecipesFor(ENERGIZING.get()));
        registration.addRecipes(CHEMICAL_REACTING_JEI, manager.getAllRecipesFor(CHEMICAL_REACTING.get()));
        registration.addRecipes(GARDEN_SIMULATING_JEI, manager.getAllRecipesFor(GARDEN_SIMULATING.get()));

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
        registration.addRecipeCatalyst(LTXIBlocks.ELECTROCENTRIFUGE, ELECTRO_CENTRIFUGING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.MIXER, MIXING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.VOLTAIC_INJECTOR, ENERGIZING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.CHEM_LAB, CHEMICAL_REACTING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.DIGITAL_GARDEN, GARDEN_SIMULATING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.FABRICATOR, FABRICATING_JEI);
        registration.addRecipeCatalyst(LTXIBlocks.AUTO_FABRICATOR, FABRICATING_JEI);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(LimaMenuScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Optional<IClickableIngredient<?>> getClickableIngredientUnderMouse(LimaMenuScreen containerScreen, double mouseX, double mouseY)
            {
                return Optional.ofNullable(containerScreen.getHoveredFluidSlot()).flatMap(slot ->
                        registration.getJeiHelpers().getIngredientManager().createClickableIngredient(NeoForgeTypes.FLUID_STACK, slot.getFluid(), new Rect2i(containerScreen.getGuiLeft() + slot.x(), containerScreen.getGuiTop() + slot.y(), 16, 16), false));
            }
        });

        registration.addGuiContainerHandler(RecipeLayoutScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(RecipeLayoutScreen containerScreen, double guiMouseX, double guiMouseY)
            {
                ResourceLocation typeId;
                net.minecraft.world.item.crafting.RecipeType<?> gameType = containerScreen.getMenu().menuContext().getRecipeCheck().getRecipeType();
                if (gameType instanceof LimaRecipeType<?> limaType) typeId = limaType.id();
                else typeId = LimaRegistryUtil.getNonNullRegistryId(gameType, BuiltInRegistries.RECIPE_TYPE);

                RecipeMenuLayout layout = containerScreen.getMenu().getLayout();
                RecipeType<?> jeiType = JEI_RECIPE_TYPES.get(typeId);
                return jeiType != null ? List.of(IGuiClickableArea.createBasic(layout.progressBarX(), layout.progressBarY(), 24, 6, jeiType)) : List.of();
            }
        });
    }
}