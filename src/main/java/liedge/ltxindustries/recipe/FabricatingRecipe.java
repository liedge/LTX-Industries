package liedge.ltxindustries.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.RecipeInputAccess;
import liedge.limacore.recipe.input.RecipeItemInput;
import liedge.limacore.recipe.result.ItemResult;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.item.UpgradableEquipmentItem;
import liedge.ltxindustries.registry.game.LTXIRecipeSerializers;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.resource.ResourceStack;

import java.util.Comparator;
import java.util.List;

public final class FabricatingRecipe extends LimaCustomRecipe<RecipeInputAccess>
{
    public static final Codec<ItemResult> RESULT_CODEC = ItemResult.CODEC.validate(result ->
    {
        if (result.count().isConstant() && !result.count().isRandom())
        {
            return DataResult.success(result);
        }
        else
        {
            return DataResult.error(() -> "Fabricating recipe results must have a constant count and cannot be random.");
        }
    });

    public static final MapCodec<FabricatingRecipe> CODEC = RecordCodecBuilder.<FabricatingRecipe>mapCodec(instance -> instance.group(
            RecipeItemInput.listCodec(1, 16).forGetter(LimaCustomRecipe::getItemInputs),
            RESULT_CODEC.fieldOf("result").forGetter(LimaCustomRecipe::getFirstItemResult),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(FabricatingRecipe::getEnergyRequired),
            GROUP_MAP_CODEC.forGetter(FabricatingRecipe::group))
            .apply(instance, FabricatingRecipe::new))
            .validate(LimaCustomRecipe::checkNotEmpty);

    public static final StreamCodec<RegistryFriendlyByteBuf, FabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            RecipeItemInput.LIST_STREAM_CODEC, LimaCustomRecipe::getItemInputs,
            ItemResult.STREAM_CODEC, LimaCustomRecipe::getFirstItemResult,
            LimaStreamCodecs.POSITIVE_VAR_INT, FabricatingRecipe::getEnergyRequired,
            ByteBufCodecs.STRING_UTF8, LimaCustomRecipe::group,
            FabricatingRecipe::new);

    public static final Comparator<RecipeHolder<FabricatingRecipe>> GROUP_AND_NAME_COMPARATOR = Comparator
            .<RecipeHolder<FabricatingRecipe>, String>comparing(holder -> holder.value().group)
            .thenComparing(holder -> holder.id().identifier());

    private final int energyRequired;
    private final String group;

    public FabricatingRecipe(List<RecipeItemInput> ingredients, ItemResult result, int energyRequired, String group)
    {
        super(ingredients, List.of(result));
        this.energyRequired = energyRequired;
        this.group = group;
    }

    public int getEnergyRequired()
    {
        return energyRequired;
    }

    public ResourceStack<ItemResource> generateItemResult(Level level)
    {
        ResourceStack<ItemResource> original = getFirstItemResult().createResource(level.getRandom());
        ItemStack stack = original.resource().toStack();

        if (stack.getItem() instanceof UpgradableEquipmentItem equipmentItem && level instanceof ServerLevel serverLevel)
        {
            equipmentItem.onUpgradeRefresh(LimaLootUtil.emptyLootContext(serverLevel), stack, equipmentItem.getUpgrades(stack));
            ItemResource modifiedResource = ItemResource.of(stack);
            return new ResourceStack<>(modifiedResource, original.amount());
        }
        else
        {
            return original;
        }
    }

    public ItemStack getResultPreview()
    {
        return getFirstItemResult().display();
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
}