package liedge.limatech.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.menu.ItemGridTooltip;
import liedge.limatech.registry.LimaTechCrafting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.List;

public class FabricatingRecipe extends LimaCustomRecipe<FabricatorBlockEntity>
{
    private static final MapCodec<FabricatingRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
            LimaCoreCodecs.ingredientsMapCodec(1, 16).forGetter(LimaCustomRecipe::getIngredients),
            Codec.STRING.optionalFieldOf("group", "").forGetter(LimaCustomRecipe::getGroup),
            ItemStack.CODEC.fieldOf("result").forGetter(r -> r.result),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(r -> r.energyRequired))
            .apply(builder, FabricatingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.ingredientsStreamCodec(1, 16), LimaCustomRecipe::getIngredients,
            ByteBufCodecs.STRING_UTF8, LimaCustomRecipe::getGroup,
            ItemStack.STREAM_CODEC, r -> r.result,
            LimaStreamCodecs.POSITIVE_VAR_INT, r -> r.energyRequired,
            FabricatingRecipe::new);

    private final String group;
    private final ItemStack result;
    private final int energyRequired;

    public FabricatingRecipe(NonNullList<Ingredient> ingredients, String group, ItemStack result, int energyRequired)
    {
        super(ingredients);
        this.group = group;
        this.result = result;
        this.energyRequired = energyRequired;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public TooltipComponent createIngredientTooltip()
    {
        List<ItemStack> stacks = getIngredients().stream().map(i -> i.getItems()[0]).toList();
        return new ItemGridTooltip(stacks, 4);
    }

    @Override
    public boolean matches(FabricatorBlockEntity blockEntity, Level level)
    {
        return false;
    }

    @Override
    public ItemStack assemble(FabricatorBlockEntity blockEntity, HolderLookup.Provider provider)
    {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechCrafting.FABRICATING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType()
    {
        return LimaTechCrafting.FABRICATING_TYPE.get();
    }

    @Override
    public String getGroup()
    {
        return this.group;
    }

    public static class Serializer implements RecipeSerializer<FabricatingRecipe>
    {
        @Override
        public MapCodec<FabricatingRecipe> codec()
        {
            return MAP_CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> streamCodec()
        {
            return STREAM_CODEC;
        }
    }
}