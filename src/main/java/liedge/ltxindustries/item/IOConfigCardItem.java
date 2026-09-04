package liedge.ltxindustries.item;

import liedge.limacore.blockentity.IOAccess;
import liedge.limacore.blockentity.RelativeHorizontalSide;
import liedge.limacore.client.gui.TooltipLineConsumer;
import liedge.limacore.client.util.LimaComponentUtil;
import liedge.limacore.util.LimaBlockUtil;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import liedge.ltxindustries.blockentity.base.ConfigurableIOBlockEntity;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIDataComponents;
import liedge.ltxindustries.util.LTXIChatStyles;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (player.isCrouching())
        {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.get(LTXIDataComponents.BLOCK_IO_CONFIGURATION) != null)
            {
                if (level.isClientSide()) player.sendOverlayMessage(LTXILangKeys.IO_CARD_CLEARED.translate().withStyle(ChatFormatting.YELLOW));

                stack.remove(LTXIDataComponents.BLOCK_IO_CONFIGURATION);
                return InteractionResult.SUCCESS_SERVER.heldItemTransformedTo(stack);
            }
        }

        return super.use(level, player, hand);
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
                if (blockEntity.supportsInputType(cardInputType))
                {
                    BlockIOConfiguration currentConfig = blockEntity.getIOConfigurationOrThrow(cardInputType);

                    // Pull into card item if blank
                    if (configuration == null)
                    {
                        stack.set(LTXIDataComponents.BLOCK_IO_CONFIGURATION, currentConfig);
                        player.sendOverlayMessage(LTXILangKeys.IO_CARD_COPIED.translate().withStyle(ChatFormatting.AQUA));
                    }
                    // Imprint onto machine
                    else
                    {
                        boolean applied = blockEntity.setIOConfiguration(cardInputType, configuration);
                        if (applied)
                            player.sendOverlayMessage(LTXILangKeys.IO_CARD_PASTED.translate().withStyle(LTXIChatStyles.LIME_GREEN));
                        else if (currentConfig.equals(configuration))
                            player.sendOverlayMessage(LTXILangKeys.IO_CARD_SAME_CONFIG.translate().withStyle(ChatFormatting.YELLOW));
                        else
                            player.sendOverlayMessage(LTXILangKeys.IO_CARD_INVALID_SETUP.translate().withStyle(LTXIChatStyles.HOSTILE_ORANGE));
                    }
                }
                else
                {
                    player.sendOverlayMessage(LTXILangKeys.IO_CARD_INVALID_TYPE.translateArgs(cardInputType.translate()).withStyle(LTXIChatStyles.HOSTILE_ORANGE));
                }
            }

            return InteractionResult.SUCCESS_SERVER;
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
            for (var entry : configuration)
            {
                RelativeHorizontalSide side = entry.getKey();
                IOAccess access = entry.getValue();
                consumer.accept(LimaComponentUtil.colonSpaced(side.translate().withStyle(ChatFormatting.GRAY), translateAccess(access)));
            }

            if (configuration.autoInput())
                consumer.accept(LTXILangKeys.AUTO_INPUT_ON_TOOLTIP.translate().withStyle(s -> s.withColor(LTXIConstants.INPUT_BLUE).withBold(true)));

            if (configuration.autoOutput())
                consumer.accept(LTXILangKeys.AUTO_OUTPUT_ON_TOOLTIP.translate().withStyle(s -> s.withColor(LTXIConstants.OUTPUT_ORANGE).withBold(true)));
        }
    }

    private MutableComponent translateAccess(IOAccess access)
    {
        MutableComponent component = access.translate();
        return switch (access)
        {
            case DISABLED -> component.withStyle(ChatFormatting.GRAY);
            case INPUT_ONLY -> component.withStyle(LTXIChatStyles.INPUT_BLUE);
            case OUTPUT_ONLY -> component.withStyle(LTXIChatStyles.OUTPUT_ORANGE);
            case INPUT_AND_OUTPUT -> component.withStyle(LTXIChatStyles.INPUT_OUTPUT_GREEN);
        };
    }
}