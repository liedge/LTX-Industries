package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limacore.lib.ModResources;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.client.LTXIKeyMappings;
import liedge.ltxindustries.item.SimpleHintItem;
import liedge.ltxindustries.item.tool.ToolSpeed;
import liedge.ltxindustries.item.weapon.GrenadeLauncherItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseBuilder;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.recipe.RecipeMode;
import liedge.ltxindustries.registry.bootstrap.*;
import liedge.ltxindustries.registry.game.*;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredItem;

import static liedge.ltxindustries.client.LTXILangKeys.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.*;
import static liedge.ltxindustries.registry.game.LTXIBlocks.GLOOM_SHROOM;
import static liedge.ltxindustries.registry.game.LTXIBlocks.SPARK_FRUIT;
import static liedge.ltxindustries.registry.game.LTXICreativeTabs.*;
import static liedge.ltxindustries.registry.game.LTXIItems.*;
import static liedge.ltxindustries.registry.game.LTXIRecipeTypes.*;
import static liedge.ltxindustries.registry.game.LTXISounds.*;

class LanguageGen extends LimaLanguageProvider
{
    LanguageGen(PackOutput output)
    {
        super(output, LTXIndustries.RESOURCES);
    }

    @Override
    protected void addTranslations()
    {
        // Attributes
        add(LTXIAttributes.SHIELD_CAPACITY.get().getDescriptionId(), "Passive Bubble Shield");

        //#region Blocks
        addBlock(TITANIUM_ORE, "Titanium Ore");
        addBlock(DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(NIOBIUM_ORE, "Niobium Ore");
        addBlock(RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(RAW_NIOBIUM_BLOCK, "Block of Raw Niobium");
        addBlock(RAW_TITANIUM_CLUSTER, "Raw Titanium Cluster");
        addBlock(RAW_NIOBIUM_CLUSTER, "Raw Niobium Cluster");
        addBlock(TITANIUM_BLOCK, "Block of Titanium");
        addBlock(NIOBIUM_BLOCK, "Block of Niobium");
        addBlock(SLATESTEEL_BLOCK, "Block of Slatesteel");

        NEON_LIGHTS.forEach((color, holder) ->
        {
            String name = color == NeonLightColor.LTX_LIME ? "LTX Lime" : localizeSimpleName(color.toString());
            name += " Neon Light";
            addBlock(holder, name);
        });
        addBlock(TITANIUM_PANEL, "Titanium Panel");
        addBlock(SMOOTH_TITANIUM_PANEL, "Smooth Titanium Panel");
        addBlock(TILED_TITANIUM_PANEL, "Tiled Titanium Panel");
        addBlock(TITANIUM_GLASS, "Titanium Glass");
        addBlock(SLATESTEEL_PANEL, "Slatesteel Panel");
        addBlock(SMOOTH_SLATESTEEL_PANEL, "Smooth Slatesteel Panel");
        addBlock(TILED_SLATESTEEL_PANEL, "Tiled Slatesteel Panel");

        addBlock(SPARK_FRUIT, "Spark Fruit");
        addBlock(BILEVINE, "Bilevine");
        addBlock(BILEVINE_PLANT, "Bilevine");
        addBlock(GLOOM_SHROOM, "Gloom Shroom");

        addBlock(ENERGY_CELL_ARRAY, "Energy Cell Array");
        addBlock(INFINITE_ENERGY_CELL_ARRAY, "Energy Cell Array (∞)");
        addBlock(DIGITAL_FURNACE, "Digital Furnace");
        addBlock(DIGITAL_SMOKER, "Digital Smoker");
        addBlock(DIGITAL_BLAST_FURNACE, "Digital Blast Furnace");
        addBlock(GRINDER, "Grinder");
        addBlock(MATERIAL_FUSING_CHAMBER, "Material Fusing Chamber");
        addBlock(ELECTROCENTRIFUGE, "ElectroCentrifuge");
        addBlock(MIXER, "Mixer");
        addBlock(VOLTAIC_INJECTOR, "Voltaic Injector");
        addBlock(CHEM_LAB, "Chem Lab");
        addBlock(ASSEMBLER, "Assembler");
        addBlock(GEO_SYNTHESIZER, "Geo Synthesizer");
        addBlock(FABRICATOR, "Fabricator");
        addBlock(AUTO_FABRICATOR, "Auto Fabricator");
        addBlock(EQUIPMENT_UPGRADE_STATION, "Equipment Upgrade Station");
        addBlock(MOLECULAR_RECONSTRUCTOR, "Molecular Reconstructor");
        addBlock(DIGITAL_GARDEN, "Bio/ARU Garden");

        addBlock(ROCKET_TURRET, italicName("%s A/DS Turret", "Atmos"));
        addBlock(RAILGUN_TURRET, italicName("%s A/DS Turret", "Noctis"));

        addBlock(GLOWSTICK, "Glowstick");
        //#endregion

        // Fluids
        fluidType(LTXIFluids.VIRIDIC_ACID_TYPE, "Viridic Acid");
        fluidType(LTXIFluids.HYDROGEN_TYPE, "Hydrogen");
        fluidType(LTXIFluids.OXYGEN_TYPE, "Oxygen");

        //#region Items
        addItem(RAW_TITANIUM, "Raw Titanium");
        addItem(TITANIUM_INGOT, "Titanium Ingot");
        addItem(TITANIUM_NUGGET, "Titanium Nugget");
        addItem(RAW_NIOBIUM, "Raw Niobium");
        addItem(NIOBIUM_INGOT, "Niobium Ingot");
        addItem(NIOBIUM_NUGGET, "Niobium Nugget");
        addItem(VITRIOL_BERRIES, "Vitriol Berries");
        addItem(VIRIDIC_ACID_BUCKET, "Viridic Acid Bucket");
        addItem(HYDROGEN_BUCKET, "Hydrogen Bucket");
        addItem(OXYGEN_BUCKET, "Oxygen Bucket");

        addItem(LTX_LIME_PIGMENT, "LTX Lime Pigment");
        addItem(ENERGY_BLUE_PIGMENT, "Energy Blue Pigment");
        addItem(ELECTRIC_CHARTREUSE_PIGMENT, "Electric Chartreuse Pigment");
        addItem(VIRIDIC_GREEN_PIGMENT, "Viridic Green Pigment");
        addItem(NEURO_BLUE_PIGMENT, "Neuro Blue Pigment");

        addItem(CARBON_DUST, "Carbon Dust");
        addItem(RESINOUS_BIOMASS, "Resinous Biomass");
        addItem(ACIDIC_BIOMASS, "Acidic Biomass");
        addItem(DEEPSLATE_DUST, "Deepslate Dust");
        addItem(ELECTRIC_CHEMICAL, "Electrolyte Blend");
        addItem(MONOMER_CHEMICAL, "Monomer Solution");
        addItem(VIRIDIC_WEAPON_CHEMICAL, "Weapons-Grade Viridic Acid");
        addItem(CHORUS_CHEMICAL, "Chorus Extract");
        addItem(SCULK_CHEMICAL, "Echo Serum");
        addItem(NEURO_CHEMICAL, "Neuro-Suppressant");
        addItem(SLATESTEEL_INGOT, "Slatesteel Ingot");
        addItem(SLATESTEEL_NUGGET, "Slatesteel Nugget");
        addItem(POLYMER_INGOT, "Polymer Bar");
        addItem(TITANIUM_GEAR, "Titanium Gear");
        addItem(SLATESTEEL_GEAR, "Slatesteel Gear");
        addItem(CIRCUIT_BOARD, "Circuit Board");
        simpleHintItem(T1_CIRCUIT, "Basic Circuit", "First tier circuit. Sufficient energy and data throughput for most entry-level applications.");
        simpleHintItem(T2_CIRCUIT, "Enhanced Circuit", "Second tier circuit. Improved energy and data efficiency from higher quality conductors.");
        simpleHintItem(T3_CIRCUIT, "Precision Circuit", "Third tier circuit. Highest performance on conventional materials. Handles all standard engineering and industrial applications.");
        simpleHintItem(T4_CIRCUIT, "Nano-Core Processor", "Fourth tier circuit. Exotic crystal-metal nanoarchitecture packed into a small form factor. Near unmatched power delivery and data processing.");
        simpleHintItem(T5_CIRCUIT, "Real Virtuality Processor", "Fifth and final tier circuit. Can create digital microverses where its calculations don't break the laws of physics. Don't question it.");

        addItem(COAL_ORE_PEBBLES, "Coal Ore Pebbles");
        addItem(COPPER_ORE_PEBBLES, "Copper Ore Pebbles");
        addItem(IRON_ORE_PEBBLES, "Iron Ore Pebbles");
        addItem(LAPIS_ORE_PEBBLES, "Lapis Lazuli Ore Pebbles");
        addItem(REDSTONE_ORE_PEBBLES, "Redstone Ore Pebbles");
        addItem(GOLD_ORE_PEBBLES, "Gold Ore Pebbles");
        addItem(DIAMOND_ORE_PEBBLES, "Diamond Ore Pebbles");
        addItem(EMERALD_ORE_PEBBLES, "Emerald Ore Pebbles");
        addItem(QUARTZ_ORE_PEBBLES, "Nether Quartz Ore Pebbles");
        addItem(NETHERITE_ORE_PEBBLES, "Netherite Scrap Ore Pebbles");
        addItem(TITANIUM_ORE_PEBBLES, "Titanium Ore Pebbles");
        addItem(NIOBIUM_ORE_PEBBLES, "Niobium Ore Pebbles");
        addItem(TIN_ORE_PEBBLES, "Tin Ore Pebbles");
        addItem(OSMIUM_ORE_PEBBLES, "Osmium Ore Pebbles");
        addItem(NICKEL_ORE_PEBBLES, "Nickel Ore Pebbles");
        addItem(LEAD_ORE_PEBBLES, "Lead Ore Pebbles");
        addItem(SILVER_ORE_PEBBLES, "Silver Ore Pebbles");
        addItem(URANIUM_ORE_PEBBLES, "Uranium Ore Pebbles");
        addItem(LTX_DRILL, "ε-Series Drill");
        addItem(LTX_SWORD, "ε-Series Sword");
        addItem(LTX_SHOVEL, "ε-Series Shovel");
        addItem(LTX_AXE, "ε-Series Axe");
        addItem(LTX_HOE, "ε-Series Hoe");
        addItem(LTX_SHEARS, "ε-Series Shears");
        addItem(LTX_BRUSH, "ε-Series Brush");
        addItem(LTX_FISHING_ROD, "ε-Series Fishing Rod");
        addItem(LTX_LIGHTER, "ε-Series Lighter");
        addItem(LTX_WRENCH, "ε-Series Wrench");
        addItem(WONDERLAND_HEAD, italicName("%s AL/1C Seg.H", "Wonderland"));
        addItem(WONDERLAND_BODY, italicName("%s AL/1C Seg.B", "Wonderland"));
        addItem(WONDERLAND_LEGS, italicName("%s AL/1C Seg.L", "Wonderland"));
        addItem(WONDERLAND_FEET, italicName("%s AL/1C Seg.F", "Wonderland"));

        addItem(EMPTY_UPGRADE_MODULE, "Empty Upgrade Module");
        simpleHintItem(EMPTY_FABRICATION_BLUEPRINT, "Empty Fabrication Blueprint", "Encode a Fabrication recipe in a Fabricator.");
        addItem(FABRICATION_BLUEPRINT, "Fabrication Blueprint");
        addItem(ITEMS_IO_CONFIG_CARD, "Items IO Config Card");
        addItem(ENERGY_IO_CONFIG_CARD, "Energy IO Config Card");
        addItem(FLUIDS_IO_CONFIG_CARD, "Fluids IO Config Card");

        simpleHintItem(EXPLOSIVES_WEAPON_TECH_SALVAGE, "Salvaged Tech: Explosive Weapon Systems", "Broken components from an explosives handling device. Might be useful in reconstructing explosive weaponry.");
        simpleHintItem(TARGETING_TECH_SALVAGE, "Salvaged Tech: Auto-Targeting Systems", "Broken electronics from a targeting computer. Might be useful in reconstructing guidance systems for weaponry.");

        addItem(SUBMACHINE_GUN, italicName("%s 07/SD", "Serenity"));
        addItem(GLOWSTICK_LAUNCHER, italicName("%s 05/ID", "Wayfinder"));
        addItem(SHOTGUN, italicName("%s 21/SG", "Aurora"));
        addItem(GRENADE_LAUNCHER, italicName("%s 33/GL", "Hanabi"));
        addItem(LINEAR_FUSION_RIFLE, italicName("%s 38/LF", "Stargazer"));
        addItem(ROCKET_LAUNCHER, italicName("%s 42/RL", "Daybreak"));
        addItem(HEAVY_PISTOL, italicName("%s 77/HX", "Nova"));

        simpleHintItem(LIGHTWEIGHT_WEAPON_ENERGY, "Lightweight Weapon Energy", "Consumable energy cell for powering Lightweight-class weaponry.");
        simpleHintItem(SPECIALIST_WEAPON_ENERGY, "Specialist Weapon Energy", "Consumable energy cell for powering Specialist-class weaponry.");
        simpleHintItem(EXPLOSIVES_WEAPON_ENERGY, "Explosive Weapon Energy", "Consumable energy cell for powering Explosive-class weaponry.");
        simpleHintItem(HEAVY_WEAPON_ENERGY, "Heavy Weapon Energy", "Consumable energy cell for powering Heavy-class weaponry.");
        //#endregion

        //#region Equipment upgrades
        add(TOOL_DEFAULT_UPGRADE_TITLE, "ε Core Systems");
        upgradeDescOnly(LTXIEquipmentUpgrades.LTX_SHOVEL_DEFAULT, "Standard issue operating system. Tool energy cutter preserves topographical integrity.");
        upgradeDescOnly(LTXIEquipmentUpgrades.LTX_WRENCH_DEFAULT, "Standard issue operating system. Enables lossless dismantling and retrieval of machines.");
        upgradeDescOnly(LTXIEquipmentUpgrades.LTX_MELEE_DEFAULT, "Standard issue operating system. Tool energy blade is optimized for efficient severing and disintegration of organic matter.");
        upgradeTooltip(LTXIEquipmentUpgrades.LTX_MELEE_DEFAULT, 0, "%s bonus damage against unarmored targets");
        upgrade(LTXIEquipmentUpgrades.GLOWSTICK_LAUNCHER_DEFAULT, "Wayfinder Intrinsics", "Wayfinder's energy feed system ships pre-configured to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.SUBMACHINE_GUN_DEFAULT, "Serenity Intrinsics", "Serenity's small lightfrags zip right through targets without a trace.");
        upgradeTooltip(LTXIEquipmentUpgrades.SUBMACHINE_GUN_DEFAULT, 0, "No anger and knockback on damage");
        upgrade(LTXIEquipmentUpgrades.SHOTGUN_DEFAULT, "Aurora Intrinsics", "Aurora's combat precepts, specialized in fast assault and scout operations.");
        upgrade(LTXIEquipmentUpgrades.LFR_DEFAULT, "Stargazer Intrinsics", "Stargazer's systems are calibrated for precise long-range engagements.");
        upgradeTooltip(LTXIEquipmentUpgrades.LFR_DEFAULT, 0, "%s bonus damage when at least 40m away from target");
        upgradeTooltip(LTXIEquipmentUpgrades.LFR_DEFAULT, 1, "%s bonus damage when standing still and sneaking");
        upgrade(LTXIEquipmentUpgrades.HEAVY_PISTOL_DEFAULT, "Nova Intrinsics", "Nova's lightfrags can knock away even the heaviest targets.");
        upgrade(LTXIEquipmentUpgrades.HEAD_DEFAULT, "AL/1C [H] Unit", "Neural processor core of the AL/1C bodysuit.");
        upgradeTooltip(LTXIEquipmentUpgrades.HEAD_DEFAULT, 0, "Blocks vision debuff effects (%s)");
        upgrade(LTXIEquipmentUpgrades.BODY_DEFAULT, "AL/1C [B] Unit", "Vital function management system of the AL/1C bodysuit.");
        upgrade(LTXIEquipmentUpgrades.LEGS_DEFAULT, "AL/1C [L] Unit", "Primary locomotion systems of the AL/1C bodysuit.");
        upgradeTooltip(LTXIEquipmentUpgrades.LEGS_DEFAULT, 0, "Blocks movement debuff effects (%s)");
        upgrade(LTXIEquipmentUpgrades.FEET_DEFAULT, "AL/1C [F] Unit", "Bipedal stabilization systems of the AL/1C bodysuit.");
        upgradeTooltip(LTXIEquipmentUpgrades.FEET_DEFAULT, 0, "Immune to hot floor damage.");

        upgrade(LTXIEquipmentUpgrades.EQUIPMENT_ENERGY_UPGRADE, "Augmented Equipment Battery", "Maximize your equipment's field uptime with this upgraded battery.");

        upgrade(LTXIEquipmentUpgrades.EPSILON_FISHING_LURE, "ε Anglers' Kit", "Upgraded lure for the ε-Series fishing rod. Attracts more valuable aquatic specimens faster than regular bait.");
        upgrade(LTXIEquipmentUpgrades.TOOL_NETHERITE_LEVEL, "Netherite-Core Tool Head", "Upgraded tool cutters can harvest Netherite-level materials.");
        upgrade(LTXIEquipmentUpgrades.EPSILON_OMNI_DRILL, "ε Cutter Rev.000", "Restored prototype ε mining tech with bypassed safeties. Makes modular mining tools effective against all material types.");
        upgrade(LTXIEquipmentUpgrades.TREE_VEIN_MINE, "Lumber Felling Unit", "Automatic harvesting algorithm for the ε-Series Axe's energy cutter. May cause localized deforestation.");
        upgradeTooltip(LTXIEquipmentUpgrades.TREE_VEIN_MINE, 0, "Vein-mines trees, up to 256 logs");
        upgrade(LTXIEquipmentUpgrades.TOOL_VIBRATION_CANCEL, "Resonance-Tuned Servos", "Special lining on this tool's servos dampen vibrations from standard use.");
        upgrade(LTXIEquipmentUpgrades.TOOL_DIRECT_DROPS, "Mining Subspace Link", "Tool systems interface directly with your inventory, depositing materials without physical collection.");

        upgrade(LTXIEquipmentUpgrades.WEAPON_VIBRATION_CANCEL, "Echo Suppressor", "Augments weapons and projectiles with an anti-resonance field, erasing vibration signatures");
        upgrade(LTXIEquipmentUpgrades.WEAPON_DIRECT_DROPS, "Combat Subspace Link", "Weapon systems interface directly with your inventory, depositing loot without physical collection.");
        upgrade(LTXIEquipmentUpgrades.LIGHTWEIGHT_ENERGY_ADAPTER, "Lightweight Energy Adapter", "Reroutes the energy feed system of Lightweight weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.SPECIALIST_ENERGY_ADAPTER, "Specialist Energy Adapter", "Reroutes the energy feed system of Specialist weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.EXPLOSIVES_ENERGY_ADAPTER, "Explosives Energy Adapter", "Reroutes the energy feed system of Explosives weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.HEAVY_ENERGY_ADAPTER, "Heavy Energy Adapter", "Reroutes the energy feed system of Heavy weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.UNIVERSAL_INFINITE_AMMO, "//ERR~MAG-Z!!-NE//∞", "Ignore the laws of physics with this never-ending ammo source. Try not to cause a mass extinction event.");
        upgrade(LTXIEquipmentUpgrades.NEUTRAL_ENEMY_TARGET_FILTER, "Engagement Protocol: Preemption", "Weapon systems restrict engagement to neutral enemies and hostile targets.");
        upgrade(LTXIEquipmentUpgrades.HOSTILE_TARGET_FILTER, "Engagement Protocol: Rectification", "Weapon systems restrict engagement to actively hostile targets.");
        upgrade(LTXIEquipmentUpgrades.HEAVY_PISTOL_GOD_ROUNDS, "Stellar Reality Disruptor", "Rip through reality itself with this Nova upgrade. Ensures swift defeat of even the strongest enemies.");
        upgrade(LTXIEquipmentUpgrades.UNIVERSAL_STEALTH_DAMAGE, "Biometric Obfuscation", "Traceable identity masking tech derived from ephemeral materials. May not confuse a more discerning target.");
        upgradeTooltip(LTXIEquipmentUpgrades.UNIVERSAL_STEALTH_DAMAGE, 0, "No anger on damage");
        upgrade(LTXIEquipmentUpgrades.GRENADE_LAUNCHER_PROJECTILE_SPEED, "Hanabi Launch Boost", "Increases the velocity of the Hanabi grenades.");

        upgrade(LTXIEquipmentUpgrades.PASSIVE_NIGHT_VISION, "NV Visor", "AL/1C optics modified to work in low light environments.");
        upgrade(LTXIEquipmentUpgrades.ARMOR_PASSIVE_SHIELD, "Bubble Projection Unit", "Upgrades AL/1C with auto-recharging Bubble Shield engines. Can be installed on all pieces.");
        upgrade(LTXIEquipmentUpgrades.ARMOR_DEFENSE, "Active Protection Unit", "Upgrades AL/1C's shell with an energy-powered impact and trauma attenuation system.");
        upgradeTooltip(LTXIEquipmentUpgrades.ARMOR_DEFENSE, 0, "%s non-void damage reduction (%s)");
        upgrade(LTXIEquipmentUpgrades.BREATHING_UNIT, "Atmospheric Regulator", "Allows indefinite operation underwater for the user.");
        upgrade(LTXIEquipmentUpgrades.PASSIVE_SATURATION, "Metabolic Synthesizer", "Injects a stable mix of nutrients required for healthy function.");
        upgrade(LTXIEquipmentUpgrades.CREATIVE_FLIGHT, "Fantasia Rulebook", italicName("One of the %s project's most successful experiments. Allows the user to exist outside the laws of gravity.", "Wonderland"));

        upgrade(LTXIEquipmentUpgrades.EFFICIENCY_ENCHANTMENT, "Overclocked Energy Cutters", "Enhances the power feed to the tool's energy cutter.");
        upgrade(LTXIEquipmentUpgrades.SILK_TOUCH_ENCHANTMENT, "Stabilized Harvest Matrix", "Calibrated to extract intact samples from the terrain.");
        upgrade(LTXIEquipmentUpgrades.FORTUNE_ENCHANTMENT, "Overclocked Harvest Matrix", "Calibrated to extract superior quantities of valuable resources.");
        upgrade(LTXIEquipmentUpgrades.LOOTING_ENCHANTMENT, "Combat Yield Protocol", "Calibrated to maximize structural integrity of salvageable biomaterials.");
        upgrade(LTXIEquipmentUpgrades.AMMO_SCAVENGER_ENCHANTMENT, "Munition Trace Unit", "Improves detection of high-grade LTX ammunition in the field.");
        upgrade(LTXIEquipmentUpgrades.RAZOR_ENCHANTMENT, "Severance Algorithm", "Weapon calibration enables the retrieval of anatomical curiosities.");

        upgrade(LTXIEquipmentUpgrades.FLAME_GRENADE_CORE, "Hanabi Core/Flame", "Grenades are loaded with a concentrated fuel that creates powerful flames.");
        upgrade(LTXIEquipmentUpgrades.CRYO_GRENADE_CORE, "Hanabi Core/Cryo", "Grenades contain a cryogenic compound that freezes a large area.");
        upgrade(LTXIEquipmentUpgrades.ELECTRIC_GRENADE_CORE, "Hanabi Core/Electric", "Grenades create a burst of electrical energy. Recommended for use in humid/aquatic environments.");
        upgrade(LTXIEquipmentUpgrades.ACID_GRENADE_CORE, "Hanabi Core/Acid", "Grenades contain a highly corrosive acid that reduces target armor strength.");
        upgrade(LTXIEquipmentUpgrades.NEURO_GRENADE_CORE, "Hanabi Core/Neuro", "Grenades contain a powerful neuro-suppressant agent that highly reduces target attack strength.");
        upgrade(LTXIEquipmentUpgrades.OMNI_GRENADE_CORE, "Hanabi Core/ARCOIRIS", "Full spectrum adaptable core for the Hanabi. Allows the use of any of grenade shells.");
        //#endregion

        //#region Machine upgrades
        upgrade(LTXIMachineUpgrades.ECA_CAPACITY_UPGRADE, "Auxiliary Energy Cells", "Increases the energy capacity and transfer rate of the Energy Cell Array.");
        upgrade(LTXIMachineUpgrades.STANDARD_MACHINE_SYSTEMS, "GPM Standard Systems", "Core modular systems designed for balanced efficiency.");
        upgrade(LTXIMachineUpgrades.ULTIMATE_MACHINE_SYSTEMS, "GPM Ultimate Systems", "The pinnacle of engineering precision! Achieves near-instantaneous crafting at the cost of immense energy consumption.");
        upgrade(LTXIMachineUpgrades.GPM_PARALLEL, "GPM Multi-Threading", "Parallelized task framework that completes multiple operations per cycle, as input and output constraints permit.");
        upgrade(LTXIMachineUpgrades.FABRICATOR_UPGRADE, "Enhanced Tool Head", "Elevate your Fabricator's manufacturing capabilities with superior internal components.");
        upgrade(LTXIMachineUpgrades.GEO_SYNTHESIZER_PARALLEL, "Lithic Co-Processor", "A specialized parallel processor for producing geological material.");
        upgrade(LTXIMachineUpgrades.TURRET_LOOTING, "Efficient Target Disposal", "Smarter turret targeting systems allow for increased loot drops from eliminated targets.");
        upgrade(LTXIMachineUpgrades.TURRET_RAZOR, "Headhunter Scope", "Precise turret calibration enables the collection of anatomical curiosities.");
        upgrade(LTXIMachineUpgrades.TURRET_LOOT_COLLECTOR, "Matter SubLink", "Loot is sent directly to the turret’s storage. If full, items appear at the turret’s base.");
        //#endregion

        // Creative tabs
        creativeTab(MAIN_TAB, "LTX Industries");
        creativeTab(EQUIPMENT_MODULES_TAB, "LTXI Equipment Upgrades");
        creativeTab(MACHINE_MODULES_TAB, "LTXI Machine Upgrades");

        // Menu titles
        menuTitle(LTXIMenus.BLOCK_IO_CONFIGURATION, "%s IO Config");
        menuTitle(LTXIMenus.MACHINE_UPGRADES, "Machine Upgrades");
        menuTitle(LTXIMenus.ROCKET_TURRET, "Atmos Turret");
        menuTitle(LTXIMenus.RAILGUN_TURRET, "Noctis Turret");

        // Machine input types
        add(BlockEntityInputType.ITEMS, "Items");
        add(BlockEntityInputType.ENERGY, "Energy");
        add(BlockEntityInputType.FLUIDS, "Fluids");
        add(BlockEntityInputType.SIDEBAR_TOOLTIP, "Configure %s IO");

        // Recipe types
        add(GRINDING, "Grinding");
        add(MATERIAL_FUSING, "Material Fusing");
        add(ELECTRO_CENTRIFUGING, "ElectroCentrifuging");
        add(MIXING, "Mixing");
        add(ENERGIZING, "Energizing");
        add(CHEMICAL_REACTING, "Chemical Reacting");
        add(GARDEN_SIMULATING, "Garden Simulating");
        add(ASSEMBLING, "Assembling");
        add(GEO_SYNTHESIS, "Geo Synthesis");
        add(FABRICATING, "Fabricating");

        // Recipe sub-modes
        recipeMode(LTXIRecipeModes.DYE_EXTRACTION, "Dye Extraction");
        recipeMode(LTXIRecipeModes.CHEM_DISSOLUTION, "Dissolution");
        recipeMode(LTXIRecipeModes.ECF_ELECTROLYZE, "Electrolyze");
        recipeMode(LTXIRecipeModes.GS_FARMING, "Farming");
        recipeMode(LTXIRecipeModes.GS_WOODS, "Woods");
        recipeMode(LTXIRecipeModes.GS_ORCHARD, "Orchard");
        recipeMode(LTXIRecipeModes.GS_FOLIAGE, "Foliage");

        // Entity type names
        addEntityType(LTXIEntities.GLOWSTICK_PROJECTILE, "Wayfinder Glowstick");
        addEntityType(LTXIEntities.ORB_GRENADE, "Orb Grenade");
        addEntityType(LTXIEntities.DAYBREAK_ROCKET, "Daybreak Rocket");
        addEntityType(LTXIEntities.TURRET_ROCKET, "Turret Rocket");
        addEntityType(LTXIEntities.STICKY_FLAME, "Sticky Flame");

        // Mob effects
        addEffect(LTXIMobEffects.FROSTBITE, "Frostbite");
        addEffect(LTXIMobEffects.CORROSIVE, "Corroding");
        addEffect(LTXIMobEffects.NEURO_SUPPRESSED, "Neuro-Suppressed");

        // Enchantments
        enchantment(LTXIEnchantments.RAZOR, "Razor");
        enchantment(LTXIEnchantments.AMMO_SCAVENGER, "Ammo Scavenger");

        //#region Tooltips
        add(INLINE_ENERGY, "Energy: %s");
        add(INLINE_ENERGY_TRANSFER_RATE, "Energy I/O: %s");
        add(INLINE_ENERGY_USAGE, "Energy use: %s");
        add(INLINE_NO_OWNER_TOOLTIP, "No Owner");
        add(INLINE_OWNER_TOOLTIP, "Owner: %s");
        add(ENERGY_OVERCHARGE_TOOLTIP, "Energy Overcharged! Your energy stored is more than your current capacity.");
        add(ENERGY_ACTIONS_TOOLTIP, "%s energy actions");

        add(NONE_UNIVERSAL_TOOLTIP, "None");
        add(BACK_BUTTON_LABEL, "Back");
        add(AUTO_OUTPUT_OFF_TOOLTIP, "Auto Output Disabled");
        add(AUTO_OUTPUT_ON_TOOLTIP, "Auto Output Enabled");
        add(AUTO_INPUT_OFF_TOOLTIP, "Auto Input Disabled");
        add(AUTO_INPUT_ON_TOOLTIP, "Auto Input Enabled");
        add(MACHINE_UPGRADES_SIDEBAR_TOOLTIP, "Manage Upgrade Modules");
        add(RECIPE_MODES_TITLE_OR_TOOLTIP, "Recipe Modes");
        add(RECIPE_MODE_CURRENT_MODE, "Current mode: %s");
        add(JEI_RECIPE_MODE_NEEDED, "Needs mode: %s");
        add(JEI_NO_RECIPE_MODE_NEEDED, "No recipe mode needed.");

        add(MACHINE_TICKS_PER_OP_TOOLTIP, "Ticks per operation: %s");
        add(EMPTY_ITEM_INVENTORY_TOOLTIP, "No items stored");
        add(ITEM_INVENTORY_TOOLTIP, "Stored Items");
        add(FABRICATOR_SELECTED_RECIPE_TOOLTIP, "Left click to craft. Right click to encode blueprint (must have blank blueprint in slot).");
        add(INLINE_ENERGY_REQUIRED_TOOLTIP, "Energy required: %s");
        add(WORKING_PROGRESS_TOOLTIP, "Working: %s%%");
        add(CRAFTING_PROGRESS_TOOLTIP, "Crafting: %s%%");
        add(JEI_CRAFTING_TIME_TOOLTIP, "%ss | %st");

        add(INPUT_NOT_CONSUMED_TOOLTIP, "Input not consumed");
        add(INPUT_CONSUME_CHANCE_TOOLTIP, "Input consume chance: %s");
        add(OUTPUT_CHANCE_TOOLTIP, "Output chance: ");
        add(OUTPUT_VARIABLE_COUNT_TOOLTIP, "Outputs between: ");
        add(OUTPUT_NON_PRIMARY_TOOLTIP, "Non-primary outputs are discarded when space is limited.");

        add(UPGRADE_RANK_TOOLTIP, "Rank %s/%s");
        add(UPGRADE_REMOVE_HINT, "Shift + left click to remove upgrade. Must have space in your inventory.");
        add(UPGRADE_COMPATIBILITY_TOOLTIP, "Compatible with:");
        add(EQUIPMENT_UPGRADE_MODULE_TOOLTIP, "Equipment Upgrade Module");
        add(MACHINE_UPGRADE_MODULE_TOOLTIP, "Machine Upgrade Module");
        add(DAMAGE_ATTRIBUTES_EFFECT_PREFIX, "Against target: ");

        add(UPGRADE_INSTALL_SUCCESS, "Installed upgrade.");
        add(UPGRADE_INSTALL_FAIL, "Can't install upgrade.");
        add(IO_CARD_CLEARED, "IO config cleared.");
        add(IO_CARD_COPIED, "Machine IO config copied.");
        add(IO_CARD_PASTED, "Applied IO config to machine.");
        add(IO_CARD_SAME_CONFIG, "IO config already applied.");
        add(IO_CARD_INVALID_SETUP, "Machine does not support this IO config.");
        add(IO_CARD_INVALID_TYPE, "Machine does not support %s.");
        add(SHIELD_COMMAND_MSG, "Modified %s entity shields");

        add(SHIFT_HOVER_HINT, "Hold SHIFT for extra info");
        add(INVALID_UPGRADE_HINT, "The upgrade in this module is invalid or corrupted. Shift+Right Click to clear.");
        add(INVALID_BLUEPRINT_HINT, "The recipe in this blueprint is invalid or corrupted. Shift+Right Click to clear.");
        add(EMPTY_IO_CARD_HINT, "No IO configuration data stored. Shift+Right Click a machine to copy.");
        add(ENCODED_IO_CARD_HINT, "Shift+Right Click on a compatible machine to paste settings, or in the air to clear.");

        add(ENERGY_CAPACITY_UPGRADE, "%s energy capacity");
        add(ENERGY_TRANSFER_UPGRADE, "%s energy transfer rate");
        add(ENERGY_USAGE_UPGRADE, "%s energy usage");
        add(PARALLEL_OPERATIONS_UPGRADE, "%s operations per cycle");
        add(MACHINE_SPEED_UPGRADE, "%s machine speed");
        add(ENERGY_PER_RECIPE_UPGRADE, "Avg. energy per recipe: %s");
        add(INSTANT_PROCESSING_UPGRADE, "Instant machine operation");
        add(PROJECTILE_SPEED_UPGRADE, "%s projectile speed");
        add(ATTRIBUTE_SCALED_DAMAGE_UPGRADE, "%s of target's %s as extra damage");

        add(MINIMUM_MACHINE_SPEED_EFFECT, "Minimum speed: %s ticks");
        add(SUPPRESS_VIBRATIONS_EFFECT, "Suppresses %s sculk vibrations");
        add(CAPTURE_BLOCK_DROPS_EFFECT, "Captures %s block drops to your inventory");
        add(CAPTURE_MOB_DROPS_EFFECT, "Captures mob drops to your inventory");
        add(REDUCTION_MODIFIER_EFFECT, "%s %s breach");
        add(ENCHANTMENT_UPGRADE_EFFECT, "+%s %s levels");
        add(CAPPED_ENCHANTMENT_UPGRADE_EFFECT, "+%s %s levels (max %s)");
        add(GRENADE_UNLOCK_EFFECT, "Can use %s shells");
        add(CANCEL_FALLS_EFFECT, "Cancels falls (%s)");

        add(WeaponReloadSource.Type.ITEM.getItemTooltip(), "Reloads with %s");
        add(WeaponReloadSource.Type.ITEM.getUpgradeTooltip(), "Replaces reload item: %s");
        add(WeaponReloadSource.Type.COMMON_ENERGY.getItemTooltip(), "Reloads from Common Energy battery");
        add(WeaponReloadSource.Type.COMMON_ENERGY.getUpgradeTooltip(), "Weapon reloads from an internal CE battery");
        add(WeaponReloadSource.Type.INFINITE.getItemTooltip(), "This weapon has infinite ammo!");
        add(WeaponReloadSource.Type.INFINITE.getUpgradeTooltip(), "Grants weapon an infinite, never-draining magazine");

        add(WeaponItem.AMMO_LOADED_TOOLTIP, "Ammo: %s/%s");
        add(WeaponItem.RELOAD_SPEED_TOOLTIP, "Reload speed: %ss");
        add(GrenadeLauncherItem.GRENADE_TYPE_TOOLTIP, "%s shells equipped");

        add(LTXITooltipUtil.ALL_HOLDER_SET, "all");
        add(LTXITooltipUtil.AMBIGUOUS_HOLDER_SET, "certain");
        //#endregion

        // Sound subtitles
        soundEvent(UPGRADE_INSTALL, "Upgrade module installed");
        soundEvent(UPGRADE_REMOVE, "Upgrade module removed");
        soundEvent(EQUIPMENT_MODE_SWITCH, "Equipment mode switched");
        soundEvent(TURRET_TARGET_FOUND, "Turret finds targets");
        soundEvent(BUBBLE_SHIELD_BREAK, "Bubble shield breaks");
        soundEvent(GLOWSTICK_LAUNCHER_FIRE, "Wayfinder fires");
        soundEvent(SUBMACHINE_GUN_LOOP, "Serenity firing");
        soundEvent(SHOTGUN_FIRE, "Aurora fires");
        soundEvent(GRENADE_LAUNCHER_FIRE, "Hanabi fires");
        soundEvent(LINEAR_FUSION_CHARGE, "Stargazer charges");
        soundEvent(LINEAR_FUSION_FIRE, "Stargazer fires");
        soundEvent(ROCKET_LAUNCHER_FIRE, "Daybreak fires");
        soundEvent(HEAVY_PISTOL_FIRE, "Nova fires");
        soundEvent(ROCKET_EXPLODE, "Rocket explodes");
        GRENADE_EXPLOSIONS.forEach((element, holder) -> soundEvent(holder, localizeSimpleName(element) + " grenade explodes"));
        soundEvent(RAILGUN_BOOM, "Railgun booms");

        // Enum types
        addEnum(GrenadeType.class);
        addEnum(ToolSpeed.class);

        // Damage types
        add(INVALID_WEAPON_DEATH_MESSAGE, "%s was killed by an invalid LTX weapon");
        add(STRAY_PROJECTILE_DEATH_MESSAGE, "%s was killed by a stray %s");
        damageType(LTXIDamageTypes.LIGHTFRAG, "%2$s shot %1$s with %3$s");
        damageType(LTXIDamageTypes.EXPLOSIVE_GRENADE, "%s was blown away by %s's %s");
        damageType(LTXIDamageTypes.FLAME_GRENADE, "%s was incinerated by %s's %s");
        damageType(LTXIDamageTypes.CRYO_GRENADE, "%s was frozen solid by %s's %s");
        damageType(LTXIDamageTypes.ELECTRIC_GRENADE, "%s was electrocuted by %s's %s");
        damageType(LTXIDamageTypes.ACID_GRENADE, "%s was dissolved by %s's %s");
        damageType(LTXIDamageTypes.NEURO_GRENADE, "%s was decayed by %s's %s");
        damageType(LTXIDamageTypes.ROCKET_LAUNCHER, "%s was blown up by %s's %s");

        noItemCausingEntityOnlyDamageMessage(LTXIDamageTypes.STICKY_FLAME, "%s was cooked well-done by %s", "%s was cooked well-done");
        noItemCausingEntityOnlyDamageMessage(LTXIDamageTypes.TURRET_ROCKET, "%s was shot down by %s's Atmos turret", "%s was shot down by a rogue Atmos turret");
        noItemCausingEntityOnlyDamageMessage(LTXIDamageTypes.RAILGUN_TURRET, "%s was obliterated by %s's Noctis turret", "%s was obliterated by a rogue Noctis turret");

        //#region Advancements
        //#endregion

        // Named tags
        add(LTXITags.Items.WRENCH_BREAKABLE, "wrench-breakable");
        add(LTXITags.GameEvents.WEAPON_VIBRATIONS, "weaponry");
        add(LTXITags.GameEvents.HANDHELD_EQUIPMENT, "handheld tool");

        // GuideME compatibility
        add(modResources.translationKey("item.{}.guide_tablet"), "Guide Tablet");
        add(modResources.translationKey("hint.{}.guide_tablet"), "To be completed Soon™");

        // Key mapping
        add(LTXIKeyMappings.CATEGORY_LTXI, "LTX Industries");
        add(LTXIKeyMappings.RELOAD_KEY_SUBTITLE, "Reload Weapon");
    }

    private void simpleHintItem(DeferredItem<SimpleHintItem> item, String name, String hint)
    {
        addItem(item, name);
        add(item.get().getShiftHint(), hint);
    }

    private String italicName(String pattern, String name)
    {
        name = "§o" + name + "§r";
        return String.format(pattern, name);
    }

    private void noItemCausingEntityOnlyDamageMessage(ResourceKey<DamageType> damageTypeKey, String translation, String unownedTranslation)
    {
        damageTypeAndVariants(damageTypeKey, translation, collector -> collector.accept("unowned", unownedTranslation));
    }

    private void upgrade(ResourceKey<? extends UpgradeBase<?, ?>> key, String title, String description)
    {
        add(UpgradeBaseBuilder.defaultTitleKey(key), title);
        add(UpgradeBaseBuilder.defaultDescriptionKey(key), description);
    }

    private void upgradeDescOnly(ResourceKey<? extends UpgradeBase<?, ?>> key, String description)
    {
        add(UpgradeBaseBuilder.defaultDescriptionKey(key), description);
    }

    private void upgradeTooltip(ResourceKey<? extends UpgradeBase<?, ?>> key, int index, String value)
    {
        add(UpgradeBaseBuilder.tooltipKey(key, index), value);
    }

    private void recipeMode(ResourceKey<RecipeMode> key, String value)
    {
        add(ModResources.registryPrefixedIdLangKey(key), value);
    }
}