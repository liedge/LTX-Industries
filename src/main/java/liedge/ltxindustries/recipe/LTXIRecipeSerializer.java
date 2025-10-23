package liedge.ltxindustries.recipe;

import com.google.common.base.Preconditions;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeSerializerBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.Optional;

public record LTXIRecipeSerializer<R extends LTXIRecipe>(MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> streamCodec, LTXIRecipeSupplier<R> factory, int defaultTime) implements RecipeSerializer<R>
{
    public static <R extends LTXIRecipe> Builder<R> builder(LTXIRecipeSupplier<R> factory)
    {
        return new Builder<>(factory);
    }

    public static final class Builder<R extends LTXIRecipe> extends LimaRecipeSerializerBuilder<R, LTXIRecipeSerializer<R>, Builder<R>>
    {
        private final LTXIRecipeSupplier<R> factory;
        private int defaultCraftTime = LTXIRecipe.DEFAULT_CRAFTING_TIME;

        private Builder(LTXIRecipeSupplier<R> factory)
        {
            this.factory = factory;
        }

        public Builder<R> defaultTime(int defaultCraftTime)
        {
            Preconditions.checkArgument(defaultCraftTime >= 1 && defaultCraftTime < Integer.MAX_VALUE);
            this.defaultCraftTime = defaultCraftTime;
            return this;
        }

        @Override
        public LTXIRecipeSerializer<R> build(ResourceLocation id)
        {
            MapCodec<R> mapCodec = RecordCodecBuilder.<R>mapCodec(instance -> commonFields(instance)
                    .and(ExtraCodecs.POSITIVE_INT.optionalFieldOf("craft_time", defaultCraftTime).forGetter(LTXIRecipe::getCraftTime))
                    .and(RecipeMode.CODEC.optionalFieldOf("mode").forGetter(o -> Optional.ofNullable(o.getMode())))
                    .apply(instance, factory))
                    .validate(LimaCustomRecipe::checkNotEmpty);

            StreamCodec<RegistryFriendlyByteBuf, R> streamCodec = StreamCodec.composite(
                    itemIngredientStreamCodec,
                    LimaCustomRecipe::getItemIngredients,
                    fluidIngredientStreamCodec,
                    LimaCustomRecipe::getFluidIngredients,
                    itemResultStreamCodec,
                    LimaCustomRecipe::getItemResults,
                    fluidResultStreamCodec,
                    LimaCustomRecipe::getFluidResults,
                    LimaStreamCodecs.POSITIVE_VAR_INT,
                    LTXIRecipe::getCraftTime,
                    ByteBufCodecs.optional(RecipeMode.STREAM_CODEC),
                    o -> Optional.ofNullable(o.getMode()),
                    factory);

            return new LTXIRecipeSerializer<>(mapCodec, streamCodec, factory, defaultCraftTime);
        }
    }
}