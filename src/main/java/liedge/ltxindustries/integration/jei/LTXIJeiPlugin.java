package liedge.ltxindustries.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import liedge.limacore.client.LimaCoreClient;
import liedge.limacore.client.gui.LimaMenuScreen;
import liedge.limacore.menu.slot.LimaFluidSlot;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.screen.RecipeLayoutScreen;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import liedge.ltxindustries.recipe.*;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.*;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.*;

@JeiPlugin
public class LTXIJeiPlugin implements IModPlugin
{
    private static final Identifier PLUGIN_ID = LTXIndustries.RESOURCES.id("jei_plugin");
    private static final Map<Identifier, IRecipeHolderType<?>> TYPE_MAP = new Object2ObjectOpenHashMap<>();

    private static <R extends Recipe<?>> IRecipeHolderType<R> storeExisting(IRecipeHolderType<R> type)
    {
        TYPE_MAP.put(type.getUid(), type);
        return type;
    }

    private static <R extends LimaCustomRecipe<?>> IRecipeHolderType<R> storeType(DeferredHolder<RecipeType<?>, LimaRecipeType<R>> deferredHolder)
    {
        IRecipeHolderType<R> type = IRecipeHolderType.create(deferredHolder.getId());
        TYPE_MAP.put(type.getUid(), type);
        return type;
    }

    static final IRecipeHolderType<SmeltingRecipe> SMELTING_JEI = storeExisting(RecipeTypes.SMELTING);
    static final IRecipeHolderType<SmokingRecipe> SMOKING_JEI = storeExisting(RecipeTypes.SMOKING);
    static final IRecipeHolderType<BlastingRecipe> BLASTING_JEI = storeExisting(RecipeTypes.BLASTING);
    static final IRecipeHolderType<GrindingRecipe> GRINDING_JEI = storeType(GRINDING);
    static final IRecipeHolderType<MaterialFusingRecipe> MATERIAL_FUSING_JEI = storeType(MATERIAL_FUSING);
    static final IRecipeHolderType<ElectroCentrifugingRecipe> ELECTRO_CENTRIFUGING_JEI = storeType(ELECTRO_CENTRIFUGING);
    static final IRecipeHolderType<MixingRecipe> MIXING_JEI = storeType(MIXING);
    static final IRecipeHolderType<EnergizingRecipe> ENERGIZING_JEI = storeType(ENERGIZING);
    static final IRecipeHolderType<ChemicalReactingRecipe> CHEMICAL_REACTING_JEI = storeType(CHEMICAL_REACTING);
    static final IRecipeHolderType<AssemblingRecipe> ASSEMBLING_JEI = storeType(ASSEMBLING);
    static final IRecipeHolderType<GeoSynthesisRecipe> GEO_SYNTHESIS_JEI = storeType(GEO_SYNTHESIS);
    static final IRecipeHolderType<GardenSimulatingRecipe> GARDEN_SIMULATING_JEI = storeType(GARDEN_SIMULATING);
    static final IRecipeHolderType<FabricatingRecipe> FABRICATING_JEI = storeType(FABRICATING);

