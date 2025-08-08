package liedge.ltxindustries.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientWeaponControls;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LTXIClientPacketHandler
{
    private LTXIClientPacketHandler() {}

    public static void handleWeaponsControlPacket(ClientboundWeaponControlsPacket packet, IPayloadContext context)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        if (player != null)
        {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof WeaponItem weaponItem)
            {
                ClientWeaponControls controls = ClientWeaponControls.of(player);

                if (packet.weaponItem() == weaponItem)
                {
                    controls.handleServerAction(heldItem, player, weaponItem, packet.action());
                }
                else
                {
                    controls.stopHoldingTrigger(heldItem, player, weaponItem, false);
                }
            }
        }
    }

    public static void handleFocusTargetPacket(ClientboundFocusTargetPacket packet, IPayloadContext context)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        LivingEntity livingEntity = LimaCoreClientUtil.getClientEntity(packet.entityId(), LivingEntity.class);

        if (player != null)
        {
            ClientWeaponControls.of(player).setFocusedTarget(livingEntity);
        }
    }
}