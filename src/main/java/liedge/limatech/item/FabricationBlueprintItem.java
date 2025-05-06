package liedge.limatech.item;

import liedge.limacore.capability.energy.LimaEnergyUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaRecipesUtil;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.game.LimaTechDataComponents;
import liedge.limatech.registry.game.LimaTechRecipeTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FabricationBlueprintItem extends Item implements TooltipShiftHintItem
{
    public FabricationBlueprintItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        // Clear the blueprint if Shift + Right-Click.
        ItemStack stack = player.getItemInHand(usedHand);
        if (player.isCrouching() && stack.get(LimaTechDataComponents.BLUEPRINT_RECIPE) != null)
        {
            return InteractionResultHolder.sidedSuccess(new ItemStack(this), level.isClientSide());
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        ResourceLocation id = stack.get(LimaTechDataComponents.BLUEPRINT_RECIPE);
        if (id != null)
        {
            LimaRecipesUtil.getRecipeById(level, id, LimaTechRecipeTypes.FABRICATING).ifPresentOrElse(holder ->
            {
                ItemStack result = holder.value().getResultItem();
                MutableComponent resultName = result.getHoverName().copy().withStyle(result.getRarity().getStyleModifier());
                if (result.getCount() > 1) resultName.append(" x" + result.getCount());

                consumer.accept(resultName);
                consumer.accept(LimaTechLang.INLINE_ENERGY_REQUIRED_TOOLTIP.translateArgs(LimaEnergyUtil.toEnergyString(holder.value().getEnergyRequired())).withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
                consumer.accept(holder.value().createIngredientTooltip());
            }, () -> consumer.accept(LimaTechLang.INVALID_BLUEPRINT_HINT.translate().withStyle(LimaTechConstants.HOSTILE_ORANGE.chatStyle())));
        }
        else
        {
            consumer.accept(LimaTechLang.BLANK_BLUEPRINT_HINT.translate().withStyle(ChatFormatting.GRAY));
        }
    }
}