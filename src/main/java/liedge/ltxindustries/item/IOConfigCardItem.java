package liedge.ltxindustries.item;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.client.LimaComponentUtil;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class IOConfigCardItem extends Item implements TooltipShiftHintItem
{
    private final BlockEntityInputType cardInputType;

    public IOConfigCardItem(Properties properties, BlockEntityInputType cardInputType)
    {
        super(properties);
        this.cardInputType = cardInputType;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
    {
        if (player.isCrouching())
        {
            ItemStack stack = player.getItemInHand(usedHand);
            if (stack.get(LTXIDataComponents.BLOCK_IO_CONFIGURATION) != null)
            {
                if (level.isClientSide()) player.displayClientMessage(LTXILangKeys.IO_CARD_CLEARED.translate().withStyle(ChatFormatting.YELLOW), true);

                stack.remove(LTXIDataComponents.BLOCK_IO_CONFIGURATION);
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        Level level = context.getLevel();
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        BlockIOConfiguration configuration = stack.get(LTXIDataComponents.BLOCK_IO_CONFIGURATION);
        ConfigurableIOBlockEntity blockEntity = LimaBlockUtil.getBlockEntity(level, context.getClickedPos(), ConfigurableIOBlockEntity.class);

        if (blockEntity != null && player != null)
        {
            if (!level.isClientSide())
            {
                if (blockEntity.getConfigurableInputTypes().contains(cardInputType))
                {
                    BlockIOConfiguration currentConfig = blockEntity.getIOConfigurationOrThrow(cardInputType);

                    // Pull into card item if blank
                    if (configuration == null)
                    {
                        stack.set(LTXIDataComponents.BLOCK_IO_CONFIGURATION, currentConfig);
                        player.displayClientMessage(LTXILangKeys.IO_CARD_COPIED.translate().withStyle(ChatFormatting.AQUA), true);
                    }
                    // Imprint onto machine
                    else if (configuration.isValidForRules(blockEntity.getIOConfigRules(cardInputType)))
                    {
                        if (!currentConfig.equals(configuration))
                        {
                            blockEntity.setIOConfiguration(cardInputType, configuration);
                            player.displayClientMessage(LTXILangKeys.IO_CARD_PASTED.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()), true);
                        }
                        else
                        {
                            player.displayClientMessage(LTXILangKeys.IO_CARD_SAME_CONFIG.translate().withStyle(ChatFormatting.YELLOW), true);
                        }
                    }
                    // Config not valid for machine
                    else
                    {
                        player.displayClientMessage(LTXILangKeys.IO_CARD_INVALID_SETUP.translate().withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle()), true);
                    }
                }
                else
                {
                    player.displayClientMessage(LTXILangKeys.IO_CARD_INVALID_TYPE.translateArgs(cardInputType.translate()).withStyle(LTXIConstants.HOSTILE_ORANGE.chatStyle()), true);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendTooltipHintComponents(Level level, ItemStack stack, TooltipLineConsumer consumer)
    {
        BlockIOConfiguration configuration = stack.get(LTXIDataComponents.BLOCK_IO_CONFIGURATION);
        if (configuration == null)
        {
            consumer.accept(LTXILangKeys.EMPTY_IO_CARD_HINT.translate().withStyle(ChatFormatting.GRAY));
        }
        else
        {
            consumer.accept(LTXILangKeys.ENCODED_IO_CARD_HINT.translate().withStyle(ChatFormatting.GRAY));
            configuration.forEach((side, access) -> consumer.accept(LimaComponentUtil.colonSpaced(
                    side.translate().withStyle(ChatFormatting.GRAY),
                    access.translate().withStyle(ioStyle(access)))));

            if (configuration.autoInput())
                consumer.accept(LTXILangKeys.AUTO_INPUT_ON_TOOLTIP.translate().withStyle(LTXIConstants.INPUT_BLUE.chatStyle().withBold(true)));

            if (configuration.autoOutput())
                consumer.accept(LTXILangKeys.AUTO_OUTPUT_ON_TOOLTIP.translate().withStyle(LTXIConstants.OUTPUT_ORANGE.chatStyle().withBold(true)));
        }
    }

    private Style ioStyle(IOAccess access)
    {
        return switch (access)
        {
            case DISABLED -> Style.EMPTY.withColor(ChatFormatting.GRAY);
            case INPUT_ONLY -> LTXIConstants.INPUT_BLUE.chatStyle();
            case OUTPUT_ONLY -> LTXIConstants.OUTPUT_ORANGE.chatStyle();
            case INPUT_AND_OUTPUT -> LTXIConstants.INPUT_OUTPUT_GREEN.chatStyle();
        };
    }
}