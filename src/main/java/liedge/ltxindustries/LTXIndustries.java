package liedge.ltxindustries;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.lib.upgrades.equipment.EquipmentUpgrade;
import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrade;
import liedge.ltxindustries.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.ltxindustries.network.packet.LTXIPacketsRegistration;
import liedge.ltxindustries.registry.LTXIRegistries;
import liedge.ltxindustries.registry.game.*;
import liedge.ltxindustries.util.config.LTXIClientConfig;
import liedge.ltxindustries.util.config.LTXIMachinesConfig;
import liedge.ltxindustries.util.config.LTXIServerConfig;
import liedge.ltxindustries.util.config.LTXIWeaponsConfig;
import net.minecraft.core.registries.Registries;
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
import net.neoforged.neoforge.registries.RegisterEvent;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.slf4j.Logger;

@Mod(LTXIndustries.MODID)
public class LTXIndustries
{
    public static final String MODID = "ltxi";
    public static final ModResources RESOURCES = new ModResources(MODID);
    public static final Logger LOGGER = LogUtils.getLogger();

    public LTXIndustries(IEventBus modBus, ModContainer modContainer)
    {
        // Deferred register initialization
        LTXIAttachmentTypes.register(modBus);
        LTXIBlockEntities.register(modBus);
        LTXIBlocks.register(modBus);
        LTXICreativeTabs.register(modBus);
        LTXIDataComponents.register(modBus);
        LTXIEntities.register(modBus);
        LTXIEquipmentUpgradeEffects.register(modBus);
        LTXIFluids.register(modBus);
        LTXIGameEvents.register(modBus);
        LTXIItems.register(modBus);
        LTXILootRegistries.register(modBus);
        LTXIMenus.register(modBus);
        LTXIMobEffects.register(modBus);
        LTXINetworkSerializers.register(modBus);
        LTXIParticles.register(modBus);
        LTXIRecipeSerializers.register(modBus);
        LTXIRecipeTypes.register(modBus);
        LTXISounds.register(modBus);
        LTXIUpgradeEffectComponents.register(modBus);

        // Mod configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, LTXIClientConfig.CLIENT_CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LTXIServerConfig.SERVER_CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LTXIWeaponsConfig.WEAPONS_CONFIG_SPEC, "ltxi-weapons.toml");
        modContainer.registerConfig(ModConfig.Type.SERVER, LTXIMachinesConfig.MACHINES_CONFIG_SPEC, "ltxi-machines.toml");

        modBus.register(new CommonSetup());
    }

    private static class CommonSetup
    {
        @SubscribeEvent
        private void onConfigLoaded(final ModConfigEvent event)
        {
            LTXIClientConfig.reCacheConfigValues(event);
        }

        @SubscribeEvent
        private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
        {
            LTXIPacketsRegistration.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
        }

        @SubscribeEvent
        private void registerCapabilities(final RegisterCapabilitiesEvent event)
        {
            // Entity capabilities
            event.registerEntity(LTXICapabilities.ENTITY_BUBBLE_SHIELD, EntityType.PLAYER, (player, $) -> player.getData(LTXIAttachmentTypes.BUBBLE_SHIELD));
        }

        @SubscribeEvent
        private void registerDataMapTypes(final RegisterDataMapTypesEvent event)
        {
            event.register(GlobalWeaponDamageModifiers.DATA_MAP_TYPE);
        }

        @SubscribeEvent
        private void registerGameObjects(final RegisterEvent event)
        {
            event.register(Registries.FLUID, LTXIFluids::registerFluids);
        }

        @SubscribeEvent
        private void registerCustomRegistries(final NewRegistryEvent event)
        {
            event.register(LTXIRegistries.UPGRADE_COMPONENT_TYPES);
            event.register(LTXIRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPES);
        }

        @SubscribeEvent
        private void registerDataPackRegistries(final DataPackRegistryEvent.NewRegistry event)
        {
            event.dataPackRegistry(LTXIRegistries.Keys.EQUIPMENT_UPGRADES, EquipmentUpgrade.DIRECT_CODEC, EquipmentUpgrade.DIRECT_CODEC);
            event.dataPackRegistry(LTXIRegistries.Keys.MACHINE_UPGRADES, MachineUpgrade.DIRECT_CODEC, MachineUpgrade.DIRECT_CODEC);
        }
    }
}