package liedge.ltxindustries.blockentity.base;

import liedge.limacore.network.sync.LimaDataWatcher;
import liedge.limacore.network.sync.NullableValueTracker;
import liedge.limacore.transfer.item.ItemHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.Upgrades;
import liedge.ltxindustries.lib.upgrades.effect.UnlockRecipeMode;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.LTXIDataMaps;
import liedge.ltxindustries.registry.game.LTXINetworkSerializers;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface RecipeModeHolderBlockEntity extends ItemHolderBlockEntity, SubMenuProviderBlockEntity
{
    String TAG_KEY_RECIPE_MODE = "recipe_mode";

    static void applyUpgrades(RecipeModeHolderBlockEntity blockEntity, Upgrades upgrades)
    {
        HolderSet<RecipeMode> defaultModes = blockEntity.getDefaultRecipeModes();
        List<Holder<RecipeMode>> upgradeModes = Stream.concat(defaultModes.stream(), upgrades.effectStream(LTXIUpgradeEffectComponents.UNLOCK_RECIPE_MODE).map(UnlockRecipeMode::mode))
                .limit(23)
                .toList();

        HolderSet<RecipeMode> availableModes = upgradeModes.isEmpty() ? HolderSet.empty() : HolderSet.direct(upgradeModes);
        blockEntity.setAvailableRecipeModes(availableModes);

        Holder<RecipeMode> currentMode = blockEntity.getMode();
        if (currentMode != null && !availableModes.contains(currentMode))
        {
            blockEntity.setMode(null);
        }
    }

    @Nullable Holder<RecipeMode> getMode();

    @ApiStatus.Internal
    void setMode(@Nullable Holder<RecipeMode> mode);

    HolderSet<RecipeMode> getAvailableRecipeModes();

    @ApiStatus.Internal
    void setAvailableRecipeModes(HolderSet<RecipeMode> availableModes);

    default HolderSet<RecipeMode> getDefaultRecipeModes()
    {
        if (this instanceof RecipeMachineBlockEntity<?, ?> recipeMachine)
        {
            HolderSet<RecipeMode> modes = BuiltInRegistries.RECIPE_TYPE.wrapAsHolder(recipeMachine.getRecipeCheck().getRecipeType()).getData(LTXIDataMaps.DEFAULT_RECIPE_MODES);
            return modes != null ? modes : HolderSet.empty();
        }

        return HolderSet.empty();
    }

    default LimaDataWatcher<Optional<Holder<RecipeMode>>> keepRecipeModeSynced()
    {
        return NullableValueTracker.create(LTXINetworkSerializers.RECIPE_MODE, this::getMode, this::setMode).setAutomatic();
    }
}