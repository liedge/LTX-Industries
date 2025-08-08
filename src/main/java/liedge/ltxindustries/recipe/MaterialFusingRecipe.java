package liedge.ltxindustries.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaRecipeType;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.List;

public class MaterialFusingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final MapCodec<MaterialFusingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LimaCoreCodecs.sizedIngredients(3).forGetter(LimaCustomRecipe::getItemIngredients),
            LimaCoreCodecs.sizedFluidIngredients(0, 1).forGetter(LimaCustomRecipe::getFluidIngredients),
            ItemResult.listMapCodec(1).forGetter(LimaCustomRecipe::getItemResults))
            .apply(instance, MaterialFusingRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, MaterialFusingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.sizedIngredients(3),
            LimaCustomRecipe::getItemIngredients,
            LimaStreamCodecs.sizedFluidIngredients(0, 1),
            LimaCustomRecipe::getFluidIngredients,
            ItemResult.listStreamCodec(1),
            LimaCustomRecipe::getItemResults,
            MaterialFusingRecipe::new);

    public MaterialFusingRecipe(List<SizedIngredient> ingredients, List<SizedFluidIngredient> fluidIngredients, List<ItemResult> itemResults)
    {
        super(ingredients, fluidIngredients, itemResults, List.of());
    }

    @Override
    public boolean matches(LimaRecipeInput input, Level level)
    {
        return consumeItemIngredients(input, true) && consumeFluidIngredients(input, IFluidHandler.FluidAction.SIMULATE);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.MATERIAL_FUSING.get();
    }

    @Override
    public LimaRecipeType<?> getType()
    {
        return LTXIRecipeTypes.MATERIAL_FUSING.get();
    }
}