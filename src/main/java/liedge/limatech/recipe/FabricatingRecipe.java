package liedge.limatech.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.limatech.menu.tooltip.FabricatorIngredientTooltip;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricatingRecipe extends LimaSimpleSizedIngredientRecipe<LimaRecipeInput>
{
    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance, 1, 16)
            .and(ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(FabricatingRecipe::getEnergyRequired))
            .and(GROUP_MAP_CODEC.forGetter(FabricatingRecipe::getGroup))
            .apply(instance, FabricatingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.sizedIngredientsStreamCodec(1, 16), FabricatingRecipe::getRecipeIngredients,
            ItemStack.STREAM_CODEC, FabricatingRecipe::getResultItem,
            LimaStreamCodecs.POSITIVE_VAR_INT, FabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.STRING_UTF8, FabricatingRecipe::getGroup,
            FabricatingRecipe::new);

    private final int energyRequired;
    private final String group;

    public FabricatingRecipe(List<SizedIngredient> ingredients, ItemStack result, int energyRequired, String group)
    {
        super(ingredients, result);
        this.energyRequired = energyRequired;
        this.group = group;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public TooltipComponent createIngredientTooltip()
    {
        return new FabricatorIngredientTooltip(getRecipeIngredients());
    }

    @Override
    public ItemStack assemble(@Nullable LimaRecipeInput input, HolderLookup.Provider registries)
    {
        return getResultItem().copy();
    }

    @Override
    public boolean matches(LimaRecipeInput input, @Nullable Level level)
    {
        return consumeIngredientsLenientSlots(input, true);
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public final RecipeType<?> getType()
    {
        return LimaTechRecipeTypes.FABRICATING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.FABRICATING.get();
    }
}