package liedge.ltxindustries.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.weapons.ClientExtendedInput;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

final class LTXIClientPacketHandler
{
    private LTXIClientPacketHandler() {}

    static void handleWeaponsControlPacket(ClientboundWeaponControlsPacket packet)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        if (player != null)
        {
            ItemStack heldItem = player.getMainHandItem();
            if (heldItem.getItem() instanceof WeaponItem weaponItem)
            {
                ClientExtendedInput controls = ClientExtendedInput.of(player);

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

    static void handleFocusTargetPacket(ClientboundFocusTargetPacket packet)
    {
        Player player = LimaCoreClientUtil.getClientEntity(packet.playerId(), Player.class);
        LivingEntity livingEntity = LimaCoreClientUtil.getClientEntity(packet.entityId(), LivingEntity.class);

        if (player != null) ClientExtendedInput.of(player).setFocusedTarget(player, livingEntity);
    }
}