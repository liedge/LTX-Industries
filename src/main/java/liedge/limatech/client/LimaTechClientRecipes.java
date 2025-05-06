package liedge.limatech.client;

import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechBlocks;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

public final class LimaTechClientRecipes
{
    private LimaTechClientRecipes() {}

    public static final EnumProxy<RecipeBookCategories> FABRICATING_CATEGORY = new EnumProxy<>(RecipeBookCategories.class,
            (Supplier<List<ItemStack>>) () -> List.of(LimaTechBlocks.FABRICATOR.toStack()));

    public static List<RecipeHolder<FabricatingRecipe>> getUnlockedFabricatingRecipes(LocalPlayer player)
    {
        Level level = player.level();
        ClientRecipeBook book = player.getRecipeBook();

        return level.getRecipeManager().getAllRecipesFor(LimaTechRecipeTypes.FABRICATING.get()).stream()
                .filter(r -> FabricatingRecipe.validateUnlocked(book, r, player))
                .sorted(Comparator.<RecipeHolder<FabricatingRecipe>, String>comparing(r -> r.value().getGroup()).thenComparing(r -> r.id().toString()))
                .toList();
    }
}