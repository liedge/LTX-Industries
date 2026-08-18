package liedge.ltxindustries.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.result.FluidResult;
import liedge.limacore.recipe.result.ItemResult;
import liedge.ltxindustries.lib.MachineLocation;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Objects;

public final class AirScrubbingRecipe extends LimaCustomRecipe<AirScrubbingInput>
{
    public static final MapCodec<AirScrubbingRecipe> CODEC = RecordCodecBuilder.<AirScrubbingRecipe>mapCodec(i -> i.group(
            RecipeMode.CODEC.fieldOf("mode").forGetter(AirScrubbingRecipe::getMode),
            MachineLocation.CODEC.optionalFieldOf("location", MachineLocation.ANY).forGetter(AirScrubbingRecipe::getLocation),
            ItemResult.listMapCodec(0, 2).forGetter(LimaCustomRecipe::getItemResults),
            FluidResult.listMapCodec(0, 4).forGetter(LimaCustomRecipe::getFluidResults))
            .apply(i, AirScrubbingRecipe::new))
            .validate(recipe -> LimaCustomRecipe.checkNotEmpty(recipe, false));

    public static final StreamCodec<RegistryFriendlyByteBuf, AirScrubbingRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeMode.STREAM_CODEC, AirScrubbingRecipe::getMode,
            MachineLocation.STREAM_CODEC, AirScrubbingRecipe::getLocation,
            ItemResult.LIST_STREAM_CODEC, LimaCustomRecipe::getItemResults,
            FluidResult.LIST_STREAM_CODEC, LimaCustomRecipe::getFluidResults,
            AirScrubbingRecipe::new);

    private final Holder<RecipeMode> mode;
    private final MachineLocation location;

    public AirScrubbingRecipe(Holder<RecipeMode> mode, MachineLocation location, List<ItemResult> itemResults, List<FluidResult> fluidResults)
    {
        super(List.of(), List.of(), itemResults, fluidResults);
        this.mode = mode;
        this.location = location;
    }

    public Holder<RecipeMode> getMode()
    {
        return mode;
    }

    public MachineLocation getLocation()
    {
        return location;
    }

    @Override
    public boolean matches(AirScrubbingInput input, Level level)
    {
        if (!Objects.equals(this.mode, input.mode()))
            return false;
        else
            return location.test(level, input.pos());
    }

    @Override
    public RecipeSerializer<? extends Recipe<AirScrubbingInput>> getSerializer()
    {
        return LTXIRecipeSerializers.AIR_SCRUBBING.get();
    }

    @Override
    public RecipeType<? extends Recipe<AirScrubbingInput>> getType()
    {
        return LTXIRecipeTypes.AIR_SCRUBBING.get();
    }
}