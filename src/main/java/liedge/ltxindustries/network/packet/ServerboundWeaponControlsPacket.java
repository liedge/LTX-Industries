package liedge.ltxindustries.network.packet;

import liedge.limacore.network.ServerboundPayload;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.item.weapon.WeaponItem;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundWeaponControlsPacket(WeaponItem weaponItem, byte action) implements ServerboundPayload
{
    public static final Type<ServerboundWeaponControlsPacket> TYPE = LTXIndustries.RESOURCES.packetType("server_weapon_controls");
    public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundWeaponControlsPacket> STREAM_CODEC = StreamCodec.composite(
            WeaponItem.STREAM_CODEC, ServerboundWeaponControlsPacket::weaponItem,
            ByteBufCodecs.BYTE, ServerboundWeaponControlsPacket::action,
            ServerboundWeaponControlsPacket::new);

    public static final byte TRIGGER_PRESS = 0;
    public static final byte TRIGGER_RELEASE = 1;
    public static final byte RELOAD_PRESS = 2;

    @Override
    public void handleServer(ServerPlayer sender, IPayloadContext context)
    {
        LTXIServerPacketHandler.handleWeaponControlsPacket(this, sender);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}