package liedge.ltxindustries.client;

import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
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

public final class LTXIClientRecipes
{
    private LTXIClientRecipes() {}

    public static final EnumProxy<RecipeBookCategories> FABRICATING_CATEGORY = new EnumProxy<>(RecipeBookCategories.class,
            (Supplier<List<ItemStack>>) () -> List.of(LTXIBlocks.FABRICATOR.toStack()));

    private static final Comparator<RecipeHolder<FabricatingRecipe>> FABRICATING_COMPARATOR = Comparator.<RecipeHolder<FabricatingRecipe>, String>comparing(holder -> holder.value().getGroup())
            .thenComparing(RecipeHolder::id);

    public static Comparator<RecipeHolder<FabricatingRecipe>> comparingFabricationRecipes()
    {
        return FABRICATING_COMPARATOR;
    }

    public static List<RecipeHolder<FabricatingRecipe>> getUnlockedFabricatingRecipes(LocalPlayer player)
    {
        Level level = player.level();
        ClientRecipeBook book = player.getRecipeBook();

        return level.getRecipeManager().getAllRecipesFor(LTXIRecipeTypes.FABRICATING.get()).stream()
                .filter(holder -> FabricatingRecipe.validateUnlocked(book, holder, player))
                .sorted(comparingFabricationRecipes())
                .toList();
    }
}