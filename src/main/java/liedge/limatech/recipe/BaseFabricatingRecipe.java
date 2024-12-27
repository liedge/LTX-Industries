package liedge.limatech.recipe;

import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.LimaSimpleSizedIngredientRecipe;
import liedge.limatech.menu.tooltip.FabricatorIngredientTooltip;
import liedge.limatech.registry.LimaTechRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class BaseFabricatingRecipe extends LimaSimpleSizedIngredientRecipe<LimaRecipeInput>
{
    public static final String EMPTY_GROUP = "";

    protected static <R extends BaseFabricatingRecipe> MapCodec<R> createCodec(Codec<ItemStack> resultItemCodec, Function4<List<SizedIngredient>, ItemStack, Integer, String, R> factory)
    {
        return RecordCodecBuilder.mapCodec(instance -> instance.group(
                LimaCoreCodecs.sizedIngredientsMapCodec(1, 16).forGetter(BaseFabricatingRecipe::getRecipeIngredients),
                resultItemCodec.fieldOf("result").forGetter(BaseFabricatingRecipe::getResultItem),
                ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(BaseFabricatingRecipe::getEnergyRequired),
                Codec.STRING.optionalFieldOf("group", EMPTY_GROUP).forGetter(BaseFabricatingRecipe::getGroup))
                .apply(instance, factory));
    }

    protected static <R extends BaseFabricatingRecipe> StreamCodec<RegistryFriendlyByteBuf, R> createStreamCodec(Function4<List<SizedIngredient>, ItemStack, Integer, String, R> factory)
    {
        return StreamCodec.composite(
                LimaStreamCodecs.sizedIngredientsStreamCodec(1, 16), BaseFabricatingRecipe::getRecipeIngredients,
                ItemStack.STREAM_CODEC, BaseFabricatingRecipe::getResultItem,
                LimaStreamCodecs.POSITIVE_VAR_INT, BaseFabricatingRecipe::getEnergyRequired,
                ByteBufCodecs.STRING_UTF8, BaseFabricatingRecipe::getGroup,
                factory);
    }

    private final int energyRequired;
    private final String group;

    protected BaseFabricatingRecipe(List<SizedIngredient> ingredients, ItemStack result, int energyRequired, String group)
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
}