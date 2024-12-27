package liedge.limatech.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.LimaTech;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;

public class WeaponFabricatingRecipe extends BaseFabricatingRecipe
{
    private static final Codec<ItemStack> RESULT_CODEC = ItemStack.CODEC.comapFlatMap(stack -> {
        if (stack.getItem() instanceof WeaponItem)
        {
            return DataResult.success(stack);
        }
        else
        {
            return DataResult.error(() -> "Result item stack is a not a Weapon Item");
        }
    }, Function.identity());

    public static final MapCodec<WeaponFabricatingRecipe> CODEC = createCodec(RESULT_CODEC, WeaponFabricatingRecipe::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, WeaponFabricatingRecipe> STREAM_CODEC = createStreamCodec(WeaponFabricatingRecipe::new);

    public WeaponFabricatingRecipe(List<SizedIngredient> ingredients, ItemStack resultItem, int energyRequired, String group)
    {
        super(ingredients, resultItem, energyRequired, group);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.WEAPON_FABRICATING.get();
    }

    @Override
    public ItemStack assemble(@Nullable LimaRecipeInput input, HolderLookup.Provider registries)
    {
        WeaponItem weaponItem = LimaCoreUtil.castOrNull(WeaponItem.class, getResultItem().getItem());
        if (weaponItem != null)
        {
            return weaponItem.getDefaultInstance(registries);
        }
        else
        {
            LimaTech.LOGGER.warn("Recipe of serializer type '{}' has an invalid result item. This should not happen.", LimaRegistryUtil.getNonNullRegistryId(getSerializer(), BuiltInRegistries.RECIPE_SERIALIZER));
            return ItemStack.EMPTY;
        }
    }
}