package liedge.limatech.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.recipe.LimaCustomRecipe;
import liedge.limacore.recipe.LimaRecipeInput;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.registry.LimaTechRecipeSerializers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.Nullable;

public class WeaponFabricatingRecipe extends BaseFabricatingRecipe
{
    public static final MapCodec<WeaponFabricatingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LimaCoreCodecs.ingredientsMapCodec(1, 16).forGetter(LimaCustomRecipe::getIngredients),
            ExtraCodecs.POSITIVE_INT.fieldOf("energy_required").forGetter(BaseFabricatingRecipe::getEnergyRequired),
            WeaponItem.CODEC.fieldOf("weapon_item").forGetter(o -> o.weaponItem))
            .apply(instance, WeaponFabricatingRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, WeaponFabricatingRecipe> STREAM_CODEC = StreamCodec.composite(
            LimaStreamCodecs.ingredientsStreamCodec(1, 16), LimaCustomRecipe::getIngredients,
            LimaStreamCodecs.POSITIVE_VAR_INT, BaseFabricatingRecipe::getEnergyRequired,
            WeaponItem.STREAM_CODEC, o -> o.weaponItem,
            WeaponFabricatingRecipe::new);

    private final WeaponItem weaponItem;
    private final ItemStack previewItem;

    public WeaponFabricatingRecipe(NonNullList<Ingredient> ingredients, int energyRequired, WeaponItem weaponItem)
    {
        super(ingredients, energyRequired);
        this.weaponItem = weaponItem;
        this.previewItem = weaponItem.getDefaultInstance();
    }

    @Override
    public String getGroup()
    {
        return "weapons";
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return LimaTechRecipeSerializers.WEAPON_FABRICATING.get();
    }

    @Override
    public ItemStack assemble(@Nullable LimaRecipeInput input, HolderLookup.Provider registries)
    {
        return weaponItem.getDefaultInstance(registries);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.@Nullable Provider registries)
    {
        return previewItem;
    }
}