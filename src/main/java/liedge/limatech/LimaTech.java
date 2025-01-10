package liedge.limatech;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limatech.lib.upgradesystem.equipment.EquipmentUpgrade;
import liedge.limatech.lib.upgradesystem.machine.MachineUpgrade;
import liedge.limatech.lib.weapons.GlobalWeaponDamageModifiers;
import liedge.limatech.network.packet.LimaTechPacketsRegistration;
import liedge.limatech.registry.*;
import liedge.limatech.util.config.LimaTechClientConfig;
import liedge.limatech.util.config.LimaTechMachinesConfig;
import liedge.limatech.util.config.LimaTechWeaponsConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
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
        LimaTechAttributes.initRegister(modBus);
        LimaTechAttachmentTypes.initRegister(modBus);
        LimaTechBlockEntities.initRegister(modBus);
        LimaTechBlocks.initRegister(modBus);
        LimaTechCreativeTabs.initRegister(modBus);
        LimaTechDataComponents.initRegister(modBus);
        LimaTechEntities.initRegister(modBus);
        LimaTechEquipmentUpgrades.initRegister(modBus);
        LimaTechGameEvents.initRegister(modBus);
        LimaTechItems.initRegister(modBus);
        LimaTechMachineUpgrades.initRegister(modBus);
        LimaTechMenus.initRegister(modBus);
        LimaTechMobEffects.initRegister(modBus);
        LimaTechNetworkSerializers.initRegister(modBus);
        LimaTechParticles.initRegister(modBus);
        LimaTechRecipeSerializers.initRegister(modBus);
        LimaTechRecipeTypes.initRegister(modBus);
        LimaTechSounds.initRegister(modBus);

        // Mod configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, LimaTechClientConfig.CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechWeaponsConfig.WEAPONS_CONFIG_SPEC, "limatech-weapons.toml");
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechMachinesConfig.MACHINES_CONFIG_SPEC, "limatech-machines.toml");

        modBus.register(new CommonSetup());
    }

    private static class CommonSetup
    {
        @SubscribeEvent
        private void onConfigLoaded(final ModConfigEvent event)
        {
            LimaTechClientConfig.onConfigLoaded(event);
        }

        @SubscribeEvent
        private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
        {
            LimaTechPacketsRegistration.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
        }

        @SubscribeEvent
        private void registerCapabilities(final RegisterCapabilitiesEvent event)
        {
            LimaTechItems.registerCapabilities(event);
            LimaTechBlocks.registerCapabilities(event);
            LimaTechBlockEntities.registerCapabilities(event);

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
            event.register(LimaTechRegistries.EQUIPMENT_UPGRADE_EFFECT_TYPE);
            event.register(LimaTechRegistries.MACHINE_UPGRADE_EFFECT_TYPE);
        }

        @SubscribeEvent
        private void registerDataPackRegistries(final DataPackRegistryEvent.NewRegistry event)
        {
            event.dataPackRegistry(LimaTechRegistries.EQUIPMENT_UPGRADES_KEY, EquipmentUpgrade.DIRECT_CODEC, EquipmentUpgrade.DIRECT_CODEC);
            event.dataPackRegistry(LimaTechRegistries.MACHINE_UPGRADES_KEY, MachineUpgrade.DIRECT_CODEC, MachineUpgrade.DIRECT_CODEC);
        }

        @SubscribeEvent
        private void modifyEntityAttributes(final EntityAttributeModificationEvent event)
        {
            for (EntityType<? extends LivingEntity> type : event.getTypes())
            {
                if (!event.has(type, LimaTechAttributes.UNIVERSAL_STRENGTH))
                {
                    event.add(type, LimaTechAttributes.UNIVERSAL_STRENGTH);
                }
            }
        }
    }
}