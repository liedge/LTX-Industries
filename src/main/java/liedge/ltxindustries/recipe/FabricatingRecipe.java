package liedge.ltxindustries.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limacore.recipe.ingredient.LimaSizedItemIngredient;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.menu.tooltip.RecipeIngredientsTooltip;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;

import java.util.List;

public final class FabricatingRecipe extends LimaCustomRecipe<LimaRecipeInput>
{
    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.<FabricatingRecipe>mapCodec(instance -> instance.group(
            LimaSizedItemIngredient.listMapCodec(1, 16).forGetter(LimaCustomRecipe::getItemIngredients),
            ItemResult.CODEC.fieldOf("result").forGetter(LimaCustomRecipe::getFirstItemResult),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(FabricatingRecipe::getEnergyRequired),
            GROUP_MAP_CODEC.forGetter(FabricatingRecipe::group))
            .apply(instance, FabricatingRecipe::new))
            .validate(LimaCustomRecipe::checkNotEmpty);

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaSizedItemIngredient.listStreamCodec(1, 16), LimaCustomRecipe::getItemIngredients,
            ItemResult.STREAM_CODEC, LimaCustomRecipe::getFirstItemResult,
            LimaStreamCodecs.POSITIVE_VAR_INT, FabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.STRING_UTF8, LimaCustomRecipe::group,
            FabricatingRecipe::new);

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
        return RecipeIngredientsTooltip.create(this, 4, 4);
    }

    public ResourceStack<ItemResource> generateItemResult(ServerLevel level)
    {
        ResourceStack<ItemResource> original = getFirstItemResult().generateResult(level.random);

        ItemStack stack = original.resource().toStack();
        if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem)
        {
            equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(level), stack, equipmentItem.getUpgrades(stack));
        }

        ItemResource modified = ItemResource.of(stack);

        return new ResourceStack<>(modified, original.amount());
    }

    public ItemStack getResultPreview()
    {
        ItemResult result = getFirstItemResult();
        return result.getResource().toStack(result.getCount().max());
    }

    @Override
    public String group()
    {
        return group;
    }

    @Override
    public RecipeType<FabricatingRecipe> getType()
    {
        return LTXIRecipeTypes.FABRICATING.get();
    }

    @Override
    public RecipeSerializer<FabricatingRecipe> getSerializer()
    {
        return LTXIRecipeSerializers.FABRICATING.get();
    }

    @Override
    protected ItemLike getWorkstation()
    {
        return LTXIItems.FABRICATOR;
    }
}