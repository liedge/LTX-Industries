package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function5;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public abstract class LTXIRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final int DEFAULT_CRAFTING_TIME = 200;
    public static final MapCodec<Integer> CRAFT_TIME_MAP_CODEC = Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("craft_time", DEFAULT_CRAFTING_TIME);

    public static <R extends LTXIRecipe> SerializerBuilder<R> serializerBuilder(LTXIRecipeFactory<R> factory)
    {
        return new SerializerBuilder<>(factory);
    }

    private final int craftTime;

    protected LTXIRecipe(List<SizedIngredient> itemIngredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults, List<FluidStack> fluidResults, int craftTime)
    {
        super(itemIngredients, fluidIngredients, itemResults, fluidResults);
        this.craftTime = craftTime;
    }

    public int getCraftTime()
    {
        return craftTime;
    }

    @FunctionalInterface
    public interface LTXIRecipeFactory<R extends LTXIRecipe> extends Function5<List<SizedIngredient>, List<SizedFluidIngredient>, List<ItemResult>, List<FluidStack>, Integer, R> {}

    public static final class SerializerBuilder<R extends LTXIRecipe> extends LimaRecipeSerializerBuilder<R, SerializerBuilder<R>>
    {
        private final LTXIRecipeFactory<R> factory;

        private SerializerBuilder(LTXIRecipeFactory<R> factory)
        {
            this.factory = factory;
        }

        @Override
        public LimaRecipeSerializer<R> build(ResourceLocation id)
        {
            MapCodec<R> mapCodec = RecordCodecBuilder.<R>mapCodec(instance -> commonFields(instance).and(CRAFT_TIME_MAP_CODEC.forGetter(LTXIRecipe::getCraftTime)).apply(instance, factory))
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
                    factory);

            return LimaRecipeSerializer.of(id, mapCodec, streamCodec);
        }
    }
}