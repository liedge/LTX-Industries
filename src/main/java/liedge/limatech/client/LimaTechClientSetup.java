package liedge.limatech.client;

import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.ClientFabricatorIngredientTooltip;
import liedge.limatech.client.gui.ClientItemGridTooltip;
import liedge.limatech.client.gui.UpgradeIconTextures;
import liedge.limatech.client.gui.layer.BubbleShieldLayer;
import liedge.limatech.client.gui.layer.WeaponCrosshairLayer;
import liedge.limatech.client.gui.layer.WeaponHUDInfoLayer;
import liedge.limatech.client.gui.screen.*;
import liedge.limatech.client.model.baked.DynamicModularGeometry;
import liedge.limatech.client.model.baked.LimaTechExtraBakedModels;
import liedge.limatech.client.model.custom.BubbleShieldModel;
import liedge.limatech.client.model.entity.LimaTechModelLayers;
import liedge.limatech.client.model.entity.OrbGrenadeModel;
import liedge.limatech.client.model.entity.RocketModel;
import liedge.limatech.client.particle.*;
import liedge.limatech.client.renderer.blockentity.EnergyStorageArrayRenderer;
import liedge.limatech.client.renderer.blockentity.EquipmentUpgradeStationRenderer;
import liedge.limatech.client.renderer.blockentity.FabricatorRenderer;
import liedge.limatech.client.renderer.blockentity.RocketTurretRenderer;
import liedge.limatech.client.renderer.entity.OrbGrenadeRenderer;
import liedge.limatech.client.renderer.entity.RocketRenderer;
import liedge.limatech.client.renderer.entity.StickyFlameRenderer;
import liedge.limatech.client.renderer.item.LimaTechItemRenderers;
import liedge.limatech.client.renderer.item.UpgradeModuleItemExtensions;
import liedge.limatech.menu.tooltip.FabricatorIngredientTooltip;
import liedge.limatech.menu.tooltip.ItemGridTooltip;
import liedge.limatech.registry.*;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static liedge.limacore.client.particle.SpriteSetParticleProvider.registerPositionOnly;
import static liedge.limacore.client.particle.SpriteSetParticleProvider.registerPositionVelocity;
import static liedge.limatech.registry.LimaTechParticles.*;

