package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.recipe.RecipeMode;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public record UnlockRecipeMode(Holder<RecipeMode> mode) implements UpgradeTooltipsProvider
{
    public static final Codec<UnlockRecipeMode> CODEC = RecipeMode.CODEC.xmap(UnlockRecipeMode::new, UnlockRecipeMode::mode);

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(LTXILangKeys.UNLOCK_RECIPE_MODE_EFFECT.translateArgs(mode.value().title()));
    }
}