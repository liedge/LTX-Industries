package liedge.limatech.client;

import liedge.limatech.registry.game.LimaTechBlocks;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.List;
import java.util.function.Supplier;

public final class LimaTechClientRecipes
{
    private LimaTechClientRecipes() {}

    public static final EnumProxy<RecipeBookCategories> FABRICATING_CATEGORY = new EnumProxy<>(RecipeBookCategories.class,
            (Supplier<List<ItemStack>>) () -> List.of(LimaTechBlocks.FABRICATOR.toStack()));
}