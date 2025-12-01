package liedge.ltxindustries.integration.ae2;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import org.slf4j.Logger;

@EventBusSubscriber
public final class AE2Integration
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean isAE2Loaded()
    {
        return ModList.get().isLoaded("ae2");
    }

    @SubscribeEvent
    static void onCommonSetup(final FMLCommonSetupEvent event)
    {
        if (isAE2Loaded())
        {
            BlueprintDecoder.registerDecoder();
            LOGGER.info("Registered fabrication blueprint AE2 decoder");
        }
    }

    @SubscribeEvent
    static void onRegisterCapabilities(final RegisterCapabilitiesEvent event)
    {
        if (isAE2Loaded())
        {
            AutoFabricatorCraftingMachine.registerCapability(event);
            LOGGER.info("Registered Auto Fabricator crafting machine capability for AE2.");
        }
    }
}