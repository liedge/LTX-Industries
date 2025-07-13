package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.item.LimaCreativeTabFillerItem;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.recipe.FabricatingRecipe;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechItems;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
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
            return InteractionResultHolder.sidedSuccess(new ItemStack(LimaTechItems.EMPTY_FABRICATION_BLUEPRINT.asItem()), level.isClientSide());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        ResourceLocation id = stack.get(LimaTechDataComponents.BLUEPRINT_RECIPE);
        if (id != null)
        {
            Optional<RecipeHolder<FabricatingRecipe>> holder = LimaRecipesUtil.getRecipeById(level, id, LimaTechRecipeTypes.FABRICATING);
            if (holder.isPresent())
            {
                FabricatingRecipe recipe = holder.get().value();
                ItemStack result = recipe.getResultItem();
                MutableComponent resultName = result.getHoverName().copy().withStyle(result.getRarity().getStyleModifier());
                if (result.getCount() > 1) resultName.append(" x" + result.getCount());

                consumer.accept(resultName);
                consumer.accept(LimaTechLang.INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(recipe.getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
                consumer.accept(recipe.createIngredientTooltip());
                return;
            }
        }

        consumer.accept(LimaTechLang.INVALID_BLUEPRINT_HINT.translate().withStyle(LimaTechConstants.HOSTILE_ORANGE.chatStyle()));
    }

    @Override
    public boolean addDefaultInstanceToCreativeTab(ResourceLocation tabId)
    {
        return false;
    }
}