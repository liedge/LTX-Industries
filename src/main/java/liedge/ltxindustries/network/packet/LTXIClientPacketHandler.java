package liedge.ltxindustries.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LTXIClientPacketHandler
{
    private LTXIClientPacketHandler() {}

    static void handleTriggerPacket(ClientboundTriggerPacket packet, IPayloadContext context)
    {
        Player player = context.player();
        LTXIExtendedInput input = LTXIExtendedInput.of(player);

        if (input.getSelectedSlot() == packet.slot())
        {
            if (packet.holding())
            {
                input.startTriggerHold(player);
            }
            else
            {
                input.stopTriggerHold(player);
            }
        }
    }

    static void handleFocusTargetPacket(ClientboundFocusTargetPacket packet)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        LivingEntity livingEntity = LimaCoreClientUtil.getClientEntity(packet.entityId(), LivingEntity.class);

        if (player != null) ClientExtendedInput.of(player).setFocusedTarget(player, livingEntity);
    }
}