package liedge.limatech;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limatech.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgrades.machine.MachineUpgrade;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.network.packet.LimaTechPacketsRegistration;
import liedge.limatech.registry.LimaTechRegistries;
import liedge.limatech.registry.game.*;
import liedge.limatech.util.config.LimaTechClientConfig;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import liedge.limatech.util.config.LimaTechServerConfig;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.slf4j.Logger;

@Mod(LimaTech.MODID)
public class LimaTech
{
    public static final String MODID = "limatech";
    public static final ModResources RESOURCES = new ModResources(MODID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public LimaTech(IEventBus modBus, ModContainer modContainer)
    {
        // Deferred register initialization
        LimaTechAttachmentTypes.register(modBus);
        LimaTechBlockEntities.register(modBus);
        LimaTechBlocks.register(modBus);
        LimaTechCreativeTabs.register(modBus);
        LimaTechDataComponents.register(modBus);
        LimaTechEntities.register(modBus);
        LimaTechEquipmentUpgradeEffects.register(modBus);
        LimaTechGameEvents.register(modBus);
        LimaTechItems.register(modBus);
        LimaTechLootRegistries.register(modBus);
        LimaTechMenus.register(modBus);
        LimaTechMobEffects.register(modBus);
        LimaTechNetworkSerializers.register(modBus);
        LimaTechParticles.register(modBus);
        LimaTechRecipeSerializers.register(modBus);
        LimaTechRecipeTypes.register(modBus);
        LimaTechSounds.register(modBus);
        LimaTechUpgradeEffectComponents.register(modBus);

        // Mod configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, LimaTechClientConfig.CLIENT_CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechServerConfig.SERVER_CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechWeaponsConfig.WEAPONS_CONFIG_SPEC, "limatech-weapons.toml");
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechMachinesConfig.MACHINES_CONFIG_SPEC, "limatech-machines.toml");

        modBus.register(new CommonSetup());
    }

    private static class CommonSetup
    {
        @SubscribeEvent
        private void onConfigLoaded(final ModConfigEvent event)
        {
            LimaTechClientConfig.reCacheConfigValues(event);
        }

        @SubscribeEvent
        private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
        {
            LimaTechPacketsRegistration.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
        }

        @SubscribeEvent
        private void registerCapabilities(final RegisterCapabilitiesEvent event)
        {
            // Entity capabilities
            event.registerEntity(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD, EntityType.PLAYER, (player, $) -> player.getData(LimaTechAttachmentTypes.BUBBLE_SHIELD));
        }

        @SubscribeEvent
        private void registerDataMapTypes(final RegisterDataMapTypesEvent event)
        {
            event.register(GlobalWeaponDamageModifiers.DATA_MAP_TYPE);
        }

        @SubscribeEvent
        private void registerCustomRegistries(final NewRegistryEvent event)
        {
            event.register(LimaTechRegistries.UPGRADE_COMPONENT_TYPES);
            event.register(LimaTechRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPES);
        }

        @SubscribeEvent
        private void registerDataPackRegistries(final DataPackRegistryEvent.NewRegistry event)
        {
            event.dataPackRegistry(LimaTechRegistries.Keys.EQUIPMENT_UPGRADES, EquipmentUpgrade.DIRECT_CODEC, EquipmentUpgrade.DIRECT_CODEC);
            event.dataPackRegistry(LimaTechRegistries.Keys.MACHINE_UPGRADES, MachineUpgrade.DIRECT_CODEC, MachineUpgrade.DIRECT_CODEC);
        }
    }
}