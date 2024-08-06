package liedge.limatech.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class FabricatingRecipe extends BaseFabricatingRecipe
{
    public static final String EMPTY_GROUP = "";

    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LimaCoreCodecs.ingredientsMapCodec(1, 16).forGetter(LimaCustomRecipe::getIngredients),
            ItemStack.CODEC.fieldOf("result").forGetter(o -> o.result),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(BaseFabricatingRecipe::getEnergyRequired),
            Codec.STRING.optionalFieldOf("group", EMPTY_GROUP).forGetter(LimaCustomRecipe::getGroup))
            .apply(instance, FabricatingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.ingredientsStreamCodec(1, 16), LimaCustomRecipe::getIngredients,
            ItemStack.STREAM_CODEC, r -> r.result,
            LimaStreamCodecs.POSITIVE_VAR_INT, BaseFabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.STRING_UTF8, FabricatingRecipe::getGroup,
            FabricatingRecipe::new);

    private final ItemStack result;
    private final String group;

    public FabricatingRecipe(NonNullList<Ingredient> ingredients, ItemStack result, int energyRequired, String group)
    {
        super(ingredients, energyRequired);
        this.result = result;
        this.group = group;
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.FABRICATING.get();
    }

    @Override
    public ItemStack assemble(@Nullable LimaRecipeInput input, HolderLookup.Provider registries)
    {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.@Nullable Provider registries)
    {
        return result;
    }
}