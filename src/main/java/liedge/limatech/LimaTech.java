package liedge.limatech;

import com.mojang.logging.LogUtils;
import liedge.limacore.lib.ModResources;
import liedge.limatech.network.packet.LimaTechPacketsRegistration;
import liedge.limatech.registry.*;
import liedge.limatech.util.config.LimaTechClientConfig;
import liedge.limatech.util.config.LimaTechServerConfig;
import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
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
        LimaTechAttachmentTypes.initRegister(modBus);
        LimaTechBlockEntities.initRegister(modBus);
        LimaTechBlocks.initRegister(modBus);
        LimaTechCrafting.initRegister(modBus);
        LimaTechDataComponents.initRegister(modBus);
        LimaTechEntities.initRegister(modBus);
        LimaTechItems.initRegister(modBus);
        LimaTechMenus.initRegister(modBus);
        LimaTechMobEffects.initRegister(modBus);
        LimaTechParticles.initRegister(modBus);
        LimaTechSounds.initRegister(modBus);
        LimaTechTriggerTypes.initRegister(modBus);

        // Mod configs
        modContainer.registerConfig(ModConfig.Type.CLIENT, LimaTechClientConfig.CONFIG_SPEC);
        modContainer.registerConfig(ModConfig.Type.SERVER, LimaTechServerConfig.CONFIG_SPEC);

        // Mod event bus wrappers
        modBus.addListener(this::registerPayloadHandlers);
        modBus.addListener(this::registerCapabilities);
        modBus.addListener(ModConfigEvent.Loading.class, this::onModConfigLoaded);
        modBus.addListener(ModConfigEvent.Reloading.class, this::onModConfigLoaded);
    }

    private void onModConfigLoaded(final ModConfigEvent event)
    {
        LimaTechServerConfig.onConfigLoaded(event);
        LimaTechClientConfig.onConfigLoaded(event);
    }

    private void registerPayloadHandlers(final RegisterPayloadHandlersEvent event)
    {
        LimaTechPacketsRegistration.registerPacketHandlers(event.registrar(MODID).versioned("1.0.0"));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event)
    {
        LimaTechItems.registerCapabilities(event);
        LimaTechBlockEntities.registerCapabilities(event);

        // Entity capabilities
        event.registerEntity(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD, EntityType.PLAYER, (player, $) -> player.getData(LimaTechAttachmentTypes.BUBBLE_SHIELD));
    }
}