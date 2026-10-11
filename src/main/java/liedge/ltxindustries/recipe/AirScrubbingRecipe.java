package liedge.ltxindustries.recipe;

import com.mojang.datafixers.util.Function5;
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
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public final class AirScrubbingRecipe extends LTXIRecipe
{
    public static final MapCodec<AirScrubbingRecipe> CODEC = RecordCodecBuilder.<AirScrubbingRecipe>mapCodec(i -> i.group(
            ItemResult.listMapCodec(0, 2).forGetter(LimaCustomRecipe::getItemResults),
            FluidResult.listMapCodec(0, 4).forGetter(LimaCustomRecipe::getFluidResults),
            LTXIRecipeCodecBuilder.craftTimeField(600).forGetter(LTXIRecipe::getCraftTime),
            LTXIRecipeCodecBuilder.MODE_FIELD_CODEC.forGetter(o -> Optional.ofNullable(o.getMode())),
            MachineLocation.CODEC.optionalFieldOf("location", MachineLocation.ANY).forGetter(AirScrubbingRecipe::getLocation))
            .apply(i, (Factory) AirScrubbingRecipe::new))
            .validate(recipe -> LimaCustomRecipe.checkNotEmpty(recipe, false));

    public static final StreamCodec<RegistryFriendlyByteBuf, AirScrubbingRecipe> STREAM_CODEC = StreamCodec.composite(
            ItemResult.LIST_STREAM_CODEC, LimaCustomRecipe::getItemResults,
            FluidResult.LIST_STREAM_CODEC, LimaCustomRecipe::getFluidResults,
            ByteBufCodecs.VAR_INT, LTXIRecipe::getCraftTime,
            ByteBufCodecs.optional(RecipeMode.STREAM_CODEC), r -> Optional.ofNullable(r.getMode()),
            MachineLocation.STREAM_CODEC, AirScrubbingRecipe::getLocation,
            (Factory) AirScrubbingRecipe::new);

    private final MachineLocation location;

    public AirScrubbingRecipe(List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode, MachineLocation location)
    {
        super(List.of(), List.of(), itemResults, fluidResults, craftTime, mode);
        this.location = location;
    }

    public MachineLocation getLocation()
    {
        return location;
    }

    @Override
    public boolean matches(LTXIRecipeInput input, Level level)
    {
        return super.matches(input, level) && location.test(level, input.pos());
    }

    @Override
    public RecipeSerializer<AirScrubbingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.AIR_SCRUBBING.get();
    }

    @Override
    public RecipeType<AirScrubbingRecipe> getType()
    {
        return LTXIRecipeTypes.AIR_SCRUBBING.get();
    }

    private interface Factory extends Function5<List<ItemResult>, List<FluidResult>, Integer, Optional<Holder<RecipeMode>>, MachineLocation, AirScrubbingRecipe>
    {
        AirScrubbingRecipe create(List<ItemResult> itemResults, List<FluidResult> fluidResults, int craftTime, @Nullable Holder<RecipeMode> mode, MachineLocation location);

        @Override
        default AirScrubbingRecipe apply(List<ItemResult> itemResults, List<FluidResult> fluidResults, Integer craftTime, Optional<Holder<RecipeMode>> mode, MachineLocation location)
        {
            return create(itemResults, fluidResults, craftTime, mode.orElse(null), location);
        }
    }
}