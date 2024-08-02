package liedge.limatech.network.packet;

import liedge.limacore.network.LimaStreamCodecs;
import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static liedge.limatech.registry.LimaTechAttachmentTypes.BUBBLE_SHIELD;

public record ClientboundBubbleShieldPacket(Optional<Entity> remoteEntity, float shieldHealth) implements LimaPlayPacket.ClientboundOnly
{
    static final PacketSpec<ClientboundBubbleShieldPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec("bubble_shield", StreamCodec.composite(
            LimaStreamCodecs.REMOTE_ENTITY, ClientboundBubbleShieldPacket::remoteEntity,
            ByteBufCodecs.FLOAT, ClientboundBubbleShieldPacket::shieldHealth,
            ClientboundBubbleShieldPacket::new));

    public static void sendShieldToTrackers(LivingEntity shieldedEntity)
    {
        shieldedEntity.getExistingData(BUBBLE_SHIELD).ifPresent(shield -> PacketDistributor.sendToPlayersTrackingEntityAndSelf(shieldedEntity, new ClientboundBubbleShieldPacket(Optional.of(shieldedEntity), shield.getShieldHealth())));
    }

    public ClientboundBubbleShieldPacket(@Nullable Entity remoteEntity, float shieldHealth)
    {
        this(Optional.ofNullable(remoteEntity), shieldHealth);
    }

    @Override
    public void onReceivedByClient(IPayloadContext context, Player player)
    {
        remoteEntity.ifPresent(entity -> entity.getData(BUBBLE_SHIELD).setShieldHealth(shieldHealth));
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}