package liedge.ltxindustries.item;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.recipe.FabricatingRecipe;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.registry.game.LTXIItems;
import liedge.ltxindustries.registry.game.LTXIRecipeTypes;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FabricationBlueprintItem extends Item implements TooltipShiftHintItem, LimaCreativeTabFillerItem
{
    public FabricationBlueprintItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        // Clear the blueprint if Shift + Right-Click.
        if (player.isCrouching())
        {
            return InteractionResultHolder.sidedSuccess(new ItemStack(LTXIItems.EMPTY_FABRICATION_BLUEPRINT.asItem()), level.isClientSide());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        ResourceLocation id = stack.get(LTXIDataComponents.BLUEPRINT_RECIPE);
        if (id != null)
        {
            Optional<RecipeHolder<FabricatingRecipe>> holder = LimaRecipesUtil.getRecipeById(level, id, LTXIRecipeTypes.FABRICATING);
            if (holder.isPresent())
            {
                FabricatingRecipe recipe = holder.get().value();
                ItemStack result = recipe.getResultItem();
                MutableComponent resultName = result.getHoverName().copy().withStyle(result.getRarity().getStyleModifier());
                if (result.getCount() > 1) resultName.append(" x" + result.getCount());

                consumer.accept(resultName);
                consumer.accept(LTXILangKeys.INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(recipe.getEnergyRequired())).withStyle(LTXIConstants.REM_BLUE.chatStyle()));
                consumer.accept(recipe.createIngredientTooltip());
                return;
            }
        }

        consumer.accept(LTXILangKeys.INVALID_BLUEPRINT_HINT.translate().withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle()));
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }
}