package liedge.limatech.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import static liedge.limatech.registry.LimaTechAttachmentTypes.BUBBLE_SHIELD;

public record ClientboundBubbleShieldPacket(@Nullable Entity remoteEntity, float shieldHealth) implements LimaPlayPacket.ClientboundOnly
{
    static final PacketSpec<ClientboundBubbleShieldPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec(PacketFlow.CLIENTBOUND, "bubble_shield", StreamCodec.composite(
            ByteBufCodecs.VAR_INT, msg -> LimaEntityUtil.getEntityId(msg.remoteEntity),
            ByteBufCodecs.FLOAT, ClientboundBubbleShieldPacket::shieldHealth,
            ClientboundBubbleShieldPacket::new));

    private ClientboundBubbleShieldPacket(int eid, float shieldHealth)
    {
        this(LimaCoreClientUtil.getClientEntity(eid), shieldHealth);
    }

    public static void sendShieldToTrackersAndSelf(LivingEntity shieldedEntity)
    {
        shieldedEntity.getExistingData(BUBBLE_SHIELD).ifPresent(shield -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(shieldedEntity, new ClientboundBubbleShieldPacket(shieldedEntity, shield.getShieldHealth())));
    }

    @Override
    public void onReceivedByClient(IPayloadContext context, Player localPlayer)
    {
        if (remoteEntity != null) remoteEntity.getData(BUBBLE_SHIELD).setShieldHealth(shieldHealth);
    }

    @Override
    public PacketSpec<?> getPacketSpec()
    {
        return PACKET_SPEC;
    }
}