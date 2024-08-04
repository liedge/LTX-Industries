package liedge.limatech.registry;

import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.limatech.LimaTech;
import liedge.limatech.recipe.BasicMachineRecipe;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.recipe.FusingRecipe;
import liedge.limatech.recipe.GrindingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechCrafting
{
    private LimaTechCrafting() {}

    private static final DeferredRegister<RecipeType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.RECIPE_TYPE);
    private static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = LimaTech.RESOURCES.deferredRegister(Registries.RECIPE_SERIALIZER);

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
        SERIALIZERS.register(bus);
    }

    // Recipe types
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<GrindingRecipe>> GRINDING_TYPE = registerType("grinding");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FusingRecipe>> FUSING_TYPE = registerType("fusing");
    public static final DeferredHolder<RecipeType<?>, LimaRecipeType<FabricatingRecipe>> FABRICATING_TYPE = registerType("fabricating");

    // Recipe serializers
    public static final DeferredHolder<RecipeSerializer<?>, BasicMachineRecipe.BasicMachineRecipeSerializer<GrindingRecipe>> GRINDING_SERIALIZER = SERIALIZERS.register("grinding", () -> BasicMachineRecipe.createRecipeSerializer(1, GrindingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, BasicMachineRecipe.BasicMachineRecipeSerializer<FusingRecipe>> FUSING_SERIALIZER = SERIALIZERS.register("fusing", () -> BasicMachineRecipe.createRecipeSerializer(3, FusingRecipe::new));
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FabricatingRecipe>> FABRICATING_SERIALIZER = SERIALIZERS.register("fabricating", FabricatingRecipe.Serializer::new);

    private static <R extends LimaCustomRecipe<?>> DeferredHolder<RecipeType<?>, LimaRecipeType<R>> registerType(String name)
    {
        return TYPES.register(name, LimaRecipeType::new);
    }
}