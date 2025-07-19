package liedge.ltxindustries.network.packet;

import io.netty.buffer.ByteBuf;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

import static liedge.ltxindustries.registry.game.LTXIAttachmentTypes.BUBBLE_SHIELD;

public record ClientboundPlayerShieldPacket(float shieldHealth) implements CustomPacketPayload
{
    static final Type<ClientboundPlayerShieldPacket> TYPE = LTXIndustries.RESOURCES.packetType("player_shield");
    static final StreamCodec<ByteBuf, ClientboundPlayerShieldPacket> STREAM_CODEC = ByteBufCodecs.FLOAT.map(ClientboundPlayerShieldPacket::new, ClientboundPlayerShieldPacket::shieldHealth);

    public static void sendPacketToPlayer(Player player)
    {
        if (player instanceof ServerPlayer serverPlayer)
        {
            float shieldHealth = serverPlayer.getData(BUBBLE_SHIELD).getShieldHealth();
            PacketDistributor.sendToPlayer(serverPlayer, new ClientboundPlayerShieldPacket(shieldHealth));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}