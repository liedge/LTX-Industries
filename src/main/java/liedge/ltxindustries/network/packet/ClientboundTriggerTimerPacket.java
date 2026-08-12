package liedge.ltxindustries.network.packet;

import liedge.limacore.network.ClientboundPayload;
import liedge.limacore.network.LimaStreamCodecs;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.LTXIExtendedInput;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundTriggerTimerPacket(WeaponItem weaponItem, int duration) implements ClientboundPayload
{
    public static final Type<ClientboundTriggerTimerPacket> TYPE = LTXIndustries.RESOURCES.packetType("client_trigger_timer");
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundTriggerTimerPacket> STREAM_CODEC = StreamCodec.composite(
            WeaponItem.STREAM_CODEC, ClientboundTriggerTimerPacket::weaponItem,
            LimaStreamCodecs.NON_NEGATIVE_VAR_INT, ClientboundTriggerTimerPacket::duration,
            ClientboundTriggerTimerPacket::new);

    @Override
    public void handleClient(IPayloadContext context)
    {
        Player player = context.player();
        LTXIExtendedInput.of(player).setTriggerTimer(player, weaponItem, duration);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}