package liedge.ltxindustries.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.util.LimaLootUtil;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.menu.tooltip.FabricatorIngredientTooltip;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.List;

public final class FabricatingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.<FabricatingRecipe>mapCodec(instance -> instance.group(
            LimaSizedItemIngredient.listMapCodec(1, 16).forGetter(LimaCustomRecipe::getItemIngredients),
            ItemResult.CODEC.fieldOf("result").forGetter(LimaCustomRecipe::getFirstItemResult),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(FabricatingRecipe::getEnergyRequired),
            GROUP_MAP_CODEC.forGetter(FabricatingRecipe::getGroup))
            .apply(instance, FabricatingRecipe::new))
            .validate(LimaCustomRecipe::checkNotEmpty);

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaSizedItemIngredient.listStreamCodec(1, 16), LimaCustomRecipe::getItemIngredients,
            ItemResult.STREAM_CODEC, LimaCustomRecipe::getFirstItemResult,
            LimaStreamCodecs.POSITIVE_VAR_INT, FabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.STRING_UTF8, LimaCustomRecipe::getGroup,
            FabricatingRecipe::new);

    public static List<RecipeHolder<FabricatingRecipe>> getSortedRecipes(Level level)
    {
        return LimaRecipesUtil.getSortedRecipesForType(level,
                LTXIRecipeTypes.FABRICATING,
                Comparator.comparing(holder -> holder.value().getGroup()),
                Comparator.comparing(RecipeHolder::id));
    }

    private final int energyRequired;
    private final String group;

    public FabricatingRecipe(List<LimaSizedItemIngredient> ingredients, ItemResult result, int energyRequired, String group)
    {
        super(ingredients, List.of(result));
        this.energyRequired = energyRequired;
        this.group = group;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public TooltipComponent createIngredientTooltip()
    {
        return new FabricatorIngredientTooltip(getItemIngredients());
    }

    public ItemStack generateItemResult(ServerLevel level)
    {
        ItemStack stack = getFirstItemResult().generateResult(level.random);
        if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(level), stack, equipmentItem.getUpgrades(stack));
        }

        return stack;
    }

    public ItemStack getFabricatingResultItem()
    {
        return getFirstItemResult().getMaxStack();
    }

    @Override
    public String getGroup()
    {
        return group;
    }

    @Override
    public RecipeType<?> getType()
    {
        return LTXIRecipeTypes.FABRICATING.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LTXIRecipeSerializers.FABRICATING.get();
    }
}