package liedge.ltxindustries.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.ItemResult;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.ltxindustries.menu.tooltip.FabricatorIngredientTooltip;
import liedge.ltxindustries.registry.game.LTXIBlocks;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.RecipeBook;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FabricatingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LimaCoreCodecs.sizedIngredients(16).forGetter(LimaCustomRecipe::getItemIngredients),
            ItemResult.CODEC.fieldOf("result").forGetter(LimaCustomRecipe::getFirstResult),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(FabricatingRecipe::getEnergyRequired),
            Codec.BOOL.optionalFieldOf("advancement_locked", false).forGetter(FabricatingRecipe::isAdvancementLocked),
            GROUP_MAP_CODEC.forGetter(FabricatingRecipe::getGroup))
            .apply(instance, FabricatingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.sizedIngredients(16), LimaCustomRecipe::getItemIngredients,
            ItemResult.STREAM_CODEC, LimaCustomRecipe::getFirstResult,
            LimaStreamCodecs.POSITIVE_VAR_INT, FabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.BOOL, FabricatingRecipe::isAdvancementLocked,
            ByteBufCodecs.STRING_UTF8, LimaCustomRecipe::getGroup,
            FabricatingRecipe::new);

    public static boolean validateUnlocked(RecipeBook recipeBook, RecipeHolder<FabricatingRecipe> recipe, Player player)
    {
        if (recipe.value().advancementLocked)
        {
            return player.isCreative() || recipeBook.contains(recipe);
        }

        return true;
    }

    private final int energyRequired;
    private final boolean advancementLocked;
    private final String group;

    public FabricatingRecipe(List<SizedIngredient> ingredients, ItemResult result, int energyRequired, boolean advancementLocked, String group)
    {
        super(ingredients, List.of(result));
        this.energyRequired = energyRequired;
        this.advancementLocked = advancementLocked;
        this.group = group;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public boolean isAdvancementLocked()
    {
        return advancementLocked;
    }

    public TooltipComponent createIngredientTooltip()
    {
        return new FabricatorIngredientTooltip(getItemIngredients());
    }

    public ItemStack generateFabricatingResult(RandomSource random)
    {
        return getFirstResult().generateResult(random);
    }

    public ItemStack getFabricatingResultItem()
    {
        return getFirstResult().item();
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
    public boolean isSpecial()
    {
        return false;
    }

    @Override
    public ItemStack getToastSymbol()
    {
        return LTXIBlocks.FABRICATOR.toStack();
    }

    @Override
    public final RecipeType<?> getType()
    {
        return LTXIRecipeTypes.FABRICATING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.FABRICATING.get();
    }
}