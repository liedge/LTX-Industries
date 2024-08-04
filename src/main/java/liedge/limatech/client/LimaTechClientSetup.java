package liedge.limatech.client;

import liedge.limatech.LimaTech;
import liedge.limatech.client.gui.ClientItemGridTooltip;
import liedge.limatech.client.gui.layer.BubbleShieldLayer;
import liedge.limatech.client.gui.layer.WeaponCrosshairLayer;
import liedge.limatech.client.gui.layer.WeaponHUDInfoLayer;
import liedge.limatech.client.gui.screen.FabricatorScreen;
import liedge.limatech.client.gui.screen.GrinderScreen;
import liedge.limatech.client.gui.screen.MaterialFusingChamberScreen;
import liedge.limatech.client.model.baked.DynamicModularGeometry;
import liedge.limatech.client.model.custom.BubbleShieldModel;
import liedge.limatech.client.model.entity.LimaTechModelLayers;
import liedge.limatech.client.model.entity.MissileModel;
import liedge.limatech.client.model.entity.OrbGrenadeModel;
import liedge.limatech.client.particle.*;
import liedge.limatech.client.renderer.blockentity.FabricatorRenderer;
import liedge.limatech.client.renderer.blockentity.RocketTurretRenderer;
import liedge.limatech.client.renderer.entity.MissileRenderer;
import liedge.limatech.client.renderer.entity.OrbGrenadeRenderer;
import liedge.limatech.client.renderer.item.LimaTechItemRenderers;
import liedge.limatech.menu.ItemGridTooltip;
import liedge.limatech.registry.LimaTechBlockEntities;
import liedge.limatech.registry.LimaTechEntities;
import liedge.limatech.registry.LimaTechItems;
import liedge.limatech.registry.LimaTechMenus;
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
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event)
    {
        event.registerItem(LimaTechItemRenderers.SUBMACHINE_GUN, LimaTechItems.SUBMACHINE_GUN.get());
        event.registerItem(LimaTechItemRenderers.SHOTGUN, LimaTechItems.SHOTGUN.get());
        event.registerItem(LimaTechItemRenderers.GRENADE_LAUNCHER, LimaTechItems.GRENADE_LAUNCHER.get());
        event.registerItem(LimaTechItemRenderers.ROCKET_LAUNCHER, LimaTechItems.ROCKET_LAUNCHER.get());
        event.registerItem(LimaTechItemRenderers.MAGNUM, LimaTechItems.MAGNUM.get());
    }

    @SubscribeEvent
    public static void registerMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(LimaTechMenus.GRINDER.get(), GrinderScreen::new);
        event.register(LimaTechMenus.MATERIAL_FUSING_CHAMBER.get(), MaterialFusingChamberScreen::new);
        event.register(LimaTechMenus.FABRICATOR.get(), FabricatorScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event)
    {
        event.registerSpecial(LIGHTFRAG_TRACER.get(), LightfragTracerParticle::new);
        registerPositionVelocity(event, MISSILE_TRAIL, AnimatedGlowParticle::missileTrail);
        registerPositionOnly(event, HALF_SONIC_BOOM, HalfSonicBoomParticle::new);
        event.registerSpecial(HALF_SONIC_BOOM_EMITTER.get(), HalfSonicBoomParticle.EmitterParticle::new);
        event.registerSpecial(CAMP_FLAME.get(), CampFlameParticle::new);
        event.registerSpecial(GROUND_ICICLE.get(), GroundIcicleParticle::new);
        registerPositionVelocity(event, MINI_ELECTRIC_SPARK, AnimatedGlowParticle::electricSpark);
        event.registerSprite(ACID_FALL_AMBIENT.get(), AcidSplashParticle::createAmbientFallParticle);
        event.registerSprite(ACID_FALL.get(), AcidSplashParticle::createFallParticle);
        event.registerSprite(ACID_LAND.get(), AcidSplashParticle::createLandParticle);
        event.registerSpecial(ELECTRIC_ARC.get(), ElectricArcParticle::new);
        event.registerSpecial(GRENADE_EXPLOSION.get(), GrenadeExplosionParticle::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        event.registerEntityRenderer(LimaTechEntities.ORB_GRENADE.get(), OrbGrenadeRenderer::new);
        event.registerEntityRenderer(LimaTechEntities.MISSILE.get(), MissileRenderer::new);

        // Block entities
        event.registerBlockEntityRenderer(LimaTechBlockEntities.FABRICATOR.get(), FabricatorRenderer::new);
        event.registerBlockEntityRenderer(LimaTechBlockEntities.ROCKET_TURRET.get(), RocketTurretRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(LimaTechModelLayers.ORB_GRENADE, OrbGrenadeModel::defineLayer);
        event.registerLayerDefinition(LimaTechModelLayers.MISSILE, MissileModel::defineLayer);
    }

    @SubscribeEvent
    public static void registerAdditionalModels(final ModelEvent.RegisterAdditional event)
    {
        event.register(RocketTurretRenderer.GUN_MODEL_PATH);
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
    }

    @SubscribeEvent
    public static void registerClientReloadListeners(final RegisterClientReloadListenersEvent event)
    {
        event.registerReloadListener((ResourceManagerReloadListener) LimaTechItemRenderers::reloadAll);
        event.registerReloadListener(BubbleShieldModel.SHIELD_MODEL);
    }
}