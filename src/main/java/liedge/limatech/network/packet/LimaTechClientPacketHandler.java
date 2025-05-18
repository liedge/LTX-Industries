package liedge.limatech.network.packet;

import liedge.limacore.client.LimaCoreClientUtil;
import liedge.limatech.LimaTechCapabilities;
import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.ClientWeaponControls;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

final class LimaTechClientPacketHandler
{
    private LimaTechClientPacketHandler() {}

    private static void setLocalShieldHealth(LivingEntity entity, float shieldHealth)
    {
        BubbleShieldUser shield = entity.getCapability(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD);
        if (shield != null) shield.setShieldHealth(shieldHealth);
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