package liedge.limatech.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import static liedge.limatech.registry.LimaTechAttachmentTypes.BUBBLE_SHIELD;

final class LimaTechClientPacketHandler
{
    private LimaTechClientPacketHandler() {}

    private static void setLocalShieldHealth(LivingEntity entity, float shieldHealth)
    {
        entity.getData(BUBBLE_SHIELD).setShieldHealth(shieldHealth);
    }

    public static void handlePlayerShieldPacket(ClientboundPlayerShieldPacket packet, IPayloadContext context)
    {
        setLocalShieldHealth(context.player(), packet.shieldHealth());
    }

    public static void handleEntityShieldPacket(ClientboundEntityShieldPacket packet, IPayloadContext context)
    {
        LivingEntity entity = LimaCoreClientUtil.getClientEntity(packet.entityId(), LivingEntity.class);
        if (entity != null) setLocalShieldHealth(entity, packet.shieldHealth());
    }

    public static void handleWeaponsControlPacket(ClientboundWeaponControlsPacket packet, IPayloadContext context)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        if (player != null)
        {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.is(packet.weaponItem()))
            {
                ClientWeaponControls.of(player).handleServerAction(heldItem, player, packet.weaponItem(), packet.action());
            }
        }
    }
}