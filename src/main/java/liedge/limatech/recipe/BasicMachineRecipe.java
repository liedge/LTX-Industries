package liedge.limatech.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.inventory.ItemHandlerHolder;
import liedge.limacore.inventory.LimaItemStackHandler;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.BiFunction;

public abstract class BasicMachineRecipe extends LimaCustomRecipe<BasicMachineRecipe.LimaBasicRecipeInput>
{
    public static <R extends BasicMachineRecipe> BasicMachineRecipeSerializer<R> createRecipeSerializer(int maxIngredients, BiFunction<NonNullList<Ingredient>, ItemStack, R> factory)
    {
        return new BasicMachineRecipeSerializer<>(maxIngredients, factory);
    }

    final ItemStack result;

    protected BasicMachineRecipe(NonNullList<Ingredient> ingredients, ItemStack result)
    {
        super(ingredients);
        this.result = result;
    }

    @Override
    public boolean matches(BasicMachineRecipe.LimaBasicRecipeInput recipeInput, Level level)
    {
        List<Ingredient> ingredients = getIngredients();

        for (int i = 0; i < recipeInput.size(); i++)
        {
            if (i < ingredients.size())
            {
                if (!ingredients.get(i).test(recipeInput.getItem(i))) return false;
            }
            else
            {
                if (!recipeInput.getItem(i).isEmpty()) return false;
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(BasicMachineRecipe.LimaBasicRecipeInput recipeInput, HolderLookup.Provider registries)
    {
        LimaItemStackHandler machineInventory = recipeInput.getItemHandler();
        List<Ingredient> ingredients = getIngredients();

        for (int i = 0; i < ingredients.size(); i++)
        {
            int slot = recipeInput.inventorySlotForIngredient(i);
            machineInventory.extractItem(slot, getIngredientStackSize(i), false);
        }

        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result;
    }

    public interface LimaBasicRecipeInput extends RecipeInput, ItemHandlerHolder
    {
        int inventorySlotForIngredient(int ingredientIndex);

        @Override
        default ItemStack getItem(int index)
        {
            return getItemHandler().getStackInSlot(inventorySlotForIngredient(index));
        }
    }

    public static class BasicMachineRecipeSerializer<R extends BasicMachineRecipe> implements RecipeSerializer<R>
    {
        private final BiFunction<NonNullList<Ingredient>, ItemStack, R> factory;
        private final MapCodec<R> mapCodec;
        private final StreamCodec<RegistryFriendlyByteBuf, R> streamCodec;

        private BasicMachineRecipeSerializer(int maxIngredients, BiFunction<NonNullList<Ingredient>, ItemStack, R> factory)
        {
            this.factory = factory;
            this.mapCodec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                            LimaCoreCodecs.ingredientsMapCodec(1, maxIngredients).forGetter(LimaCustomRecipe::getIngredients),
                            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result)).apply(instance, factory));
            this.streamCodec = StreamCodec.composite(
                    LimaStreamCodecs.ingredientsStreamCodec(1, maxIngredients), LimaCustomRecipe::getIngredients,
                    ItemStack.STREAM_CODEC, r -> r.result,
                    factory);
        }

        public R createRecipeInstance(NonNullList<Ingredient> ingredients, ItemStack result)
        {
            return factory.apply(ingredients, result);
        }

        @Override
        public MapCodec<R> codec()
        {
            return mapCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, R> streamCodec()
        {
            return streamCodec;
        }
    }
}