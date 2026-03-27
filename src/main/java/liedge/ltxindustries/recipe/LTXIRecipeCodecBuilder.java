package liedge.ltxindustries.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeSerializerBuilder;
import liedge.limacore.recipe.input.RecipeFluidInput;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

import java.util.Optional;

public final class LTXIRecipeCodecBuilder<R extends LTXIRecipe> extends LimaRecipeSerializerBuilder<R, LTXIRecipeCodecBuilder<R>>
{
    private final LTXIRecipeSupplier<R> factory;
    private int defaultCraftingTime = LTXIRecipe.DEFAULT_CRAFTING_TIME;

    public LTXIRecipeCodecBuilder(LTXIRecipeSupplier<R> factory)
    {
        this.factory = factory;
    }

    public LTXIRecipeCodecBuilder<R> defaultTime(int defaultCraftingTime)
    {
        Preconditions.checkArgument(defaultCraftingTime >= 1 && defaultCraftingTime < Integer.MAX_VALUE);
        this.defaultCraftingTime = defaultCraftingTime;
        return this;
    }

    @Override
    protected MapCodec<R> buildCodec()
    {
        return RecordCodecBuilder.mapCodec(instance -> commonFields(instance)
                .and(ExtraCodecs.POSITIVE_INT.optionalFieldOf("craft_time", defaultCraftingTime).forGetter(LTXIRecipe::getCraftTime))
                .and(RecipeMode.CODEC.optionalFieldOf("mode").forGetter(o -> Optional.ofNullable(o.getMode())))
                .apply(instance, factory));
    }

    @Override
    protected StreamCodec<RegistryFriendlyByteBuf, R> buildStreamCodec()
    {
        return StreamCodec.composite(
            RecipeItemInput.LIST_STREAM_CODEC,
            LimaCustomRecipe::getItemInputs,
            RecipeFluidInput.LIST_STREAM_CODEC,
            LimaCustomRecipe::getFluidInputs,
            ItemResult.LIST_STREAM_CODEC,
            LimaCustomRecipe::getItemResults,
            FluidResult.LIST_STREAM_CODEC,
            LimaCustomRecipe::getFluidResults,
            LimaStreamCodecs.POSITIVE_VAR_INT,
            LTXIRecipe::getCraftTime,
            ByteBufCodecs.optional(RecipeMode.STREAM_CODEC),
            o -> Optional.ofNullable(o.getMode()),
            factory);
    }
}