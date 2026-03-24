package liedge.ltxindustries.item;

import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.transfer.LimaEnergyUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXIClientRecipes;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

public class FabricationBlueprintItem extends Item implements TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    public FabricationBlueprintItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (player.isCrouching())
        {
            return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(LTXIItems.EMPTY_FABRICATION_BLUEPRINT.toStack());
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        ResourceKey<Recipe<?>> key = stack.get(LTXIDataComponents.BLUEPRINT_RECIPE);
        RecipeHolder<FabricatingRecipe> holder = LTXIClientRecipes.byKey(LTXIRecipeTypes.FABRICATING, key);

        if (holder != null)
        {
            FabricatingRecipe recipe = holder.value();

            ItemStack result = recipe.getResultPreview();
            MutableComponent name = result.getStyledHoverName().copy();
            if (result.getCount() > 1) name.append(" x" + result.getCount());

            consumer.accept(name);
            consumer.accept(LTXILangKeys.INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(recipe.getEnergyRequired())).withStyle(LTXIConstants.REM_BLUE.chatStyle()));
            consumer.accept(recipe.createIngredientTooltip());
        }
        else
        {
            consumer.accept(LTXILangKeys.INVALID_BLUEPRINT_HINT.translate().withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle()));
        }
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(Identifier tabId)
    {
        return false;
    }
}