package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.ltxindustries.LTXITags;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.block.NeonLightColor;
import liedge.ltxindustries.blockentity.base.BlockEntityInputType;
import liedge.ltxindustries.client.LTXIKeyMappings;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.item.SimpleHintItem;
import liedge.ltxindustries.item.TooltipShiftHintItem;
import liedge.ltxindustries.item.weapon.GrenadeLauncherWeaponItem;
import liedge.ltxindustries.item.weapon.WeaponItem;
import liedge.ltxindustries.lib.upgrades.UpgradeBase;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseBuilder;
import liedge.ltxindustries.lib.weapons.GrenadeType;
import liedge.ltxindustries.lib.weapons.WeaponReloadSource;
import liedge.ltxindustries.registry.bootstrap.LTXIDamageTypes;
import liedge.ltxindustries.registry.bootstrap.LTXIEnchantments;
import liedge.ltxindustries.registry.bootstrap.LTXIEquipmentUpgrades;
import liedge.ltxindustries.registry.bootstrap.LTXIMachineUpgrades;
import liedge.ltxindustries.registry.game.*;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
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
        //#region Blocks
        addBlock(TITANIUM_ORE, "Titanium Ore");
        addBlock(DEEPSLATE_TITANIUM_ORE, "Deepslate Titanium Ore");
        addBlock(NIOBIUM_ORE, "Niobium Ore");
        addBlock(RAW_TITANIUM_BLOCK, "Block of Raw Titanium");
        addBlock(RAW_NIOBIUM_BLOCK, "Block of Raw Niobium");
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
        addBlock(TITANIUM_GLASS, "Titanium Glass");

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
        addBlock(FABRICATOR, "Fabricator");
        addBlock(AUTO_FABRICATOR, "Auto Fabricator");
        addBlock(EQUIPMENT_UPGRADE_STATION, "Equipment Upgrade Station");
        addBlock(MOLECULAR_RECONSTRUCTOR, "Molecular Reconstructor");

        addBlock(ROCKET_TURRET, italicName("%s A/DS Turret", "Atmos"));
        addBlock(RAILGUN_TURRET, italicName("%s A/DS Turret", "Noctis"));
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
        addItem(SCULK_CHEMICAL, "Echo Serum");
        addItem(NEURO_CHEMICAL, "Neuro-Suppressant");
        addItem(SLATESTEEL_INGOT, "Slatesteel Ingot");
        addItem(SLATESTEEL_NUGGET, "Slatesteel Nugget");
        addItem(POLYMER_INGOT, "Polymer Bar");
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
        addItem(LTX_DRILL, "ε-Series Drill");
        addItem(LTX_SWORD, "ε-Series Sword");
        addItem(LTX_SHOVEL, "ε-series Shovel");
        addItem(LTX_AXE, "ε-Series Axe");
        addItem(LTX_HOE, "ε-Series Hoe");
        addItem(LTX_SHEARS, "ε-Series Shears");
        addItem(LTX_BRUSH, "ε-Series Brush");
        addItem(LTX_FISHING_ROD, "ε-Series Fishing Rod");
        addItem(LTX_LIGHTER, "ε-Series Lighter");
        addItem(LTX_WRENCH, "ε-Series Wrench");

        addItem(EMPTY_UPGRADE_MODULE, "Empty Upgrade Module");
        simpleHintItem(EMPTY_FABRICATION_BLUEPRINT, "Empty Fabrication Blueprint", "Encode a Fabrication recipe in a Fabricator.");
        addItem(FABRICATION_BLUEPRINT, "Fabrication Blueprint");

        simpleHintItem(EXPLOSIVES_WEAPON_TECH_SALVAGE, "Salvaged Tech: Explosive Weapon Systems", "Broken components from an explosives handling device. Might be useful in reconstructing explosive weaponry.");
        simpleHintItem(TARGETING_TECH_SALVAGE, "Salvaged Tech: Auto-Targeting Systems", "Broken electronics from a targeting computer. Might be useful in reconstructing guidance systems for weaponry.");

        addItem(SUBMACHINE_GUN, italicName("%s 07/SD", "Serenity"));
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
        upgrade(LTXIEquipmentUpgrades.SUBMACHINE_GUN_DEFAULT, "Serenity Intrinsics", "Serenity's small light-frags zip right through targets without a trace.");
        upgrade(LTXIEquipmentUpgrades.SHOTGUN_DEFAULT, "Aurora Intrinsics", "Aurora's combat precepts, specialized in fast assault and scout operations.");

        upgrade(LTXIEquipmentUpgrades.EPSILON_FISHING_LURE, "ε Anglers' Kit", "ε series' signature lure attracts more valuable aquatic specimens faster than regular bait.");
        upgrade(LTXIEquipmentUpgrades.TOOL_NETHERITE_LEVEL, "Netherite-Core Tool Head", "Upgrades mining tier to Netherite level and increases mining speed.");
        upgrade(LTXIEquipmentUpgrades.EPSILON_OMNI_DRILL, "ε Experimental Drill", "Special-issue drill augur. Modified to work on any material type.");
        upgrade(LTXIEquipmentUpgrades.TOOL_VIBRATION_CANCEL, "Resonance-Tuned Servos", "Special lining on this tool's servos dampen vibrations from standard use.");
        upgrade(LTXIEquipmentUpgrades.TOOL_DIRECT_DROPS, "Mining Subspace Link", "Tool systems interface directly with your inventory, depositing materials without physical collection.");

        upgrade(LTXIEquipmentUpgrades.WEAPON_VIBRATION_CANCEL, "Echo Suppressor", "Augments weapons and projectiles with an anti-resonance field, erasing vibration signatures");
        upgrade(LTXIEquipmentUpgrades.WEAPON_DIRECT_DROPS, "Combat Subspace Link", "Weapon systems interface directly with your inventory, depositing loot without physical collection.");
        upgrade(LTXIEquipmentUpgrades.WEAPON_ARMOR_PIERCE, "Armor-Piercing Rounds", "Volatile energy dispersal precepts. Allows weapons to partially breach armor.");
        upgrade(LTXIEquipmentUpgrades.LIGHTWEIGHT_ENERGY_ADAPTER, "Lightweight Energy Adapter", "Reroutes the energy feed system of Lightweight weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.SPECIALIST_ENERGY_ADAPTER, "Specialist Energy Adapter", "Reroutes the energy feed system of Specialist weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.EXPLOSIVES_ENERGY_ADAPTER, "Explosives Energy Adapter", "Reroutes the energy feed system of Explosives weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.HEAVY_ENERGY_ADAPTER, "Heavy Energy Adapter", "Reroutes the energy feed system of Heavy weaponry to use Common Energy.");
        upgrade(LTXIEquipmentUpgrades.UNIVERSAL_INFINITE_AMMO, "//ERR~MAG-Z!!-NE//∞", "Ignore the laws of physics with this never-ending ammo source. Try not to cause a mass extinction event.");
        upgrade(LTXIEquipmentUpgrades.HIGH_IMPACT_ROUNDS, "High Impact Rounds", "Light-frags with a punch! Send targets flying back regardless of their knockback resistances.");
        upgrade(LTXIEquipmentUpgrades.HEAVY_PISTOL_GOD_ROUNDS, "Stellar Reality Disruptor", "Rip through reality itself with this Nova upgrade. Ensures swift defeat of even the strongest enemies.");

        upgrade(LTXIEquipmentUpgrades.UNIVERSAL_STEALTH_DAMAGE, "Biometric Obfuscation", "Targeting systems mask your signature, leaving no trace of your involvement. May not be effective against all targets.");
        upgrade(LTXIEquipmentUpgrades.WEAPON_SHIELD_REGEN, "Regenerative Link", "Captures enemy energy upon elimination to power medical nano-tech and the bubble shield.");

        upgrade(LTXIEquipmentUpgrades.EFFICIENCY_ENCHANTMENT, "Overclocked Energy Cutters", "Enhances the power feed to the tool's energy cutter.");
        upgrade(LTXIEquipmentUpgrades.SILK_TOUCH_ENCHANTMENT, "Stabilized Harvest Matrix", "Calibrated to extract intact samples from the terrain.");
        upgrade(LTXIEquipmentUpgrades.FORTUNE_ENCHANTMENT, "Overclocked Harvest Matrix", "Calibrated to extract superior quantities of valuable resources.");
        upgrade(LTXIEquipmentUpgrades.LOOTING_ENCHANTMENT, "Combat Yield Protocol", "Calibrated to maximize structural integrity of salvageable biomaterials.");
        upgrade(LTXIEquipmentUpgrades.AMMO_SCAVENGER_ENCHANTMENT, "Munition Trace Unit", "Improves detection of high-grade LTX ammunition in the field.");
        upgrade(LTXIEquipmentUpgrades.RAZOR_ENCHANTMENT, "Severance Algorithm", "Weapon calibration enables the retrieval of anatomical curiosities.");

        upgrade(LTXIEquipmentUpgrades.GRENADE_LAUNCHER_PROJECTILE_SPEED, "Hanabi Launch Boost", "Increases the velocity of the Hanabi grenades.");

        upgrade(LTXIEquipmentUpgrades.FLAME_GRENADE_CORE, "Hanabi Core/Flame", "Grenades are loaded with a concentrated fuel that creates powerful flames.");
        upgrade(LTXIEquipmentUpgrades.CRYO_GRENADE_CORE, "Hanabi Core/Cryo", "Grenades contain a cryogenic compound that freezes a large area.");
        upgrade(LTXIEquipmentUpgrades.ELECTRIC_GRENADE_CORE, "Hanabi Core/Electric", "Grenades create a burst of electrical energy. Recommended for use in humid/aquatic environments.");
        upgrade(LTXIEquipmentUpgrades.ACID_GRENADE_CORE, "Hanabi Core/Acid", "Grenades contain a highly corrosive acid that reduces target armor strength.");
        upgrade(LTXIEquipmentUpgrades.NEURO_GRENADE_CORE, "Hanabi Core/Neuro", "Grenades contain a powerful neuro-suppressant agent that highly reduces target attack strength.");
        upgrade(LTXIEquipmentUpgrades.OMNI_GRENADE_CORE, "Hanabi Core/ARCOIRIS", "Full spectrum adaptable core for the Hanabi. Allows the use of any of grenade shells.");
        //#endregion

        //#region Machine upgrades
        upgrade(LTXIMachineUpgrades.ECA_CAPACITY_UPGRADE, "Auxiliary Energy Cells", "Increases the energy capacity and transfer rate of the Energy Cell Array.");
        upgrade(LTXIMachineUpgrades.STANDARD_MACHINE_SYSTEMS, "Standard Machine Systems", "Core modular systems designed for balanced efficiency.");
        upgrade(LTXIMachineUpgrades.ULTIMATE_MACHINE_SYSTEMS, "Ultimate Machine Systems", "The pinnacle of engineering precision! Achieves near-instantaneous crafting at the cost of immense energy consumption.");
        upgrade(LTXIMachineUpgrades.FABRICATOR_UPGRADE, "Enhanced Tool Head", "Elevate your Fabricator's manufacturing capabilities with superior internal components.");
        upgrade(LTXIMachineUpgrades.TURRET_LOOTING, "Efficient Target Disposal", "Smarter turret targeting systems allow for increased loot drops from eliminated targets.");
        upgrade(LTXIMachineUpgrades.TURRET_RAZOR, "Headhunter Scope", "Precise turret calibration enables the collection of anatomical curiosities.");
        upgrade(LTXIMachineUpgrades.TURRET_LOOT_COLLECTOR, "Matter SubLink", "Loot is sent directly to the turret’s storage. If full, items appear at the turret’s base.");
        //#endregion

        // Creative tabs
        creativeTab(MAIN_TAB, "LTX Industries");
        creativeTab(EQUIPMENT_MODULES_TAB, "LTXI Equipment Upgrades");
        creativeTab(MACHINE_MODULES_TAB, "LTXI Machine Upgrades");

        // Menu titles
        menuTitle(LTXIMenus.MACHINE_UPGRADES, "Machine Upgrades");
        menuTitle(LTXIMenus.ROCKET_TURRET, "Atmos Turret");
        menuTitle(LTXIMenus.RAILGUN_TURRET, "Noctis Turret");

        // Machine input types
        add(BlockEntityInputType.ITEMS.getMenuTitle(), "Items IO Control");
        add(BlockEntityInputType.ITEMS.getSidebarTooltip(), "Configure Item IO");
        add(BlockEntityInputType.ENERGY.getMenuTitle(), "Energy IO Control");
        add(BlockEntityInputType.ENERGY.getSidebarTooltip(), "Configure Energy IO");
        add(BlockEntityInputType.FLUIDS.getMenuTitle(), "Fluids IO Control");
        add(BlockEntityInputType.FLUIDS.getSidebarTooltip(), "Configure Fluid IO");

        // Recipe types
        add(GRINDING, "Grinding");
        add(MATERIAL_FUSING, "Material Fusing");
        add(ELECTRO_CENTRIFUGING, "ElectroCentrifuging");
        add(MIXING, "Mixing");
        add(ENERGIZING, "Energizing");
        add(CHEMICAL_REACTING, "Chemical Reacting");
        add(FABRICATING, "Fabricating");

        // Entity type names
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

        add(BACK_BUTTON_LABEL, "Back");
        add(AUTO_OUTPUT_OFF_TOOLTIP, "Auto Output Disabled");
        add(AUTO_OUTPUT_ON_TOOLTIP, "Auto Output Enabled");
        add(AUTO_INPUT_OFF_TOOLTIP, "Auto Input Disabled");
        add(AUTO_INPUT_ON_TOOLTIP, "Auto Input Enabled");
        add(MACHINE_UPGRADES_SIDEBAR_TOOLTIP, "Manage Upgrade Modules");

        add(BLUEPRINT_TOAST_MESSAGE, "New Fabrication Data");
        add(MACHINE_TICKS_PER_OP_TOOLTIP, "Ticks per operation: %s");
        add(EMPTY_ITEM_INVENTORY_TOOLTIP, "No items stored");
        add(ITEM_INVENTORY_TOOLTIP, "Stored Items");
        add(FABRICATOR_SELECTED_RECIPE_TOOLTIP, "Left click to craft. Right click to encode blueprint (must have blank blueprint in slot).");
        add(INLINE_ENERGY_REQUIRED_TOOLTIP, "Energy required: %s");
        add(WORKING_PROGRESS_TOOLTIP, "Working: %s%%");
        add(CRAFTING_PROGRESS_TOOLTIP, "Crafting: %s%%");
        add(JEI_CRAFTING_TIME_TOOLTIP, "%ss | %st");

        add(OUTPUT_CHANCE_TOOLTIP, "Output chance: ");
        add(ADVANCEMENT_LOCKED_TOOLTIP, "Requires Advancement");

        add(UPGRADE_RANK_TOOLTIP, "Rank %s/%s");
        add(UPGRADE_REMOVE_HINT, "Shift + left click to remove upgrade. Must have space in your inventory.");
        add(UPGRADE_COMPATIBILITY_TOOLTIP, "Compatible with:");
        add(EQUIPMENT_UPGRADE_MODULE_TOOLTIP, "Equipment Upgrade Module");
        add(MACHINE_UPGRADE_MODULE_TOOLTIP, "Machine Upgrade Module");

        add(UPGRADE_INSTALL_SUCCESS, "Installed upgrade.");
        add(UPGRADE_INSTALL_FAIL, "Can't install upgrade.");
        add(INVALID_UPGRADE_HINT, "The upgrade in this module is invalid or corrupted. Shift+Right Click to clear.");
        add(INVALID_BLUEPRINT_HINT, "The recipe in this blueprint is invalid or corrupted. Shift+Right Click to clear.");

        add(ENERGY_CAPACITY_UPGRADE, "%s energy capacity");
        add(ENERGY_TRANSFER_UPGRADE, "%s energy transfer rate");
        add(ENERGY_USAGE_UPGRADE, "%s energy usage");
        add(MACHINE_SPEED_UPGRADE, "%s machine speed");
        add(ENERGY_PER_RECIPE_UPGRADE, "Avg. energy per recipe: %s");
        add(INSTANT_PROCESSING_UPGRADE, "Instant machine operation");
        add(PROJECTILE_SPEED_UPGRADE, "%s projectile speed");
        add(ATTRIBUTE_SCALED_DAMAGE_UPGRADE, "%s of target's %s as extra damage");

        add(MINIMUM_MACHINE_SPEED_EFFECT, "Minimum speed: %s ticks");
        add(MINING_EFFECTIVE_BLOCKS_EFFECT, "Effective against %s blocks");
        add(MINING_BASE_SPEED_EFFECT, "Base mining speed: %s");
        add(DYNAMIC_DAMAGE_TAG_EFFECT, "+Damage Tags: %s");
        add(SUPPRESS_VIBRATIONS_EFFECT, "Suppresses %s sculk vibrations");
        add(DIRECT_BLOCK_DROPS_EFFECT, "Directly collects %s block drops");
        add(DIRECT_ENTITY_DROPS_EFFECT, "Directly collects %s entity drops");
        add(REDUCTION_MODIFIER_EFFECT, "%s %s breach");
        add(BUBBLE_SHIELD_EFFECT, "%s Bubble Shield/kill, (max %s)");
        add(MOB_EFFECT_UPGRADE_EFFECT, "Applies %s (%s)");
        add(ENCHANTMENT_UPGRADE_EFFECT, "%s %s Enchantment");
        add(GRENADE_UNLOCK_EFFECT, "Can use %s shells");

        add(TooltipShiftHintItem.HINT_HOVER_TOOLTIP, "Hold SHIFT for extra info");
        add(WeaponReloadSource.Type.ITEM.getItemTooltip(), "Reloads with %s");
        add(WeaponReloadSource.Type.ITEM.getUpgradeTooltip(), "Replaces reload item: %s");
        add(WeaponReloadSource.Type.COMMON_ENERGY.getItemTooltip(), "Reloads from Common Energy battery");
        add(WeaponReloadSource.Type.COMMON_ENERGY.getUpgradeTooltip(), "Weapon reloads from an internal CE battery");
        add(WeaponReloadSource.Type.INFINITE.getItemTooltip(), "This weapon has infinite ammo!");
        add(WeaponReloadSource.Type.INFINITE.getUpgradeTooltip(), "Grants weapon an infinite, never-draining magazine");

        add(WeaponItem.AMMO_LOADED_TOOLTIP, "Ammo: %s/%s");
        add(GrenadeLauncherWeaponItem.GRENADE_TYPE_TOOLTIP, "%s shells equipped");

        add(LTXITooltipUtil.ALL_HOLDER_SET, "all");
        add(LTXITooltipUtil.AMBIGUOUS_HOLDER_SET, "certain");
        //#endregion

        // Sound subtitles
        soundEvent(UPGRADE_INSTALL, "Upgrade module installed");
        soundEvent(UPGRADE_REMOVE, "Upgrade module removed");
        soundEvent(WEAPON_MODE_SWITCH, "Weapon mode switched");
        soundEvent(TURRET_TARGET_FOUND, "Turret finds targets");
        soundEvent(SUBMACHINE_GUN_LOOP, "Submachine gun firing");
        soundEvent(SHOTGUN_FIRE, "Shotgun fires");
        soundEvent(GRENADE_LAUNCHER_FIRE, "Grenade launched");
        soundEvent(LINEAR_FUSION_CHARGE, "Linear fusion rifle charges");
        soundEvent(LINEAR_FUSION_FIRE, "Linear fusion rifle fires");
        soundEvent(ROCKET_LAUNCHER_FIRE, "Rocket launched");
        soundEvent(HEAVY_PISTOL_FIRE, "Heavy pistol fires");
        soundEvent(ROCKET_EXPLODE, "Rocket explodes");
        GRENADE_EXPLOSIONS.forEach((element, holder) -> soundEvent(holder, localizeSimpleName(element) + " grenade explodes"));
        soundEvent(RAILGUN_BOOM, "Railgun booms");

        // Orb grenade elements
        for (GrenadeType element : GrenadeType.values())
        {
            add(element, localizeSimpleName(element));
        }

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

        namedDamageTag(LTXITags.DamageTypes.WEAPON_DAMAGE, "Weapon Damage");
        namedDamageTag(LTXITags.DamageTypes.BYPASS_SURVIVAL_DEFENSES, "Bypass All Survival Defenses");
        namedDamageTag(DamageTypeTags.NO_ANGER, "No Anger");
        namedDamageTag(DamageTypeTags.NO_KNOCKBACK, "No Knockback");

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

    private void namedDamageTag(TagKey<DamageType> tagKey, String value)
    {
        add(LTXILangKeys.namedDamageTagKey(tagKey), value);
    }
}