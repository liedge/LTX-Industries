package liedge.limatech.recipe;

import com.mojang.serialization.MapCodec;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import java.util.List;

public class FabricatingRecipe extends BaseFabricatingRecipe
{
    public static final MapCodec<FabricatingRecipe> CODEC = createCodec(ItemStack.CODEC, FabricatingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = createStreamCodec(FabricatingRecipe::new);

    public FabricatingRecipe(List<SizedIngredient> ingredients, ItemStack result, int energyRequired, String group)
    {
        super(ingredients, result, energyRequired, group);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.FABRICATING.get();
    }
}