    @Override
    public Identifier getPluginUid()
    {
        return PLUGIN_ID;
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration)
    {
        registration.registerFromDataComponentTypes(LTXIItems.UPGRADE_MODULE.asItem(), LTXIDataComponents.UPGRADE_ENTRY.get());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration)
    {
        IGuiHelper helper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, GRINDING, GRINDING_JEI, RecipeLayouts.GRINDING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, MATERIAL_FUSING, MATERIAL_FUSING_JEI, RecipeLayouts.FUSING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, ELECTRO_CENTRIFUGING, ELECTRO_CENTRIFUGING_JEI, RecipeLayouts.ELECTRO_CENTRIFUGING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, MIXING, MIXING_JEI, RecipeLayouts.MIXING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, ENERGIZING, ENERGIZING_JEI, RecipeLayouts.ENERGIZING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, CHEMICAL_REACTING, CHEMICAL_REACTING_JEI, RecipeLayouts.CHEMICAL_REACTING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, ASSEMBLING, ASSEMBLING_JEI, RecipeLayouts.ASSEMBLING));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, GEO_SYNTHESIS, GEO_SYNTHESIS_JEI, RecipeLayouts.GEO_SYNTHESIS));
        registration.addRecipeCategories(RecipeLayoutJeiCategory.create(helper, GARDEN_SIMULATING, GARDEN_SIMULATING_JEI, RecipeLayouts.GARDEN_SIMULATING));
        registration.addRecipeCategories(new FabricatingJeiCategory(helper, FABRICATING));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration)
    {
        registration.addRecipes(GRINDING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(GRINDING)));
        registration.addRecipes(MATERIAL_FUSING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(MATERIAL_FUSING)));
        registration.addRecipes(ELECTRO_CENTRIFUGING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(ELECTRO_CENTRIFUGING)));
        registration.addRecipes(MIXING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(MIXING)));
        registration.addRecipes(ENERGIZING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(ENERGIZING)));
        registration.addRecipes(CHEMICAL_REACTING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(CHEMICAL_REACTING)));
        registration.addRecipes(ASSEMBLING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(ASSEMBLING)));
        registration.addRecipes(GEO_SYNTHESIS_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(GEO_SYNTHESIS)));
        registration.addRecipes(GARDEN_SIMULATING_JEI, List.copyOf(LimaCoreClient.getClientRecipes().byType(GARDEN_SIMULATING)));

        registration.addRecipes(FABRICATING_JEI, LimaCoreClient.getClientRecipes().byType(FABRICATING)
                .stream()
                .sorted(FabricatingRecipe.GROUP_AND_NAME_COMPARATOR)
                .toList());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration)
    {
        registration.addCraftingStation(SMELTING_JEI, LTXIBlocks.DIGITAL_FURNACE);
        registration.addCraftingStation(SMOKING_JEI, LTXIBlocks.DIGITAL_SMOKER);
        registration.addCraftingStation(BLASTING_JEI, LTXIBlocks.DIGITAL_BLAST_FURNACE);
        registration.addCraftingStation(GRINDING_JEI, LTXIBlocks.GRINDER);
        registration.addCraftingStation(MATERIAL_FUSING_JEI, LTXIBlocks.MATERIAL_FUSING_CHAMBER);
        registration.addCraftingStation(ELECTRO_CENTRIFUGING_JEI, LTXIBlocks.ELECTROCENTRIFUGE);
        registration.addCraftingStation(MIXING_JEI, LTXIBlocks.MIXER);
        registration.addCraftingStation(ENERGIZING_JEI, LTXIBlocks.VOLTAIC_INJECTOR);
        registration.addCraftingStation(CHEMICAL_REACTING_JEI, LTXIBlocks.CHEM_LAB);
        registration.addCraftingStation(ASSEMBLING_JEI, LTXIBlocks.ASSEMBLER);
        registration.addCraftingStation(GEO_SYNTHESIS_JEI, LTXIBlocks.GEO_SYNTHESIZER);
        registration.addCraftingStation(GARDEN_SIMULATING_JEI, LTXIBlocks.DIGITAL_GARDEN);
        registration.addCraftingStation(FABRICATING_JEI, LTXIBlocks.FABRICATOR, LTXIBlocks.AUTO_FABRICATOR);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration)
    {
        registration.addGuiContainerHandler(LimaMenuScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(IClickableIngredientFactory builder, LimaMenuScreen containerScreen, double mouseX, double mouseY)
            {
                LimaFluidSlot slot = containerScreen.getHoveredFluidSlot();
                if (slot == null) return Optional.empty();

                return builder.createBuilder(NeoForgeTypes.FLUID_STACK, slot.getFluid()).buildWithArea(containerScreen.getLeftPos() + slot.x(), containerScreen.getTopPos() + slot.y(), 16, 16);
            }
        });

        registration.addGuiContainerHandler(RecipeLayoutScreen.class, new IGuiContainerHandler<>()
        {
            @Override
            public Collection<IGuiClickableArea> getGuiClickableAreas(RecipeLayoutScreen containerScreen, double guiMouseX, double guiMouseY)
            {
                RecipeType<?> type = containerScreen.getMenu().menuContext().getRecipeCheck().getRecipeType();
                Identifier typeId = LimaRegistryUtil.getNonNullRegistryId(type, BuiltInRegistries.RECIPE_TYPE);

                RecipeLayout layout = containerScreen.getMenu().getLayout();
                IRecipeHolderType<?> jeiType = TYPE_MAP.get(typeId);

                return jeiType != null ? List.of(IGuiClickableArea.createBasic(layout.progressBarX(), layout.progressBarY(), 24, 6, jeiType)) : List.of();
            }
        });
    }
}