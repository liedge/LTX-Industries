package liedge.ltxindustries.client;

import com.mojang.logging.LogUtils;
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
import liedge.ltxindustries.client.model.entity.GlowstickProjectileModel;
import liedge.ltxindustries.client.model.entity.LTXIModelLayers;
import liedge.ltxindustries.client.model.entity.OrbGrenadeModel;
import liedge.ltxindustries.client.model.entity.RocketModel;
import liedge.ltxindustries.client.particle.*;
import liedge.ltxindustries.client.renderer.blockentity.*;
import liedge.ltxindustries.client.renderer.entity.GlowstickProjectileRenderer;
import liedge.ltxindustries.client.renderer.entity.OrbGrenadeRenderer;
import liedge.ltxindustries.client.renderer.entity.RocketRenderer;
import liedge.ltxindustries.client.renderer.entity.StickyFlameRenderer;
import liedge.ltxindustries.client.renderer.item.BlueprintItemExtensions;
import liedge.ltxindustries.client.renderer.item.LTXIItemRenderers;
import liedge.ltxindustries.client.renderer.item.UpgradeModuleItemExtensions;
import liedge.ltxindustries.menu.tooltip.FabricatorIngredientTooltip;
import liedge.ltxindustries.menu.tooltip.ItemGridTooltip;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.slf4j.Logger;

import java.util.function.IntFunction;
import java.util.stream.Stream;

import static liedge.limacore.client.particle.LimaParticleUtil.*;
import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.registry.game.LTXIParticles.*;

@Mod(value = LTXIndustries.MODID, dist = Dist.CLIENT)
public class LTXIndustriesClient
{
    public static final Logger CLIENT_LOGGER = LogUtils.getLogger();

    public LTXIndustriesClient(IEventBus modBus, ModContainer modContainer)
    {
        modBus.register(new ClientSetup());
    }

