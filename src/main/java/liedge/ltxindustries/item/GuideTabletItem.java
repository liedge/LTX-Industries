package liedge.ltxindustries.item;

import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.integration.guideme.GuideMEIntegration;
import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class GuideTabletItem extends Item
{
    public GuideTabletItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand)
    {
        if (GuideMEIntegration.isGuideMEInstalled())
        {
            if (level.isClientSide())
            {
                GuideMEIntegration.openGuide(player);
                return InteractionResult.CONSUME;
            }

            return InteractionResult.SUCCESS;
        }
        else if (level.isClientSide())
        {
            player.sendSystemMessage(LTXILangKeys.GUIDEME_NOT_INSTALLED.translate().withStyle(ChatFormatting.RED));
        }

        player.getCooldowns().addCooldown(player.getItemInHand(hand), 40);
        return InteractionResult.FAIL;
    }
}