package liedge.ltxindustries.client;

import liedge.limacore.client.LimaFluidClientExtensions;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.ClientFabricatorIngredientTooltip;
import liedge.ltxindustries.client.gui.ClientItemGridTooltip;
import liedge.ltxindustries.client.gui.UpgradeIconSprites;
import liedge.ltxindustries.client.gui.layer.BubbleShieldLayer;
import liedge.ltxindustries.client.gui.layer.WeaponCrosshairLayer;
import liedge.ltxindustries.client.gui.layer.WeaponHUDInfoLayer;
import liedge.ltxindustries.client.gui.screen.*;
import liedge.ltxindustries.client.model.baked.EmissiveBiLayerGeometry;
import liedge.ltxindustries.client.model.custom.BubbleShieldModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.OrbGrenadeModel;
import liedge.ltxindustries.client.model.entity.RocketModel;
import liedge.ltxindustries.client.particle.*;
import liedge.ltxindustries.client.renderer.blockentity.*;
import liedge.ltxindustries.client.renderer.entity.OrbGrenadeRenderer;
import liedge.ltxindustries.client.renderer.entity.RocketRenderer;
import liedge.ltxindustries.client.renderer.entity.StickyFlameRenderer;
import liedge.ltxindustries.client.renderer.item.BlueprintItemExtensions;
import liedge.ltxindustries.client.renderer.item.LTXIItemRenderers;
import liedge.ltxindustries.client.renderer.item.UpgradeModuleItemExtensions;
import liedge.ltxindustries.menu.tooltip.FabricatorIngredientTooltip;
import liedge.ltxindustries.menu.tooltip.ItemGridTooltip;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static liedge.limacore.client.particle.LimaParticleUtil.*;
import static liedge.ltxindustries.registry.game.LTXIParticles.*;

@EventBusSubscriber(modid = LTXIndustries.MODID, value = Dist.CLIENT)
public final class LTXIClientSetup
{
    private LTXIClientSetup() {}

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        // Register item overrides
        event.enqueueWork(LTXIItemOverrides::registerOverrides);

        ItemBlockRenderTypes.setRenderLayer(LTXIFluids.VIRIDIC_ACID.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(LTXIFluids.FLOWING_VIRIDIC_ACID.get(), RenderType.translucent());
    }

