package liedge.ltxindustries.registry.game;

import liedge.limacore.registry.LimaDeferredItems;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXIIdentifiers;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.item.*;
import liedge.ltxindustries.item.tool.*;
import liedge.ltxindustries.item.weapon.*;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;
import static liedge.ltxindustries.item.LTXIItemRarities.ltxGearRarity;

public final class LTXIItems
{
    private LTXIItems() {}

    private static final LimaDeferredItems ITEMS = RESOURCES.deferredItems();

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
        bus.addListener(RegisterCapabilitiesEvent.class, LTXIItems::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        // Auto-register all energy-capable items
        for (Holder<Item> holder : ITEMS.getEntries())
        {
            ItemLike item = holder.value();
            if (item instanceof EnergyHolderItem) event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ignored) -> EnergyHolderItem.createEnergyAccess(stack), item);
        }
    }

    static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems()
    {
        return ITEMS.getEntries();
    }

    // #region Block items
    public static final DeferredItem<BlockItem> TITANIUM_ORE = ITEMS.registerSimpleBlockItem(LTXIBlocks.TITANIUM_ORE);
    public static final DeferredItem<BlockItem> DEEPSLATE_TITANIUM_ORE = ITEMS.registerSimpleBlockItem(LTXIBlocks.DEEPSLATE_TITANIUM_ORE);
    public static final DeferredItem<BlockItem> NIOBIUM_ORE = ITEMS.registerSimpleBlockItem(LTXIBlocks.NIOBIUM_ORE);
    public static final DeferredItem<BlockItem> RAW_TITANIUM_BLOCK = ITEMS.registerSimpleBlockItem(LTXIBlocks.RAW_TITANIUM_BLOCK);
    public static final DeferredItem<BlockItem> RAW_NIOBIUM_BLOCK = ITEMS.registerSimpleBlockItem(LTXIBlocks.RAW_NIOBIUM_BLOCK);
    public static final DeferredItem<BlockItem> RAW_TITANIUM_CLUSTER = ITEMS.registerSimpleBlockItem(LTXIBlocks.RAW_TITANIUM_CLUSTER);
    public static final DeferredItem<BlockItem> RAW_NIOBIUM_CLUSTER = ITEMS.registerSimpleBlockItem(LTXIBlocks.RAW_NIOBIUM_CLUSTER);
    public static final DeferredItem<BlockItem> TITANIUM_BLOCK = ITEMS.registerSimpleBlockItem(LTXIBlocks.TITANIUM_BLOCK);
    public static final DeferredItem<BlockItem> NIOBIUM_BLOCK = ITEMS.registerSimpleBlockItem(LTXIBlocks.NIOBIUM_BLOCK);
    public static final DeferredItem<BlockItem> SLATESTEEL_BLOCK = ITEMS.registerSimpleBlockItem(LTXIBlocks.SLATESTEEL_BLOCK);
    public static final Map<NeonLightColor, DeferredItem<BlockItem>> NEON_LIGHTS = LimaCollectionsUtil.fillAndCreateImmutableEnumMap(NeonLightColor.class, color -> ITEMS.registerSimpleBlockItem(LTXIBlocks.NEON_LIGHTS.get(color)));
    public static final DeferredItem<BlockItem> TITANIUM_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.TITANIUM_PANEL);
    public static final DeferredItem<BlockItem> SMOOTH_TITANIUM_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.SMOOTH_TITANIUM_PANEL);
    public static final DeferredItem<BlockItem> TILED_TITANIUM_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.TILED_TITANIUM_PANEL);
    public static final DeferredItem<BlockItem> TITANIUM_GLASS = ITEMS.registerSimpleBlockItem(LTXIBlocks.TITANIUM_GLASS);
    public static final DeferredItem<BlockItem> SLATESTEEL_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.SLATESTEEL_PANEL);
    public static final DeferredItem<BlockItem> SMOOTH_SLATESTEEL_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.SMOOTH_SLATESTEEL_PANEL);
    public static final DeferredItem<BlockItem> TILED_SLATESTEEL_PANEL = ITEMS.registerSimpleBlockItem(LTXIBlocks.TILED_SLATESTEEL_PANEL);
    public static final DeferredItem<BlockItem> EQUIPMENT_UPGRADE_STATION = ITEMS.registerSimpleBlockItem(LTXIBlocks.EQUIPMENT_UPGRADE_STATION, properties().stacksTo(1));
    public static final DeferredItem<ECABlockItem> ENERGY_CELL_ARRAY = ITEMS.registerBlockItem(LTXIBlocks.ENERGY_CELL_ARRAY, ECABlockItem::new, properties().stacksTo(1));
    public static final DeferredItem<InfiniteECABlockItem> INFINITE_ENERGY_CELL_ARRAY = ITEMS.registerBlockItem(LTXIBlocks.INFINITE_ENERGY_CELL_ARRAY, InfiniteECABlockItem::new, properties().stacksTo(1).rarity(Rarity.EPIC));
    public static final DeferredItem<BlockItem> ROCKET_TURRET = registerTurretBlockItem(LTXIBlocks.ROCKET_TURRET);
    public static final DeferredItem<BlockItem> RAILGUN_TURRET = registerTurretBlockItem(LTXIBlocks.RAILGUN_TURRET);
    public static final DeferredItem<BlockItem> DIGITAL_FURNACE = registerMachineBlockItem(LTXIBlocks.DIGITAL_FURNACE);
    public static final DeferredItem<BlockItem> DIGITAL_SMOKER = registerMachineBlockItem(LTXIBlocks.DIGITAL_SMOKER);
    public static final DeferredItem<BlockItem> DIGITAL_BLAST_FURNACE = registerMachineBlockItem(LTXIBlocks.DIGITAL_BLAST_FURNACE);
    public static final DeferredItem<BlockItem> GRINDER = registerMachineBlockItem(LTXIBlocks.GRINDER);
    public static final DeferredItem<BlockItem> MATERIAL_FUSING_CHAMBER = registerMachineBlockItem(LTXIBlocks.MATERIAL_FUSING_CHAMBER);
    public static final DeferredItem<BlockItem> ELECTROCENTRIFUGE = registerMachineBlockItem(LTXIBlocks.ELECTROCENTRIFUGE);
    public static final DeferredItem<BlockItem> MIXER = registerMachineBlockItem(LTXIBlocks.MIXER);
    public static final DeferredItem<BlockItem> VOLTAIC_INJECTOR = registerMachineBlockItem(LTXIBlocks.VOLTAIC_INJECTOR);
    public static final DeferredItem<BlockItem> CHEM_LAB = registerMachineBlockItem(LTXIBlocks.CHEM_LAB);
    public static final DeferredItem<BlockItem> ASSEMBLER = registerMachineBlockItem(LTXIBlocks.ASSEMBLER);
    public static final DeferredItem<BlockItem> GEO_SYNTHESIZER = registerMachineBlockItem(LTXIBlocks.GEO_SYNTHESIZER);
    public static final DeferredItem<BlockItem> FABRICATOR = registerMachineBlockItem(LTXIBlocks.FABRICATOR);
    public static final DeferredItem<BlockItem> AUTO_FABRICATOR = registerMachineBlockItem(LTXIBlocks.AUTO_FABRICATOR);
    public static final DeferredItem<BlockItem> MOLECULAR_RECONSTRUCTOR = registerMachineBlockItem(LTXIBlocks.MOLECULAR_RECONSTRUCTOR);
    public static final DeferredItem<BlockItem> DIGITAL_GARDEN = registerMachineBlockItem(LTXIBlocks.DIGITAL_GARDEN);
    // #endregion

    // Base mod materials
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> RAW_NIOBIUM = ITEMS.registerSimpleItem("raw_niobium");
    public static final DeferredItem<Item> NIOBIUM_INGOT = ITEMS.registerSimpleItem("niobium_ingot");
    public static final DeferredItem<Item> NIOBIUM_NUGGET = ITEMS.registerSimpleItem("niobium_nugget");
    public static final DeferredItem<BlockItem> SPARK_FRUIT = ITEMS.registerSimpleBlockItem(LTXIBlocks.SPARK_FRUIT);
    public static final DeferredItem<ItemNameBlockItem> VITRIOL_BERRIES = ITEMS.registerItem("vitriol_berries", properties -> new ItemNameBlockItem(LTXIBlocks.BILEVINE.get(), properties));
    public static final DeferredItem<BlockItem> GLOOM_SHROOM = ITEMS.registerSimpleBlockItem(LTXIBlocks.GLOOM_SHROOM);

    // Buckets
    public static final DeferredItem<BucketItem> VIRIDIC_ACID_BUCKET = ITEMS.registerItem("viridic_acid_bucket", properties -> new BucketItem(LTXIFluids.VIRIDIC_ACID.get(), properties), properties().stacksTo(1));
    public static final DeferredItem<BucketItem> HYDROGEN_BUCKET = ITEMS.registerItem("hydrogen_bucket", properties -> new BucketItem(LTXIFluids.HYDROGEN.get(), properties), properties().stacksTo(1));
    public static final DeferredItem<BucketItem> OXYGEN_BUCKET = ITEMS.registerItem("oxygen_bucket", properties -> new BucketItem(LTXIFluids.OXYGEN.get(), properties), properties().stacksTo(1));

    // Pigments
    public static final DeferredItem<Item> LTX_LIME_PIGMENT = ITEMS.registerSimpleItem("ltx_lime_pigment");
    public static final DeferredItem<Item> ENERGY_BLUE_PIGMENT = ITEMS.registerSimpleItem("energy_blue_pigment");
    public static final DeferredItem<Item> ELECTRIC_CHARTREUSE_PIGMENT = ITEMS.registerSimpleItem("electric_chartreuse_pigment");
    public static final DeferredItem<Item> VIRIDIC_GREEN_PIGMENT = ITEMS.registerSimpleItem("viridic_green_pigment");
    public static final DeferredItem<Item> NEURO_BLUE_PIGMENT = ITEMS.registerSimpleItem("neuro_blue_pigment");

    // Ore pebbles
    public static final DeferredItem<Item> COAL_ORE_PEBBLES = ITEMS.registerSimpleItem("coal_ore_pebbles");
    public static final DeferredItem<Item> COPPER_ORE_PEBBLES = ITEMS.registerSimpleItem("copper_ore_pebbles");
    public static final DeferredItem<Item> IRON_ORE_PEBBLES = ITEMS.registerSimpleItem("iron_ore_pebbles");
    public static final DeferredItem<Item> LAPIS_ORE_PEBBLES = ITEMS.registerSimpleItem("lapis_ore_pebbles");
    public static final DeferredItem<Item> REDSTONE_ORE_PEBBLES = ITEMS.registerSimpleItem("redstone_ore_pebbles");
    public static final DeferredItem<Item> GOLD_ORE_PEBBLES = ITEMS.registerSimpleItem("gold_ore_pebbles");
    public static final DeferredItem<Item> DIAMOND_ORE_PEBBLES = ITEMS.registerSimpleItem("diamond_ore_pebbles");
    public static final DeferredItem<Item> EMERALD_ORE_PEBBLES = ITEMS.registerSimpleItem("emerald_ore_pebbles");
    public static final DeferredItem<Item> QUARTZ_ORE_PEBBLES = ITEMS.registerSimpleItem("quartz_ore_pebbles");
    public static final DeferredItem<Item> NETHERITE_ORE_PEBBLES = ITEMS.registerSimpleItem("netherite_ore_pebbles");
    public static final DeferredItem<Item> TITANIUM_ORE_PEBBLES = ITEMS.registerSimpleItem("titanium_ore_pebbles");
    public static final DeferredItem<Item> NIOBIUM_ORE_PEBBLES = ITEMS.registerSimpleItem("niobium_ore_pebbles");
    public static final DeferredItem<Item> TIN_ORE_PEBBLES = ITEMS.registerSimpleItem("tin_ore_pebbles");
    public static final DeferredItem<Item> OSMIUM_ORE_PEBBLES = ITEMS.registerSimpleItem("osmium_ore_pebbles");
    public static final DeferredItem<Item> NICKEL_ORE_PEBBLES = ITEMS.registerSimpleItem("nickel_ore_pebbles");
    public static final DeferredItem<Item> LEAD_ORE_PEBBLES = ITEMS.registerSimpleItem("lead_ore_pebbles");
    public static final DeferredItem<Item> SILVER_ORE_PEBBLES = ITEMS.registerSimpleItem("silver_ore_pebbles");
    public static final DeferredItem<Item> URANIUM_ORE_PEBBLES = ITEMS.registerSimpleItem("uranium_ore_pebbles");

    // Components
    public static final DeferredItem<Item> TITANIUM_GEAR = ITEMS.registerSimpleItem("titanium_gear");
    public static final DeferredItem<Item> SLATESTEEL_GEAR = ITEMS.registerSimpleItem("slatesteel_gear");
    public static final DeferredItem<Item> CIRCUIT_BOARD = ITEMS.registerSimpleItem("circuit_board");
    public static final DeferredItem<SimpleHintItem> T1_CIRCUIT = registerSimpleHint("t1_circuit");
    public static final DeferredItem<SimpleHintItem> T2_CIRCUIT = registerSimpleHint("t2_circuit");
    public static final DeferredItem<SimpleHintItem> T3_CIRCUIT = registerSimpleHint("t3_circuit");
    public static final DeferredItem<SimpleHintItem> T4_CIRCUIT = registerSimpleHint("t4_circuit", properties().rarity(Rarity.RARE));
    public static final DeferredItem<SimpleHintItem> T5_CIRCUIT = registerSimpleHint("t5_circuit", properties().rarity(ltxGearRarity()));

    // LTX basic tools
    public static final DeferredItem<EnergyDrillItem> LTX_DRILL = registerLTXGear(LTXIIdentifiers.ID_LTX_DRILL, properties -> new EnergyDrillItem(properties, 5f, -2f));
    public static final DeferredItem<EnergySwordItem> LTX_SWORD = registerLTXGear(LTXIIdentifiers.ID_LTX_SWORD, properties -> new EnergySwordItem(properties, 9f, -1.5f));
    public static final DeferredItem<EnergyShovelItem> LTX_SHOVEL = registerLTXGear(LTXIIdentifiers.ID_LTX_SHOVEL, properties -> new EnergyShovelItem(properties, 7.5f, -2.2f));
    public static final DeferredItem<EnergyAxeItem> LTX_AXE = registerLTXGear(LTXIIdentifiers.ID_LTX_AXE, properties -> new EnergyAxeItem(properties, 12f, -2.8f));
    public static final DeferredItem<EnergyHoeItem> LTX_HOE = registerLTXGear(LTXIIdentifiers.ID_LTX_HOE, properties -> new EnergyHoeItem(properties, 3f, -1f));
    public static final DeferredItem<EnergyWrenchItem> LTX_WRENCH = registerLTXGear(LTXIIdentifiers.ID_LTX_WRENCH, EnergyWrenchItem::new);
    public static final DeferredItem<EnergyShearsItem> LTX_SHEARS = registerLTXGear(LTXIIdentifiers.ID_LTX_SHEARS, EnergyShearsItem::new);
    public static final DeferredItem<EnergyBrushItem> LTX_BRUSH = registerLTXGear(LTXIIdentifiers.ID_LTX_BRUSH, EnergyBrushItem::new);
    public static final DeferredItem<EnergyFishingRodItem> LTX_FISHING_ROD = registerLTXGear(LTXIIdentifiers.ID_LTX_FISHING_ROD, EnergyFishingRodItem::new);
    public static final DeferredItem<EnergyLighterItem> LTX_LIGHTER = registerLTXGear(LTXIIdentifiers.ID_LTX_LIGHTER, EnergyLighterItem::new);

    // LTX armor set
    public static final DeferredItem<EnergyArmorItem> WONDERLAND_HEAD = registerLTXGear(LTXIIdentifiers.ID_WONDERLAND_HEAD, properties -> new EnergyArmorItem(properties, EquipmentSlot.HEAD, 3f));
    public static final DeferredItem<EnergyArmorItem> WONDERLAND_BODY = registerLTXGear(LTXIIdentifiers.ID_WONDERLAND_BODY, properties -> new EnergyArmorItem(properties, EquipmentSlot.CHEST, 8f));
    public static final DeferredItem<EnergyArmorItem> WONDERLAND_LEGS = registerLTXGear(LTXIIdentifiers.ID_WONDERLAND_LEGS, properties -> new EnergyArmorItem(properties, EquipmentSlot.LEGS, 6f));
    public static final DeferredItem<EnergyArmorItem> WONDERLAND_FEET = registerLTXGear(LTXIIdentifiers.ID_WONDERLAND_FEET, properties -> new EnergyArmorItem(properties, EquipmentSlot.FEET, 3f));

    // Processed resources
    public static final DeferredItem<Item> CARBON_DUST = ITEMS.registerSimpleItem("carbon_dust");
    public static final DeferredItem<Item> RESINOUS_BIOMASS = ITEMS.registerSimpleItem("resinous_biomass");
    public static final DeferredItem<Item> ACIDIC_BIOMASS = ITEMS.registerSimpleItem("acidic_biomass");
    public static final DeferredItem<Item> DEEPSLATE_DUST = ITEMS.registerSimpleItem("deepslate_dust");

    // Chemicals
    public static final DeferredItem<Item> ELECTRIC_CHEMICAL = ITEMS.registerSimpleItem("electric_chemical");
    public static final DeferredItem<Item> MONOMER_CHEMICAL = ITEMS.registerSimpleItem("monomer_chemical");
    public static final DeferredItem<Item> VIRIDIC_WEAPON_CHEMICAL = ITEMS.registerSimpleItem("viridic_weapon_chemical");
    public static final DeferredItem<Item> CHORUS_CHEMICAL = ITEMS.registerSimpleItem("chorus_chemical");
    public static final DeferredItem<Item> SCULK_CHEMICAL = ITEMS.registerSimpleItem("sculk_chemical");
    public static final DeferredItem<Item> NEURO_CHEMICAL = ITEMS.registerSimpleItem("neuro_chemical");

    // Synthetic resources
    public static final DeferredItem<Item> SLATESTEEL_INGOT = ITEMS.registerSimpleItem("slatesteel_ingot");
    public static final DeferredItem<Item> SLATESTEEL_NUGGET = ITEMS.registerSimpleItem("slatesteel_nugget");
    public static final DeferredItem<Item> POLYMER_INGOT = ITEMS.registerSimpleItem("polymer_ingot");

    // Tech salvage modules
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_WEAPON_TECH_SALVAGE = registerSimpleHint("explosives_weapon_tech_salvage", properties().rarity(Rarity.RARE));
    public static final DeferredItem<SimpleHintItem> TARGETING_TECH_SALVAGE = registerSimpleHint("targeting_tech_salvage", properties().rarity(Rarity.RARE));

    // Upgrade
    public static final DeferredItem<Item> EMPTY_UPGRADE_MODULE = ITEMS.registerSimpleItem("empty_upgrade_module");
    public static final DeferredItem<EquipmentUpgradeModuleItem> EQUIPMENT_UPGRADE_MODULE = ITEMS.registerItem("equipment_upgrade_module", EquipmentUpgradeModuleItem::new, properties().stacksTo(1));
    public static final DeferredItem<MachineUpgradeModuleItem> MACHINE_UPGRADE_MODULE = ITEMS.registerItem("machine_upgrade_module", MachineUpgradeModuleItem::new, properties().stacksTo(1));

    // Data holding 'cards'
    public static final DeferredItem<SimpleHintItem> EMPTY_FABRICATION_BLUEPRINT = registerSimpleHint("empty_fabrication_blueprint");
    public static final DeferredItem<FabricationBlueprintItem> FABRICATION_BLUEPRINT = ITEMS.registerItem("fabrication_blueprint", FabricationBlueprintItem::new, properties().stacksTo(1));
    public static final DeferredItem<IOConfigCardItem> ITEMS_IO_CONFIG_CARD = ITEMS.registerItem("items_io_config_card", properties -> new IOConfigCardItem(properties, BlockEntityInputType.ITEMS), properties().stacksTo(1));
    public static final DeferredItem<IOConfigCardItem> ENERGY_IO_CONFIG_CARD = ITEMS.registerItem("energy_io_config_card", properties -> new IOConfigCardItem(properties, BlockEntityInputType.ENERGY), properties().stacksTo(1));
    public static final DeferredItem<IOConfigCardItem> FLUIDS_IO_CONFIG_CARD = ITEMS.registerItem("fluids_io_config_card", properties -> new IOConfigCardItem(properties, BlockEntityInputType.FLUIDS), properties().stacksTo(1));

    // Signature weapons
    public static final DeferredItem<GlowstickLauncherItem> GLOWSTICK_LAUNCHER = registerLTXGear(LTXIIdentifiers.ID_GLOWSTICK_LAUNCHER, GlowstickLauncherItem::new);
    public static final DeferredItem<SubmachineGunItem> SUBMACHINE_GUN = registerLTXGear(LTXIIdentifiers.ID_SUBMACHINE_GUN, SubmachineGunItem::new);
    public static final DeferredItem<ShotgunItem> SHOTGUN = registerLTXGear(LTXIIdentifiers.ID_SHOTGUN, ShotgunItem::new);
    public static final DeferredItem<GrenadeLauncherItem> GRENADE_LAUNCHER = registerLTXGear(LTXIIdentifiers.ID_GRENADE_LAUNCHER, GrenadeLauncherItem::new);
    public static final DeferredItem<LinearFusionRifleItem> LINEAR_FUSION_RIFLE = registerLTXGear(LTXIIdentifiers.ID_LINEAR_FUSION_RIFLE, LinearFusionRifleItem::new);
    public static final DeferredItem<RocketLauncherItem> ROCKET_LAUNCHER = registerLTXGear(LTXIIdentifiers.ID_ROCKET_LAUNCHER, RocketLauncherItem::new);
    public static final DeferredItem<HeavyPistolItem> HEAVY_PISTOL = registerLTXGear(LTXIIdentifiers.ID_HEAVY_PISTOL, HeavyPistolItem::new);

    // Weapon ammo items
    public static final DeferredItem<SimpleHintItem> LIGHTWEIGHT_WEAPON_ENERGY = registerSimpleHint("lightweight_weapon_energy");
    public static final DeferredItem<SimpleHintItem> SPECIALIST_WEAPON_ENERGY = registerSimpleHint("specialist_weapon_energy");
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_WEAPON_ENERGY = registerSimpleHint("explosives_weapon_energy");
    public static final DeferredItem<SimpleHintItem> HEAVY_WEAPON_ENERGY = registerSimpleHint("heavy_weapon_energy");

    private static DeferredItem<BlockItem> registerMachineBlockItem(Holder<Block> holder)
    {
        return ITEMS.registerBlockItem(holder, ContentsTooltipBlockItem::energyTooltipItem, properties().stacksTo(1));
    }

    private static DeferredItem<BlockItem> registerTurretBlockItem(Holder<Block> holder)
    {
        return ITEMS.registerBlockItem(holder, ContentsTooltipBlockItem::energyOwnerTooltipItem, properties().stacksTo(1).rarity(ltxGearRarity()));
    }

    private static DeferredItem<SimpleHintItem> registerSimpleHint(String name, Item.Properties properties)
    {
        return ITEMS.registerItem(name, SimpleHintItem::new, properties);
    }

    private static DeferredItem<SimpleHintItem> registerSimpleHint(String name)
    {
        return registerSimpleHint(name, properties());
    }

    private static <T extends Item> DeferredItem<T> registerLTXGear(String name, Function<Item.Properties, T> constructor)
    {
        return ITEMS.registerItem(name, constructor, properties().stacksTo(1).fireResistant().rarity(ltxGearRarity()));
    }

    @Contract(value = " -> new", pure = true)
    private static Item.@NotNull Properties properties()
    {
        return new Item.Properties();
    }
}