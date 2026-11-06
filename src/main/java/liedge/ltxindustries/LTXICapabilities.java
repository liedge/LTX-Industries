package liedge.ltxindustries;

import liedge.ltxindustries.blockentity.base.IORules;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.lib.shield.GenericBubbleShield;
import liedge.ltxindustries.registry.game.LTXIAttachmentTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public final class LTXICapabilities
{
    private LTXICapabilities() {}

    // Blocks
    public static final BlockCapability<ResourceHandler<ItemResource>, IORules> MESH_ITEMS = BlockCapability.create(LTXIndustries.RESOURCES.id("mesh_item"), ResourceHandler.asClass(), IORules.class);
    public static final BlockCapability<EnergyHandler, IORules> MESH_ENERGY = BlockCapability.create(LTXIndustries.RESOURCES.id("mesh_energy"), EnergyHandler.class, IORules.class);
    public static final BlockCapability<ResourceHandler<FluidResource>, IORules> MESH_FLUIDS = BlockCapability.create(LTXIndustries.RESOURCES.id("mesh_fluids"), ResourceHandler.asClass(), IORules.class);

    // Entities
    public static final EntityCapability<EntityBubbleShield, @Nullable Void> ENTITY_BUBBLE_SHIELD = EntityCapability.createVoid(LTXIndustries.RESOURCES.id("bubble_shield"), EntityBubbleShield.class);

    public static void registerShieldCapability(final RegisterCapabilitiesEvent event)
    {
        event.registerEntity(ENTITY_BUBBLE_SHIELD, EntityType.PLAYER, (player, _) -> player.getData(LTXIAttachmentTypes.PLAYER_SHIELD));
        for (EntityType<?> type : BuiltInRegistries.ENTITY_TYPE)
        {
            if (!event.isEntityRegistered(ENTITY_BUBBLE_SHIELD, type))
            {
                event.registerEntity(ENTITY_BUBBLE_SHIELD, type, (entity, _) -> entity instanceof LivingEntity ? GenericBubbleShield.INSTANCE : null);
            }
        }
    }
}