    @SubscribeEvent
    public static void registerRecipeBookCategories(final RegisterRecipeBookCategoriesEvent event)
    {
        event.registerRecipeCategoryFinder(LTXIRecipeTypes.FABRICATING.get(), $ -> LTXIClientRecipes.FABRICATING_CATEGORY.getValue());
    }

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event)
    {
        event.registerItem(UpgradeModuleItemExtensions.getInstance(), LTXIItems.EQUIPMENT_UPGRADE_MODULE.get(), LTXIItems.MACHINE_UPGRADE_MODULE.get());
        event.registerItem(BlueprintItemExtensions.INSTANCE, LTXIItems.FABRICATION_BLUEPRINT);

        event.registerItem(LTXIItemRenderers.SUBMACHINE_GUN, LTXIItems.SUBMACHINE_GUN.get());
        event.registerItem(LTXIItemRenderers.SHOTGUN, LTXIItems.SHOTGUN.get());
        event.registerItem(LTXIItemRenderers.GRENADE_LAUNCHER, LTXIItems.GRENADE_LAUNCHER.get());
        event.registerItem(LTXIItemRenderers.LINEAR_FUSION_RIFLE, LTXIItems.LINEAR_FUSION_RIFLE.get());
        event.registerItem(LTXIItemRenderers.ROCKET_LAUNCHER, LTXIItems.ROCKET_LAUNCHER.get());
        event.registerItem(LTXIItemRenderers.HEAVY_PISTOL, LTXIItems.HEAVY_PISTOL.get());

        event.registerFluidType(LimaFluidClientExtensions.create(LTXIFluids.VIRIDIC_ACID_TYPE, false, null, LimaColor.WHITE, LTXIConstants.ACID_GREEN, 13.5f), LTXIFluids.VIRIDIC_ACID_TYPE);
    }

    @SubscribeEvent
    public static void registerMenuScreens(final RegisterMenuScreensEvent event)
    {
        event.register(LTXIMenus.MACHINE_UPGRADES.get(), MachineUpgradesScreen::new);
        event.register(LTXIMenus.MACHINE_IO_CONTROL.get(), IOControllerScreen::new);
        event.register(LTXIMenus.ENERGY_CELL_ARRAY.get(), EnergyCellArrayScreen::new);
        event.register(LTXIMenus.DIGITAL_FURNACE.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.DIGITAL_SMOKER.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.DIGITAL_BLAST_FURNACE.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.GRINDER.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.MATERIAL_FUSING_CHAMBER.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.ELECTROCENTRIFUGE.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.MIXER.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.CHEM_LAB.get(), RecipeLayoutScreen::new);
        event.register(LTXIMenus.FABRICATOR.get(), FabricatorScreen::new);
        event.register(LTXIMenus.AUTO_FABRICATOR.get(), AutoFabricatorScreen::new);
        event.register(LTXIMenus.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationScreen::new);
        event.register(LTXIMenus.MOLECULAR_RECONSTRUCTOR.get(), MolecularReconstructorScreen::new);
        event.register(LTXIMenus.ROCKET_TURRET.get(), TurretScreen::new);
        event.register(LTXIMenus.RAILGUN_TURRET.get(), TurretScreen::new);
    }

    @SubscribeEvent
    public static void registerParticleProviders(final RegisterParticleProvidersEvent event)
    {
        event.registerSpecial(LIGHTFRAG_TRACER.get(), LightfragTracerParticle::createLightfragTracer);
        registerSprites(event, COLOR_GLITTER, AnimatedGlowParticle::colorGlitter);
        event.registerSprite(COLOR_FLASH.get(), ColorFlashParticle::new);
        registerSpritesPosOnly(event, COLOR_FULL_SONIC_BOOM, ColorSonicBoomParticle::fullSonicBoom);
        registerSpritesPosOnly(event, COLOR_HALF_SONIC_BOOM, ColorSonicBoomParticle::halfSonicBoom);
        registerSpecialPosOnly(event, HALF_SONIC_BOOM_EMITTER, ColorSonicBoomParticle.EmitterParticle::new);
        registerSpecialPosOnly(event, GROUND_ICICLE, GroundIcicleParticle::new);
        registerSprites(event, CRYO_SNOWFLAKE, AnimatedGlowParticle::cryoSnowflake);
        registerSprites(event, MINI_ELECTRIC_SPARK, AnimatedGlowParticle::electricSpark);
        event.registerSpecial(FIXED_ELECTRIC_BOLT.get(), FixedElectricBoltParticle::create);
        event.registerSprite(CORROSIVE_DRIP.get(), AcidDripParticle::corrosiveDripParticle);
        event.registerSprite(ACID_FALL.get(), AcidDripParticle::createFallParticle);
        event.registerSprite(ACID_LAND.get(), AcidDripParticle::createLandParticle);
        registerSprites(event, NEURO_SMOKE, BigColorSmokeParticle::neuroSmokeParticle);
        registerSpecialPosOnly(event, GRENADE_EXPLOSION, GrenadeExplosionParticle::new);
        event.registerSpecial(RAILGUN_BOLT.get(), RailgunBoltParticle::create);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event)
    {
        // Entities
        event.registerEntityRenderer(LTXIEntities.ORB_GRENADE.get(), OrbGrenadeRenderer::new);
        event.registerEntityRenderer(LTXIEntities.DAYBREAK_ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(LTXIEntities.TURRET_ROCKET.get(), RocketRenderer::new);
        event.registerEntityRenderer(LTXIEntities.STICKY_FLAME.get(), StickyFlameRenderer::new);

        // Block entities
        event.registerBlockEntityRenderer(LTXIBlockEntities.ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.INFINITE_ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.ELECTROCENTRIFUGE.get(), ElectroCentrifugeRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.MIXER.get(), MixerRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.FABRICATOR.get(), BaseFabricatorRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.AUTO_FABRICATOR.get(), BaseFabricatorRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.ROCKET_TURRET.get(), RocketTurretRenderer::new);
        event.registerBlockEntityRenderer(LTXIBlockEntities.RAILGUN_TURRET.get(), RailgunTurretRenderer::new);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(LTXIModelLayers.ORB_GRENADE, OrbGrenadeModel::defineLayer);
        event.registerLayerDefinition(LTXIModelLayers.ROCKET, RocketModel::defineLayer);
    }

    @SubscribeEvent
    public static void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
    {
        EmissiveBiLayerGeometry.BI_LAYER_LOADER.registerLoader(event);
    }

    @SubscribeEvent
    public static void registerKeyMappings(final RegisterKeyMappingsEvent event)
    {
        event.register(LTXIKeyMappings.RELOAD_KEY);
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
        event.registerReloadListener((ResourceManagerReloadListener) LTXIItemRenderers::reloadAll);
        event.registerReloadListener(BubbleShieldModel.SHIELD_MODEL);
        event.registerReloadListener(UpgradeIconSprites.getInstance());
    }
}