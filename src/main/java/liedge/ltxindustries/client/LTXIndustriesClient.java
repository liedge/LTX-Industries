package liedge.ltxindustries.client;

import com.mojang.logging.LogUtils;
import liedge.limacore.client.SimpleFogFluidExtension;
import liedge.limacore.lib.LimaColor;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.gui.ClientItemGridTooltip;
import liedge.ltxindustries.client.gui.ClientRecipeIngredientsTooltip;
import liedge.ltxindustries.client.gui.layer.BubbleShieldLayer;
import liedge.ltxindustries.client.gui.layer.CrosshairRenderer;
import liedge.ltxindustries.client.gui.layer.EquipmentHUDLayer;
import liedge.ltxindustries.client.gui.layer.WeaponCrosshairLayer;
import liedge.ltxindustries.client.gui.screen.*;
import liedge.ltxindustries.client.item.BlueprintClientItem;
import liedge.ltxindustries.client.item.MiningToolClientItem;
import liedge.ltxindustries.client.item.UpgradeModuleClientItem;
import liedge.ltxindustries.client.item.WeaponClientItem;
import liedge.ltxindustries.client.model.custom.BubbleShieldModel;
import liedge.ltxindustries.client.model.entity.*;
import liedge.ltxindustries.client.particle.*;
import liedge.ltxindustries.client.renderer.entity.GlowstickProjectileRenderer;
import liedge.ltxindustries.client.renderer.entity.RocketRenderer;
import liedge.ltxindustries.client.renderer.entity.ShellGrenadeRenderer;
import liedge.ltxindustries.client.renderer.entity.WonderlandArmorLayer;
import liedge.ltxindustries.data.LTXIReloadListeners;
import liedge.ltxindustries.menu.tooltip.ItemGridTooltip;
import liedge.ltxindustries.menu.tooltip.RecipeIngredientsTooltip;
import liedge.ltxindustries.registry.game.*;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.PlayerModelType;
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
            Stream.of(LTXIFluids.VIRIDIC_ACID, LTXIFluids.FLOWING_VIRIDIC_ACID, LTXIFluids.HYDROGEN, LTXIFluids.FLOWING_HYDROGEN, LTXIFluids.OXYGEN, LTXIFluids.FLOWING_OXYGEN)
                    .map(DeferredHolder::value).forEach(fluid -> ItemBlockRenderTypes.setRenderLayer(fluid, ChunkSectionLayer.TRANSLUCENT));
        }

        @SubscribeEvent
        private void registerTextureAtlas(final RegisterTextureAtlasesEvent event)
        {
            event.register(new AtlasManager.AtlasConfig(LTXIAtlasIds.UPGRADE_ICONS_TEXTURE, LTXIAtlasIds.UPGRADE_ICONS_ID, false));
        }

        @SubscribeEvent
        public void registerClientExtensions(final RegisterClientExtensionsEvent event)
        {
            event.registerItem(UpgradeModuleClientItem.INSTANCE, LTXIItems.EQUIPMENT_UPGRADE_MODULE, LTXIItems.MACHINE_UPGRADE_MODULE);
            event.registerItem(BlueprintClientItem.INSTANCE, LTXIItems.FABRICATION_BLUEPRINT);
            event.registerItem(MiningToolClientItem.INSTANCE, LTXIItems.LTX_DRILL, LTXIItems.LTX_SHOVEL, LTXIItems.LTX_AXE, LTXIItems.LTX_HOE);

            event.registerItem(new WeaponClientItem(CrosshairRenderer.bracketsWithHollowDot()), LTXIItems.GLOWSTICK_LAUNCHER);
            event.registerItem(new WeaponClientItem(CrosshairRenderer.circleBrackets()), LTXIItems.SHOTGUN);
            event.registerItem(new WeaponClientItem(CrosshairRenderer.aoeRing(0.2f)), LTXIItems.GRENADE_LAUNCHER);
            event.registerItem(new WeaponClientItem(CrosshairRenderer.aoeRing(0.1f)), LTXIItems.ROCKET_LAUNCHER);

            //event.registerItem(LTXIItemRenderers.SUBMACHINE_GUN, LTXIItems.SUBMACHINE_GUN.get());
            //event.registerItem(LTXIItemRenderers.SHOTGUN, LTXIItems.SHOTGUN.get());
            //event.registerItem(LTXIItemRenderers.GRENADE_LAUNCHER, LTXIItems.GRENADE_LAUNCHER.get());
            //event.registerItem(LTXIItemRenderers.LINEAR_FUSION_RIFLE, LTXIItems.LINEAR_FUSION_RIFLE.get());
            //event.registerItem(LTXIItemRenderers.ROCKET_LAUNCHER, LTXIItems.ROCKET_LAUNCHER.get());
            //event.registerItem(LTXIItemRenderers.HEAVY_PISTOL, LTXIItems.HEAVY_PISTOL.get());

            event.registerFluidType(SimpleFogFluidExtension.withDefaultPaths(LTXIFluids.VIRIDIC_ACID_TYPE, false, LimaColor.WHITE, LTXIConstants.ACID_GREEN, 13f), LTXIFluids.VIRIDIC_ACID_TYPE);

            // Gases
            final Identifier gasTexture = RESOURCES.id("block/gas");
            IntFunction<SimpleFogFluidExtension> gasExtensions = rgb ->
            {
                LimaColor color = LimaColor.createOpaque(rgb);
                // Just set fog distance to 0, gases don't have blocks
                return new SimpleFogFluidExtension(gasTexture, gasTexture, null, color.argb32(), SimpleFogFluidExtension.fogTintOf(color), 0f);
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
            event.register(LTXIMenus.GEO_SYNTHESIZER.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.FABRICATOR.get(), FabricatorScreen::new);
            event.register(LTXIMenus.AUTO_FABRICATOR.get(), AutoFabricatorScreen::new);
            event.register(LTXIMenus.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationScreen::new);
            event.register(LTXIMenus.MOLECULAR_RECONSTRUCTOR.get(), MolecularReconstructorScreen::new);
            event.register(LTXIMenus.DIGITAL_GARDEN.get(), RecipeLayoutScreen::new);
            event.register(LTXIMenus.ARC_TURRET.get(), TurretScreen::new);
            event.register(LTXIMenus.ROCKET_TURRET.get(), TurretScreen::new);
            event.register(LTXIMenus.RAILGUN_TURRET.get(), TurretScreen::new);
        }

        @SubscribeEvent
        public void registerParticleProviders(final RegisterParticleProvidersEvent event)
        {
            event.registerSpecial(LIGHTFRAG_TRACER.get(), new LightfragTracerParticle.Provider());
            event.registerSpriteSet(COLOR_GLITTER.get(), AnimatedGlowParticle.ColorGlitterProvider::new);
            event.registerSpriteSet(COLOR_FLASH.get(), ColorFlashParticle.Provider::new);
            event.registerSpriteSet(COLOR_FULL_SONIC_BOOM.get(), sprites -> new ColorSonicBoomParticle.Provider(sprites, true));
            event.registerSpriteSet(COLOR_HALF_SONIC_BOOM.get(), sprites -> new ColorSonicBoomParticle.Provider(sprites, false));
            event.registerSpecial(HALF_SONIC_BOOM_EMITTER.get(), new ColorSonicBoomParticle.EmitterProvider());
            event.registerSpecial(GROUND_ICICLE.get(), new GroundIcicleParticle.Provider());
            event.registerSpriteSet(CRYO_SNOWFLAKE.get(), AnimatedGlowParticle.SnowflakeProvider::new);
            event.registerSpriteSet(MINI_ELECTRIC_SPARK.get(), AnimatedGlowParticle.ElectricSparkProvider::new);
            event.registerSpecial(ENERGY_BOLT.get(), new EnergyBoltParticle.Provider());
            event.registerSpriteSet(CORROSIVE_DRIP.get(), sprites -> new AcidDripParticle.Provider(sprites, true, false));
            event.registerSpriteSet(ACID_FALL.get(), sprites -> new AcidDripParticle.Provider(sprites, true, true));
            event.registerSpriteSet(ACID_LAND.get(), sprites -> new AcidDripParticle.Provider(sprites, false, false));
            event.registerSpriteSet(NEURO_SMOKE.get(), BigColorSmokeParticle.NeuroSmokeProvider::new);
            event.registerSpecial(GRENADE_EXPLOSION.get(), new GrenadeExplosionParticle.Provider());
            event.registerSpecial(RAILGUN_BOLT.get(), new RailgunBoltParticle.Provider());
            event.registerSpecial(SHIELD_BREAK.get(), new ShieldBreakParticle.Provider());
        }

        @SubscribeEvent
        public void registerEntityRenderers(final EntityRenderersEvent.RegisterRenderers event)
        {
            // Entities
            event.registerEntityRenderer(LTXIEntities.GLOWSTICK_PROJECTILE.get(), GlowstickProjectileRenderer::new);
            event.registerEntityRenderer(LTXIEntities.SHELL_GRENADE.get(), ShellGrenadeRenderer::new);
            event.registerEntityRenderer(LTXIEntities.DAYBREAK_ROCKET.get(), RocketRenderer::new);
            event.registerEntityRenderer(LTXIEntities.TURRET_ROCKET.get(), RocketRenderer::new);
            event.registerEntityRenderer(LTXIEntities.FLAME_FIELD.get(), NoopRenderer::new);

            // Block entities
            //event.registerBlockEntityRenderer(LTXIBlockEntities.ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.INFINITE_ENERGY_CELL_ARRAY.get(), EnergyCellArrayRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.GRINDER.get(), GrinderRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.ELECTROCENTRIFUGE.get(), ElectroCentrifugeRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.MIXER.get(), MixerRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.VOLTAIC_INJECTOR.get(), VoltaicInjectorRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.FABRICATOR.get(), ctx -> new BaseFabricatorRenderer(ctx, -0.1875d, 1.0625d));
            //event.registerBlockEntityRenderer(LTXIBlockEntities.AUTO_FABRICATOR.get(), ctx -> new BaseFabricatorRenderer(ctx, 0, 0.375d));
            //event.registerBlockEntityRenderer(LTXIBlockEntities.EQUIPMENT_UPGRADE_STATION.get(), EquipmentUpgradeStationRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.DIGITAL_GARDEN.get(), DigitalGardenRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.ARC_TURRET.get(), ArcTurretRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.ROCKET_TURRET.get(), RocketTurretRenderer::new);
            //event.registerBlockEntityRenderer(LTXIBlockEntities.RAILGUN_TURRET.get(), RailgunTurretRenderer::new);
        }

        @SubscribeEvent
        public void registerLayerDefinitions(final EntityRenderersEvent.RegisterLayerDefinitions event)
        {
            event.registerLayerDefinition(LTXIModelLayers.GLOWSTICK_PROJECTILE, GlowstickProjectileModel::defineLayer);
            event.registerLayerDefinition(LTXIModelLayers.SHELL_GRENADE, ShellGrenadeModel::defineLayer);
            event.registerLayerDefinition(LTXIModelLayers.SMALL_ROCKET, SmallRocketModel::defineLayer);
            event.registerLayerDefinition(LTXIModelLayers.WONDERLAND_ARMOR_SET, WonderlandArmorModel::createArmorLayer);
        }

        @SubscribeEvent
        public void addRenderLayers(final EntityRenderersEvent.AddLayers event)
        {
            for (PlayerModelType modelType : event.getSkins())
            {
                AvatarRenderer<?> renderer = event.getPlayerRenderer(modelType);
                if (renderer != null)
                {
                    renderer.addLayer(new WonderlandArmorLayer(renderer, event.getEntityModels()));
                }
            }
        }

        @SubscribeEvent
        public void registerBlockColors(final RegisterColorHandlersEvent.Block event)
        {
            final BlockColor water = (state, level, pos, tintIndex) -> (level != null && pos != null && tintIndex == 1) ? BiomeColors.getAverageWaterColor(level, pos) : -1;
            event.register(water, LTXIBlocks.GEO_SYNTHESIZER.get(), LTXIBlocks.DIGITAL_GARDEN.get());
        }

        @SubscribeEvent
        public void registerKeyMappings(final RegisterKeyMappingsEvent event)
        {
            event.register(LTXIKeyMappings.RELOAD_KEY);
        }

        @SubscribeEvent
        public void registerGuiOverlays(final RegisterGuiLayersEvent event)
        {
            BubbleShieldLayer.INSTANCE.registerAbove(event, VanillaGuiLayers.ARMOR_LEVEL);
            EquipmentHUDLayer.INSTANCE.registerAbove(event, VanillaGuiLayers.HOTBAR);
            WeaponCrosshairLayer.INSTANCE.registerAbove(event, VanillaGuiLayers.CROSSHAIR);
        }

        @SubscribeEvent
        public void registerTooltipComponentFactories(final RegisterClientTooltipComponentFactoriesEvent event)
        {
            event.register(ItemGridTooltip.class, ClientItemGridTooltip::new);
            event.register(RecipeIngredientsTooltip.class, ClientRecipeIngredientsTooltip::new);
        }

        @SubscribeEvent
        public void registerClientReloadListeners(final AddClientReloadListenersEvent event)
        {
            event.addListener(LTXIReloadListeners.BUBBLE_SHIELD_MODEL, BubbleShieldModel.SHIELD_MODEL);
        }
    }
}