    private static class ClientSetup
    {
        @SubscribeEvent
        public void onClientSetup(final FMLClientSetupEvent event)
        {
            // Register item overrides
            event.enqueueWork(LTXIItemOverrides::registerOverrides);

            Stream.of(LTXIFluids.VIRIDIC_ACID, LTXIFluids.FLOWING_VIRIDIC_ACID, LTXIFluids.HYDROGEN, LTXIFluids.FLOWING_HYDROGEN, LTXIFluids.OXYGEN, LTXIFluids.FLOWING_OXYGEN)
                    .map(DeferredHolder::value).forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, RenderType.translucent()));
        }

        @SubscribeEvent
        public void registerRecipeBookCategories(final RegisterRecipeBookCategoriesEvent event)
        {
            event.registerRecipeCategoryFinder(LTXIRecipeTypes.FABRICATING.get(), $ -> LTXIClientRecipes.FABRICATING_CATEGORY.getValue());
        }

        @SubscribeEvent
        public void registerClientExtensions(final RegisterClientExtensionsEvent event)
        {
            event.registerItem(UpgradeModuleItemExtensions.getInstance(), LTXIItems.EQUIPMENT_UPGRADE_MODULE.get(), LTXIItems.MACHINE_UPGRADE_MODULE.get());
            event.registerItem(BlueprintItemExtensions.INSTANCE, LTXIItems.FABRICATION_BLUEPRINT);

            event.registerItem(LTXIItemRenderers.GLOWSTICK_LAUNCHER, LTXIItems.GLOWSTICK_LAUNCHER.get());
            event.registerItem(LTXIItemRenderers.SUBMACHINE_GUN, LTXIItems.SUBMACHINE_GUN.get());
            event.registerItem(LTXIItemRenderers.SHOTGUN, LTXIItems.SHOTGUN.get());
            event.registerItem(LTXIItemRenderers.GRENADE_LAUNCHER, LTXIItems.GRENADE_LAUNCHER.get());
            event.registerItem(LTXIItemRenderers.LINEAR_FUSION_RIFLE, LTXIItems.LINEAR_FUSION_RIFLE.get());
            event.registerItem(LTXIItemRenderers.ROCKET_LAUNCHER, LTXIItems.ROCKET_LAUNCHER.get());
            event.registerItem(LTXIItemRenderers.HEAVY_PISTOL, LTXIItems.HEAVY_PISTOL.get());

            event.registerFluidType(LimaFluidClientExtensions.create(LTXIFluids.VIRIDIC_ACID_TYPE, false, null, LimaColor.WHITE, LTXIConstants.ACID_GREEN, 13.5f), LTXIFluids.VIRIDIC_ACID_TYPE);

            // Gases
            final ResourceLocation gasTexture = RESOURCES.location("block/gas");
            IntFunction<LimaFluidClientExtensions> gasExtensions = rgb ->
            {
                LimaColor color = LimaColor.createOpaque(rgb);
                // Just set fog distance to 0, gases don't have blocks
                return new LimaFluidClientExtensions(gasTexture, gasTexture, null, null, color.argb32(), LimaFluidClientExtensions.fogTintFromColor(color), 0f);
            };
            event.registerFluidType(gasExtensions.apply(0xe7e7e7), LTXIFluids.HYDROGEN_TYPE);
            event.registerFluidType(gasExtensions.apply(0x91a5d5), LTXIFluids.OXYGEN_TYPE);
        }

        @SubscribeEvent
        public void registerMenuScreens(final RegisterMenuScreensEvent event)
        {
            event.register(LTXIMenus.BLOCK_IO_CONFIGURATION.get(), BlockIOConfigurationScreen::new);
            event.register(LTXIMenus.MACHINE_UPGRADES.get(), MachineUpgradesScreen::new);
            event.register(LTXIMenus.RECIPE_MODE_SELECT.get(), RecipeModeScreen::new);

            event.register(LTXIMenus.ENERGY_CELL_ARRAY.get(), EnergyCellArrayScreen::new);
            event.register(LTXIMenus.DIGITAL_FURNACE.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.DIGITAL_SMOKER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.DIGITAL_BLAST_FURNACE.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.GRINDER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.MATERIAL_FUSING_CHAMBER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.ELECTROCENTRIFUGE.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.MIXER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.VOLTAIC_INJECTOR.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.CHEM_LAB.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.ASSEMBLER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.FABRICATOR.get(), FabricatorScreen::new);
            event.register(LTXIMenus.AUTO_FABRICATOR.get(), AutoFabricatorScreen::new);
            event.register(LTXIMenus.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationScreen::new);
            event.register(LTXIMenus.MOLECULAR_RECONSTRUCTOR.get(), MolecularReconstructorScreen::new);
            event.register(LTXIMenus.DIGITAL_GARDEN.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.ROCKET_TURRET.get(), TurretScreen::new);
            event.register(LTXIMenus.RAILGUN_TURRET.get(), TurretScreen::new);
        }

        @SubscribeEvent
        public void registerParticleProviders(final RegisterParticleProvidersEvent event)
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
        public void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event)
        {
            // Entities
            event.registerEntityRenderer(LTXIEntities.GLOWSTICK_PROJECTILE.get(), GlowstickProjectileRenderer::new);
            event.registerEntityRenderer(LTXIEntities.ORB_GRENADE.get(), OrbGrenadeRenderer::new);
            event.registerEntityRenderer(LTXIEntities.DAYBREAK_ROCKET.get(), RocketRenderer::new);
            event.registerEntityRenderer(LTXIEntities.TURRET_ROCKET.get(), RocketRenderer::new);
            event.registerEntityRenderer(LTXIEntities.STICKY_FLAME.get(), StickyFlameRenderer::new);

            // Block entities
            event.registerBlockEntityRenderer(LTXIBlockEntities.ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.INFINITE_ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.ELECTROCENTRIFUGE.get(), ElectroCentrifugeRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.MIXER.get(), MixerRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.FABRICATOR.get(), ctx -> new BaseFabricatorRenderer(ctx, -0.1875d, 1.0625d));
            event.registerBlockEntityRenderer(LTXIBlockEntities.AUTO_FABRICATOR.get(), ctx -> new BaseFabricatorRenderer(ctx, 0, 0.375d));
            event.registerBlockEntityRenderer(LTXIBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.DIGITAL_GARDEN.get(), DigitalGardenRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.ROCKET_TURRET.get(), RocketTurretRenderer::new);
            event.registerBlockEntityRenderer(LTXIBlockEntities.RAILGUN_TURRET.get(), RailgunTurretRenderer::new);
        }

        @SubscribeEvent
        public void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(LTXIModelLayers.GLOWSTICK_PROJECTILE, GlowstickProjectileModel::defineLayer);
            event.registerLayerDefinition(LTXIModelLayers.ORB_GRENADE, OrbGrenadeModel::defineLayer);
            event.registerLayerDefinition(LTXIModelLayers.ROCKET, RocketModel::defineLayer);
        }

        @SubscribeEvent
        public void registerBlockColors(final RegisterColorHandlersEvent.Block event)
        {
            final BlockColor water = (state, level, pos, tintIndex) -> (level != null && pos != null && tintIndex == 1) ? BiomeColors.getAverageWaterColor(level, pos) : -1;
            event.register(water, LTXIBlocks.DIGITAL_GARDEN.get());
        }

        @SubscribeEvent
        public void registerGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event)
        {
            EmissiveBiLayerGeometry.BI_LAYER_LOADER.registerLoader(event);
        }

        @SubscribeEvent
        public void registerKeyMappings(final RegisterKeyMappingsEvent event)
        {
            event.register(LTXIKeyMappings.RELOAD_KEY);
        }

        @SubscribeEvent
        public void registerGuiOverlays(final RegisterGuiLayersEvent event)
        {
            BubbleShieldLayer.BUBBLE_SHIELD_LAYER.registerAbove(event, VanillaGuiLayers.PLAYER_HEALTH);
            WeaponHUDInfoLayer.WEAPON_HUD_INFO_LAYER.registerAbove(event, VanillaGuiLayers.HOTBAR);
            WeaponCrosshairLayer.CROSSHAIR_LAYER.registerAbove(event, VanillaGuiLayers.CROSSHAIR);
        }

        @SubscribeEvent
        public void registerTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent event)
        {
            event.register(ItemGridTooltip.class, ClientItemGridTooltip::new);
            event.register(FabricatorIngredientTooltip.class, ClientFabricatorIngredientTooltip::new);
        }

        @SubscribeEvent
        public void registerClientReloadListeners(final RegisterClientReloadListenersEvent event)
        {
            event.registerReloadListener((ResourceManagerReloadListener) LTXIItemRenderers::reloadAll);
            event.registerReloadListener(BubbleShieldModel.SHIELD_MODEL);
            event.registerReloadListener(UpgradeIconSprites.getInstance());
        }
    }
}