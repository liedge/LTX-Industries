package liedge.ltxindustries;

import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.lib.shield.GenericBubbleShield;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public final class LTXICapabilities
{
    private LTXICapabilities() {}

    public static final EntityCapability<EntityBubbleShield, Void> ENTITY_BUBBLE_SHIELD = EntityCapability.createVoid(LTXIndustries.RESOURCES.location("bubble_shield"), EntityBubbleShield.class);

    public static void registerShieldCapability(final RegisterCapabilitiesEvent event)
    {
        event.registerEntity(ENTITY_BUBBLE_SHIELD, EntityType.PLAYER, (player, $) -> player.getData(LTXIAttachmentTypes.PLAYER_SHIELD));
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE)
        {
            if (!event.isEntityRegistered(ENTITY_BUBBLE_SHIELD, type))
            {
                event.registerEntity(ENTITY_BUBBLE_SHIELD, type, (entity, $) -> entity instanceof LivingEntity ? GenericBubbleShield.INSTANCE : null);
            }
        }
    }
}