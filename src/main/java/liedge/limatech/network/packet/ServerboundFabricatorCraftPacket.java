package liedge.limatech.network.packet;

import liedge.limacore.network.packet.LimaPlayPacket;
import liedge.limatech.LimaTech;
import liedge.limatech.menu.FabricatorMenu;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundFabricatorCraftPacket(ResourceLocation recipeId) implements LimaPlayPacket.ServerboundOnly
{
    static final PacketSpec<ServerboundFabricatorCraftPacket> PACKET_SPEC = LimaTech.RESOURCES.packetSpec("fabricator_craft", ResourceLocation.STREAM_CODEC.map(ServerboundFabricatorCraftPacket::new, ServerboundFabricatorCraftPacket::recipeId));

    @Override
    public void onReceivedByServer(IPayloadContext context, ServerPlayer sender)
    {
        if (sender.containerMenu instanceof FabricatorMenu menu)
        {
            LimaTech.LOGGER.debug("Received recipe id '{}' instruction on server for fabricator", recipeId);
            menu.tryStartCrafting(recipeId);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return PACKET_SPEC.type();
    }
}