@EventBusSubscriber(modid = LimaTech.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LimaTechClientSetup
{
    private LimaTechClientSetup() {}

    @SubscribeEvent
    public static void registerBlockColorHandlers(final RegisterColorHandlersEvent.Block event)
    {
        event.register(EnergyStorageArrayRenderer.ESA_BLOCK_COLOR, LimaTechBlocks.ENERGY_STORAGE_ARRAY.get(), LimaTechBlocks.INFINITE_ENERGY_STORAGE_ARRAY.get());
    }

    @SubscribeEvent
    public static void registerItemColorHandlers(final RegisterColorHandlersEvent.Item event)
    {
        event.register(EnergyStorageArrayRenderer.TIERED_ESA_COLOR, LimaTechBlocks.ENERGY_STORAGE_ARRAY);
        event.register(EnergyStorageArrayRenderer.INFINITE_ESA_COLOR, LimaTechBlocks.INFINITE_ENERGY_STORAGE_ARRAY);
    }

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event)
    {
        event.registerItem(UpgradeModuleItemExtensions.getInstance(), LimaTechItems.EQUIPMENT_UPGRADE_MODULE.get(), LimaTechItems.MACHINE_UPGRADE_MODULE.get());

        event.registerItem(LimaTechItemRenderers.SUBMACHINE_GUN, LimaTechItems.SUBMACHINE_GUN.get());
        event.registerItem(LimaTechItemRenderers.SHOTGUN, LimaTechItems.SHOTGUN.get());
        event.registerItem(LimaTechItemRenderers.GRENADE_LAUNCHER, LimaTechItems.GRENADE_LAUNCHER.get());
        event.registerItem(LimaTechItemRenderers.ROCKET_LAUNCHER, LimaTechItems.ROCKET_LAUNCHER.get());
        event.registerItem(LimaTechItemRenderers.MAGNUM, LimaTechItems.MAGNUM.get());
    }

    @SubscribeEvent
    public static void registerMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(LimaTechMenus.MACHINE_UPGRADES.get(), MachineUpgradesScreen::new);
        event.register(LimaTechMenus.MACHINE_IO_CONTROL.get(), IOControlScreen::new);
        event.register(LimaTechMenus.ENERGY_STORAGE_ARRAY.get(), EnergyStorageArrayScreen::new);
        event.register(LimaTechMenus.DIGITAL_FURNACE.get(), DigitalFurnaceScreen::new);
        event.register(LimaTechMenus.GRINDER.get(), GrinderScreen::new);
        event.register(LimaTechMenus.RECOMPOSER.get(), RecomposerScreen::new);
        event.register(LimaTechMenus.MATERIAL_FUSING_CHAMBER.get(), MaterialFusingChamberScreen::new);
        event.register(LimaTechMenus.FABRICATOR.get(), FabricatorScreen::new);
        event.register(LimaTechMenus.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event)
    {
        event.registerSpecial(LIGHTFRAG_TRACER.get(), LightfragTracerParticle::new);
        registerPositionVelocity(event, COLOR_GLITTER, AnimatedGlowParticle::colorGlitter);
        event.registerSprite(COLOR_FLASH.get(), ColorFlashParticle::new);
        registerPositionOnly(event, HALF_SONIC_BOOM, HalfSonicBoomParticle::new);
        event.registerSpecial(HALF_SONIC_BOOM_EMITTER.get(), HalfSonicBoomParticle.EmitterParticle::new);
        event.registerSpecial(GROUND_ICICLE.get(), GroundIcicleParticle::new);
        registerPositionVelocity(event, CRYO_SNOWFLAKE, AnimatedGlowParticle::cryoSnowflake);
        registerPositionVelocity(event, MINI_ELECTRIC_SPARK, AnimatedGlowParticle::electricSpark);
        event.registerSpecial(FIXED_ELECTRIC_BOLT.get(), FixedElectricBoltParticle::new);
        event.registerSprite(CORROSIVE_DRIP.get(), AcidDripParticle::corrosiveDripParticle);
        event.registerSprite(ACID_FALL.get(), AcidDripParticle::createFallParticle);
        event.registerSprite(ACID_LAND.get(), AcidDripParticle::createLandParticle);
        registerPositionVelocity(event, NEURO_SMOKE, BigColorSmokeParticle::neuroSmokeParticle);
        event.registerSpecial(GRENADE_EXPLOSION.get(), GrenadeExplosionParticle::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        event.registerEntityRenderer(LimaTechEntities.ORB_GRENADE.get(), OrbGrenadeRenderer::new);
        event.registerEntityRenderer(LimaTechEntities.DAYBREAK_ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(LimaTechEntities.TURRET_ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(LimaTechEntities.STICKY_FLAME.get(), StickyFlameRenderer::new);

        // Block entities
        event.registerBlockEntityRenderer(LimaTechBlockEntities.ENERGY_STORAGE_ARRAY.get(), EnergyStorageArrayRenderer::new);
        event.registerBlockEntityRenderer(LimaTechBlockEntities.INFINITE_ENERGY_STORAGE_ARRAY.get(), EnergyStorageArrayRenderer::new);
        event.registerBlockEntityRenderer(LimaTechBlockEntities.FABRICATOR.get(), FabricatorRenderer::new);
        event.registerBlockEntityRenderer(LimaTechBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationRenderer::new);
        event.registerBlockEntityRenderer(LimaTechBlockEntities.ROCKET_TURRET.get(), RocketTurretRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(LimaTechModelLayers.ORB_GRENADE, OrbGrenadeModel::defineLayer);
        event.registerLayerDefinition(LimaTechModelLayers.ROCKET, RocketModel::defineLayer);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(final ModelEvent.RegisterAdditional event)
    {
        event.register(LimaTechExtraBakedModels.ROCKET_TURRET_GUN);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
    {
        DynamicModularGeometry.DYNAMIC_MODEL_LOADER.registerLoader(event);
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(LimaTechKeyMappings.RELOAD_KEY);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(final RegisterGuiLayersEvent event)
    {
        BubbleShieldLayer.BUBBLE_SHIELD_LAYER.registerAbove(event, VanillaGuiLayers.PLAYER_HEALTH);
        WeaponHUDInfoLayer.WEAPON_HUD_INFO_LAYER.registerAbove(event, VanillaGuiLayers.HOTBAR);
        WeaponCrosshairLayer.CROSSHAIR_LAYER.registerAbove(event, VanillaGuiLayers.CROSSHAIR);
    }

    @SubscribeEvent
    public static void registerTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent event)
    {
        event.register(ItemGridTooltip.class, ClientItemGridTooltip::new);
        event.register(FabricatorIngredientTooltip.class, ClientFabricatorIngredientTooltip::new);
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) LimaTechItemRenderers::reloadAll);
        event.registerReloadListener(BubbleShieldModel.SHIELD_MODEL);
        event.registerReloadListener(UpgradeIconTextures.getUpgradeSprites());
    }
}