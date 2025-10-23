package liedge.ltxindustries.blockentity.base;

import liedge.limacore.capability.itemhandler.ItemHolderBlockEntity;
import liedge.limacore.network.sync.AutomaticDataWatcher;
import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import net.minecraft.core.Holder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface RecipeModeHolderBlockEntity extends ItemHolderBlockEntity, SubMenuProviderBlockEntity
{
    String TAG_KEY_RECIPE_MODE = "recipe_mode";

    @Nullable Holder<RecipeMode> getMode();

    void setMode(@Nullable Holder<RecipeMode> mode);

    RecipeType<?> getRecipeTypeForMode();

    default LimaDataWatcher<Optional<Holder<RecipeMode>>> keepRecipeModeSynced()
    {
        return AutomaticDataWatcher.keepNullableSynced(LTXINetworkSerializers.RECIPE_MODE, this::getMode, this::setMode);
    }
}