package liedge.limatech.data.generation;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limacore.lib.ModResources;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechTags;
import liedge.limatech.blockentity.base.BlockEntityInputType;
import liedge.limatech.client.LimaTechKeyMappings;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.item.SimpleHintItem;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.CompoundValueOperation;
import liedge.limatech.lib.upgrades.UpgradeBase;
import liedge.limatech.lib.upgrades.UpgradeBaseBuilder;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.bootstrap.LimaTechDamageTypes;
import liedge.limatech.registry.bootstrap.LimaTechEnchantments;
import liedge.limatech.registry.bootstrap.LimaTechEquipmentUpgrades;
import liedge.limatech.registry.bootstrap.LimaTechMachineUpgrades;
import liedge.limatech.registry.game.LimaTechEntities;
import liedge.limatech.registry.game.LimaTechMenus;
import liedge.limatech.registry.game.LimaTechMobEffects;
import liedge.limatech.registry.game.LimaTechUpgradeEffectComponents;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredItem;

import static liedge.limatech.client.LimaTechLang.*;
import static liedge.limatech.registry.game.LimaTechBlocks.*;
import static liedge.limatech.registry.game.LimaTechCreativeTabs.*;
import static liedge.limatech.registry.game.LimaTechItems.*;
import static liedge.limatech.registry.game.LimaTechRecipeTypes.*;
import static liedge.limatech.registry.game.LimaTechSounds.*;

class LanguageGen extends LimaLanguageProvider
{
    LanguageGen(PackOutput output)
    {
        super(output, LimaTech.RESOURCES);
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
        addBlock(SLATE_ALLOY_BLOCK, "Block of Slate Alloy");

        GLOW_BLOCKS.forEach((color, deferredBlock) -> addBlock(deferredBlock, localizeSimpleName(color) + " Glow Block"));

        addBlock(ENERGY_STORAGE_ARRAY, "Energy Storage Array");
        addBlock(INFINITE_ENERGY_STORAGE_ARRAY, "Energy Storage Array (∞)");
        addBlock(DIGITAL_FURNACE, "Digital Furnace");
        addBlock(GRINDER, "Grinder");
        addBlock(RECOMPOSER, "Recomposer");
        addBlock(MATERIAL_FUSING_CHAMBER, "Material Fusing Chamber");
        addBlock(FABRICATOR, "Fabricator");
        addBlock(AUTO_FABRICATOR, "Auto Fabricator");
        addBlock(EQUIPMENT_UPGRADE_STATION, "Equipment Upgrade Station");

        addBlock(ROCKET_TURRET, ltxName("LTX A/DU %s", "Atmos"));
        addBlock(RAILGUN_TURRET, ltxName("LTX A/DU %s", "Noctis"));
        //#endregion

        //#region Items
        addItem(RAW_TITANIUM, "Raw Titanium");
        addItem(TITANIUM_INGOT, "Titanium Ingot");
        addItem(TITANIUM_NUGGET, "Titanium Nugget");
        addItem(RAW_NIOBIUM, "Raw Niobium");
        addItem(NIOBIUM_INGOT, "Niobium Ingot");
        addItem(NIOBIUM_NUGGET, "Niobium Nugget");

        addItem(WHITE_PIGMENT, "White Pigment");
        addItem(LIGHT_BLUE_PIGMENT, "Light Blue Pigment");
        addItem(LIME_PIGMENT, "Lime Pigment");

        addItem(DEEPSLATE_POWDER, "Deepslate Powder");
        addItem(SLATE_ALLOY_INGOT, "Slate Alloy Ingot");
        addItem(BEDROCK_ALLOY_INGOT, "Bedrock Alloy Ingot");
        addItem(SLATE_ALLOY_NUGGET, "Slate Alloy Nugget");
        addItem(COPPER_CIRCUIT, "Copper Circuit");
        addItem(GOLD_CIRCUIT, "Gold Circuit");
        addItem(NIOBIUM_CIRCUIT, "Niobium Circuit");

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

        addItem(LTX_DRILL, ltxToolName("Drill"));
        addItem(LTX_SWORD, ltxToolName("Sword"));
        addItem(LTX_SHOVEL, ltxToolName("Shovel"));
        addItem(LTX_AXE, ltxToolName("Axe"));
        addItem(LTX_HOE, ltxToolName("Hoe"));
        addItem(LTX_SHEARS, ltxToolName("Shears"));
        addItem(LTX_BRUSH, ltxToolName("Brush"));
        addItem(LTX_FISHING_ROD, ltxToolName("Fishing Rod"));
        addItem(LTX_LIGHTER, ltxToolName("Lighter"));
        addItem(LTX_WRENCH, ltxToolName("Wrench"));

        addItem(EMPTY_UPGRADE_MODULE, "Empty Upgrade Module");
        addItem(FABRICATION_BLUEPRINT, "Fabrication Blueprint");

        simpleHintItem(EXPLOSIVES_WEAPON_TECH_SALVAGE, "Salvaged Tech: Explosive Weapon Systems", "Broken components from an explosives handling device. Might be useful in reconstructing explosive weaponry.");
        simpleHintItem(TARGETING_TECH_SALVAGE, "Salvaged Tech: Auto-Targeting Systems", "Broken electronics from a targeting computer. Might be useful in reconstructing guidance systems for weaponry.");

        addItem(SUBMACHINE_GUN, ltxName("LTX 07/SD %s", "Serenity"));
        addItem(SHOTGUN, ltxName("LTX 21/SG %s", "Aurora"));
        addItem(GRENADE_LAUNCHER, ltxName("LTX 33/GL %s", "Hanabi"));
        addItem(LINEAR_FUSION_RIFLE, ltxName("LTX 38/LF %s", "Stargazer"));
        addItem(ROCKET_LAUNCHER, ltxName("LTX 42/RL %s", "Daybreak"));
        addItem(MAGNUM, ltxName("LTX 77/HX %s", "Nova"));

        simpleHintItem(AUTO_AMMO_CANISTER, "Automatics Ammo Canister", "Stabilized energy suitable for use in low-power high frequency projectile synthesis.");
        simpleHintItem(SPECIALIST_AMMO_CANISTER, "Specialist Ammo Canister", "Concentrated energy suitable for use in medium-power projectile synthesis.");
        simpleHintItem(EXPLOSIVES_AMMO_CANISTER, "Explosives Ammo Canister", "Volatile energy suitable for use in explosive weaponry.");
        simpleHintItem(ROCKET_LAUNCHER_AMMO, "Daybreak Rockets", "Programmable, compact rockets for use in the Daybreak launcher.");
        simpleHintItem(MAGNUM_AMMO_CANISTER, "Magnum Ammo Canister", "Highly concentrated, compact energy capable of handling the energy spikes of the Nova magnum.");
        //#endregion

        //#region Equipment upgrades
        add(TOOL_DEFAULT_UPGRADE_TITLE, "ε Core Systems");
        upgradeDescOnly(LimaTechEquipmentUpgrades.LTX_SHOVEL_DEFAULT, "Standard issue operating system. Excavator emitter preserves topographical integrity.");
        upgradeDescOnly(LimaTechEquipmentUpgrades.LTX_AXE_DEFAULT, "Standard issue operating system. Cutter emitters have a low but functional chance to sever anatomical curiosities.");
        upgradeDescOnly(LimaTechEquipmentUpgrades.LTX_WRENCH_DEFAULT, "Standard issue operating system. Designed to facilitate industrial logistics.");
        upgrade(LimaTechEquipmentUpgrades.SUBMACHINE_GUN_DEFAULT, "Serenity Intrinsics", "Serenity's small light-frags zip right through targets without a trace.");
        upgrade(LimaTechEquipmentUpgrades.SHOTGUN_DEFAULT, "Aurora Intrinsics", "Aurora's combat precepts, specialized in fast assault and scout operations.");

        upgrade(LimaTechEquipmentUpgrades.DRILL_DIAMOND_LEVEL, "Diamond Drill Core", "Diamond plating allows the drill to harvest diamond-level materials.");
        upgrade(LimaTechEquipmentUpgrades.DRILL_NETHERITE_LEVEL, "Netherite Drill Core", "The composite alloy plating on the drill allows harvesting of netherite-level materials.");
        upgrade(LimaTechEquipmentUpgrades.DRILL_OMNI_MINER, "Omni-Drill Precept", "Special-issue drill cutter emitters are modified to work on any material type.");
        upgrade(LimaTechEquipmentUpgrades.TOOL_VIBRATION_CANCEL, "Resonance-Tuned Servos", "Special lining on this tool's servos dampen vibrations from standard use.");
        upgrade(LimaTechEquipmentUpgrades.TOOL_DIRECT_DROPS, "Mining Subspace Link", "Tool systems interface directly with your inventory, depositing materials without physical collection.");

        upgrade(LimaTechEquipmentUpgrades.WEAPON_VIBRATION_CANCEL, "Echo Suppressor", "Augments weapons and projectiles with an anti-resonance field, erasing vibration signatures");
        upgrade(LimaTechEquipmentUpgrades.HIGH_IMPACT_ROUNDS, "High Impact Rounds", "Light-frags with a punch! Send targets flying back regardless of their knockback resistances.");
        upgrade(LimaTechEquipmentUpgrades.MAGNUM_SCALING_ROUNDS, "Stellar Reality Disruptor", "Rip through reality itself with this Nova upgrade. Ensures swift defeat of even the strongest enemies.");

        upgrade(LimaTechEquipmentUpgrades.UNIVERSAL_STEALTH_DAMAGE, "Biometric Obfuscation", "Targeting systems mask your signature, leaving no trace of your involvement. May not be effective against all targets.");
        upgrade(LimaTechEquipmentUpgrades.UNIVERSAL_ENERGY_AMMO, "Weapon Energy Systems", "Reroutes magazine feed to draw from Common Energy reserves. Say goodbye to your ammo stash.");
        upgrade(LimaTechEquipmentUpgrades.UNIVERSAL_INFINITE_AMMO, "//ERR~∞//Magazine", "Ignore the laws of physics with this never-ending ammo source. Try not to cause a mass extinction event, yeah?");
        upgrade(LimaTechEquipmentUpgrades.WEAPON_ARMOR_PIERCE, "Armor-Piercing Rounds", "Volatile energy dispersal precepts. Allows weapons to partially breach armor.");
        upgrade(LimaTechEquipmentUpgrades.WEAPON_SHIELD_REGEN, "Regenerative Link", "Captures enemy energy upon elimination to power medical nano-tech and the bubble shield.");
        upgrade(LimaTechEquipmentUpgrades.WEAPON_DIRECT_DROPS, "Combat Subspace Link", "Weapon systems interface directly with your inventory, depositing loot without physical collection.");

        upgrade(LimaTechEquipmentUpgrades.SILK_TOUCH_ENCHANT, "Stabilized Harvest Matrix", "Calibrated to extract intact samples from the terrain.");
        upgrade(LimaTechEquipmentUpgrades.FORTUNE_ENCHANTMENT, "Overclocked Harvest Matrix", "Calibrated to extract superior quantities of valuable resources.");
        upgrade(LimaTechEquipmentUpgrades.LOOTING_ENCHANTMENT, "Combat Yield Protocol", "Calibrated to maximize structural integrity of salvageable biomaterials.");
        upgrade(LimaTechEquipmentUpgrades.AMMO_SCAVENGER_ENCHANTMENT, "Munition Trace Unit", "Improves detection of high-grade LTX ammunition in the field.");
        upgrade(LimaTechEquipmentUpgrades.RAZOR_ENCHANTMENT, "Severance Algorithm", "Weapon calibration enables the retrieval of anatomical curiosities.");

        upgrade(LimaTechEquipmentUpgrades.GRENADE_LAUNCHER_PROJECTILE_SPEED, "Hanabi Launch Boost", "Increases the velocity of the Hanabi grenades.");

        upgrade(LimaTechEquipmentUpgrades.FLAME_GRENADE_CORE, "Hanabi Core/Flame", "Grenades are loaded with a concentrated fuel that creates powerful flames.");
        upgrade(LimaTechEquipmentUpgrades.CRYO_GRENADE_CORE, "Hanabi Core/Cryo", "Grenades contain a cryogenic compound that freezes a large area.");
        upgrade(LimaTechEquipmentUpgrades.ELECTRIC_GRENADE_CORE, "Hanabi Core/Electric", "Grenades create a burst of electrical energy. Recommended for use in humid/aquatic environments.");
        upgrade(LimaTechEquipmentUpgrades.ACID_GRENADE_CORE, "Hanabi Core/Acid", "Grenades contain a highly corrosive acid that reduces target armor strength.");
        upgrade(LimaTechEquipmentUpgrades.NEURO_GRENADE_CORE, "Hanabi Core/Neuro", "Grenades contain a powerful neuro-suppressant agent that highly reduces target attack strength.");
        upgrade(LimaTechEquipmentUpgrades.OMNI_GRENADE_CORE, "Hanabi Core/ARCOIRIS", "Full spectrum adaptable core for the Hanabi. Allows the use of any of grenade shells.");
        //#endregion

        //#region Machine upgrades
        upgrade(LimaTechMachineUpgrades.ESA_CAPACITY_UPGRADE, "Auxiliary Energy Cells", "Increases the energy capacity and transfer rate of the Energy Storage Array.");
        upgrade(LimaTechMachineUpgrades.STANDARD_MACHINE_SYSTEMS, "Standard Machine Systems", "Core modular systems designed for balanced efficiency.");
        upgrade(LimaTechMachineUpgrades.ULTIMATE_MACHINE_SYSTEMS, "Ultimate Machine Systems", "The pinnacle of engineering precision! Achieves near-instantaneous crafting at the cost of immense energy consumption.");
        upgrade(LimaTechMachineUpgrades.FABRICATOR_UPGRADE, "Enhanced Tool Head", "Elevate your Fabricator's manufacturing capabilities with superior internal components.");
        upgrade(LimaTechMachineUpgrades.TURRET_LOOTING, "Efficient Target Disposal", "Smarter turret targeting systems allow for increased loot drops from eliminated targets.");
        upgrade(LimaTechMachineUpgrades.TURRET_RAZOR, "Headhunter Scope", "Precise turret calibration enables the collection of anatomical curiosities.");
        upgrade(LimaTechMachineUpgrades.TURRET_LOOT_COLLECTOR, "Matter SubLink", "Loot is sent directly to the turret’s storage. If full, items appear at the turret’s base.");
        //#endregion

        // Creative tabs
        creativeTab(MAIN_TAB, "LimaTech");
        creativeTab(EQUIPMENT_MODULES_TAB, "LimaTech: Equipment Upgrades");
        creativeTab(MACHINE_MODULES_TAB, "LimaTech: Machine Upgrades");

        //#region Menu titles
        add(LimaTechMenus.MACHINE_UPGRADES, "Machine Upgrades");
        add(LimaTechMenus.ENERGY_STORAGE_ARRAY, "Energy Storage Array");
        add(LimaTechMenus.DIGITAL_FURNACE, "Digital Furnace");
        add(LimaTechMenus.GRINDER, "Grinder");
        add(LimaTechMenus.RECOMPOSER, "Recomposer");
        add(LimaTechMenus.MATERIAL_FUSING_CHAMBER, "Material Fusing Chamber");
        add(LimaTechMenus.FABRICATOR, "Fabricator");
        add(LimaTechMenus.AUTO_FABRICATOR, "Auto Fabricator");
        add(LimaTechMenus.EQUIPMENT_UPGRADE_STATION, "Equipment Upgrade Station");
        add(LimaTechMenus.ROCKET_TURRET, "Atmos Turret");
        add(LimaTechMenus.RAILGUN_TURRET, "Noctis Turret");
        //#endregion

        // Machine input types
        add(BlockEntityInputType.ITEMS, "Items IO Control");
        add(BlockEntityInputType.ENERGY, "Energy IO Control");
        add(BlockEntityInputType.FLUIDS, "Fluids IO Control");

        // Recipe types
        add(GRINDING, "Grinding");
        add(RECOMPOSING, "Recomposing");
        add(MATERIAL_FUSING, "Material Fusing");
        add(FABRICATING, "Fabricating");

        // Entity type names
        addEntityType(LimaTechEntities.ORB_GRENADE, "Orb Grenade");
        addEntityType(LimaTechEntities.DAYBREAK_ROCKET, "Daybreak Rocket");
        addEntityType(LimaTechEntities.TURRET_ROCKET, "Turret Rocket");
        addEntityType(LimaTechEntities.STICKY_FLAME, "Sticky Flame");

        // Mob effects
        addEffect(LimaTechMobEffects.FROSTBITE, "Frostbite");
        addEffect(LimaTechMobEffects.CORROSIVE, "Corroding");
        addEffect(LimaTechMobEffects.NEURO_SUPPRESSED, "Neuro-Suppressed");

        // Enchantments
        enchantment(LimaTechEnchantments.RAZOR, "Razor");
        enchantment(LimaTechEnchantments.AMMO_SCAVENGER, "Ammo Scavenger");

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

        add(BLUEPRINT_TOAST_MESSAGE, "New Fabrication Data");
        add(MACHINE_TICKS_PER_OP_TOOLTIP, "Ticks per operation: %s");
        add(EMPTY_ITEM_INVENTORY_TOOLTIP, "No items stored");
        add(ITEM_INVENTORY_TOOLTIP, "Stored Items");
        add(FABRICATOR_SELECTED_RECIPE_TOOLTIP, "Left click to craft. Right click to encode blueprint (must have blank blueprint in slot).");
        add(FABRICATOR_LOCKED_TOOLTIP, "This recipe is advancement locked. You must discover it first before being able to use it.");
        add(INLINE_ENERGY_REQUIRED_TOOLTIP, "Energy required: %s");
        add(CRAFTING_PROGRESS_TOOLTIP, "Crafting: %s%%");
        add(UPGRADE_RANK_TOOLTIP, "Rank %s/%s");
        add(UPGRADE_REMOVE_HINT, "Shift + left click to remove upgrade. Must have space in your inventory.");
        add(UPGRADE_COMPATIBILITY_TOOLTIP, "Compatible with:");
        add(EQUIPMENT_UPGRADE_MODULE_TOOLTIP, "Equipment Upgrade Module");
        add(MACHINE_UPGRADE_MODULE_TOOLTIP, "Machine Upgrade Module");

        add(CompoundValueOperation.REPLACE_BASE, "Sets %s as new base");
        add(CompoundValueOperation.FLAT_ADDITION, "%s");
        add(CompoundValueOperation.ADD_MULTIPLIED_BASE, "%s of base");
        add(CompoundValueOperation.ADD_MULTIPLIED_TOTAL, "%s of total");
        add(CompoundValueOperation.MULTIPLY, "%sx");

        add(INVALID_UPGRADE_HINT, "This upgrade data in this module is invalid or corrupted. Shift+Right Click to revert to an empty module.");
        add(BLANK_BLUEPRINT_HINT, "No blueprint data. Encode at a Fabricator.");
        add(INVALID_BLUEPRINT_HINT, "The recipe in this blueprint is invalid or corrupted. Shift+Right Click to clear.");

        add(THIS_ENTITY_TARGET_TOOLTIP, "Enemy");
        add(ATTACKER_ENTITY_TARGET_TOOLTIP, "User");
        add(DIRECT_ATTACKER_ENTITY_TARGET_TOOLTIP, "Projectile");
        add(LAST_ATTACKING_PLAYER_ENTITY_TARGET_TOOLTIP, "Last Attacking Player");
        add(ATTRIBUTE_AMOUNT_VALUE_TOOLTIP, "%s of %s %s");

        add(LimaTechUpgradeEffectComponents.ENERGY_CAPACITY, "%s CE capacity");
        add(LimaTechUpgradeEffectComponents.ENERGY_TRANSFER_RATE, "%s CE transfer rate/tick");
        add(LimaTechUpgradeEffectComponents.ENERGY_USAGE, "%s CE usage");
        add(LimaTechUpgradeEffectComponents.EQUIPMENT_DAMAGE, "%s damage");
        add(LimaTechUpgradeEffectComponents.WEAPON_PROJECTILE_SPEED, "%s projectile speed");
        add(LimaTechUpgradeEffectComponents.TICKS_PER_OPERATION, "%s ticks/operation");

        add(MINING_EFFECTIVE_BLOCKS_EFFECT, "Effective against %s blocks");
        add(MINING_BASE_SPEED_EFFECT, "Base mining speed: %s");
        add(WEAPON_KNOCKBACK_EFFECT, "%s knockback power");
        add(DYNAMIC_DAMAGE_TAG_EFFECT, "+Damage Tags: %s");
        add(SUPPRESS_VIBRATIONS_EFFECT, "Suppresses %s sculk vibrations");
        add(DIRECT_BLOCK_DROPS_EFFECT, "Directly collects %s block drops");
        add(DIRECT_ENTITY_DROPS_EFFECT, "Directly collects %s entity drops");
        add(ENERGY_AMMO_EFFECT, "Weapon reloads from an internal CE reserve");
        add(INFINITE_AMMO_EFFECT, "Grants weapon infinite ammunition and magazine");
        add(REDUCTION_MODIFIER_EFFECT, "%s %s breach");
        add(BUBBLE_SHIELD_EFFECT, "%s Bubble Shield/kill, (max %s)");
        add(MOB_EFFECT_UPGRADE_EFFECT, "Applies %s (%s)");
        add(ENCHANTMENT_UPGRADE_EFFECT, "%s %s Enchantment");
        add(GRENADE_UNLOCK_EFFECT, "Can use %s shells");

        add(TooltipShiftHintItem.HINT_HOVER_TOOLTIP, "Hold SHIFT for extra info");
        add(WeaponAmmoSource.NORMAL.getItemTooltip(), "Reloads with %s");
        add(WeaponAmmoSource.COMMON_ENERGY_UNIT.getItemTooltip(), "Synthesizes ammo from Common Energy Units");
        add(WeaponAmmoSource.INFINITE.getItemTooltip(), "This weapon has infinite ammo!");

        add(WeaponItem.AMMO_LOADED_TOOLTIP, "Ammo: %s/%s");
        add(GrenadeLauncherWeaponItem.GRENADE_TYPE_TOOLTIP, "%s shells equipped");

        add(LimaTechTooltipUtil.ALL_HOLDER_SET, "all");
        add(LimaTechTooltipUtil.AMBIGUOUS_HOLDER_SET, "certain");
        //#endregion

        // Sound subtitles
        soundEvent(WEAPON_MODE_SWITCH, "Weapon mode switched");
        soundEvent(TURRET_TARGET_FOUND, "Turret finds targets");
        soundEvent(SUBMACHINE_GUN_LOOP, "Submachine gun firing");
        soundEvent(SHOTGUN_FIRE, "Shotgun fires");
        soundEvent(GRENADE_LAUNCHER_FIRE, "Grenade launched");
        soundEvent(LINEAR_FUSION_CHARGE, "Linear fusion rifle charges");
        soundEvent(LINEAR_FUSION_FIRE, "Linear fusion rifle fires");
        soundEvent(ROCKET_LAUNCHER_FIRE, "Rocket launched");
        soundEvent(MAGNUM_FIRE, "Magnum fires");
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
        damageType(LimaTechDamageTypes.LIGHTFRAG, "%2$s shot %1$s with %3$s");
        damageType(LimaTechDamageTypes.EXPLOSIVE_GRENADE, "%s was blown away by %s's %s");
        damageType(LimaTechDamageTypes.FLAME_GRENADE, "%s was incinerated by %s's %s");
        damageType(LimaTechDamageTypes.CRYO_GRENADE, "%s was frozen solid by %s's %s");
        damageType(LimaTechDamageTypes.ELECTRIC_GRENADE, "%s was electrocuted by %s's %s");
        damageType(LimaTechDamageTypes.ACID_GRENADE, "%s was dissolved by %s's %s");
        damageType(LimaTechDamageTypes.NEURO_GRENADE, "%s was decayed by %s's %s");
        damageType(LimaTechDamageTypes.ROCKET_LAUNCHER, "%s was blown up by %s's %s");

        noItemCausingEntityOnlyDamageMessage(LimaTechDamageTypes.STICKY_FLAME, "%s was cooked well-done by %s", "%s was cooked well-done");
        noItemCausingEntityOnlyDamageMessage(LimaTechDamageTypes.TURRET_ROCKET, "%s was shot down by %s's Atmos turret", "%s was shot down by a rogue Atmos turret");
        noItemCausingEntityOnlyDamageMessage(LimaTechDamageTypes.RAILGUN_TURRET, "%s was obliterated by %s's Noctis turret", "%s was obliterated by a rogue Noctis turret");

        //#region Advancements
        //#endregion

        // Named tags
        add(LimaTechTags.Items.WRENCH_BREAKABLE, "wrench-breakable");
        add(LimaTechTags.GameEvents.WEAPON_VIBRATIONS, "weaponry");
        add(LimaTechTags.GameEvents.HANDHELD_EQUIPMENT, "handheld tool");

        namedDamageTag(LimaTechTags.DamageTypes.WEAPON_DAMAGE, "Weapon Damage");
        namedDamageTag(LimaTechTags.DamageTypes.BYPASS_SURVIVAL_DEFENSES, "Bypass All Survival Defenses");
        namedDamageTag(DamageTypeTags.NO_ANGER, "No Anger");
        namedDamageTag(DamageTypeTags.NO_KNOCKBACK, "No Knockback");

        // GuideME compatibility
        add("item.limatech.guide_tablet", "LTX Guide Tablet");

        // Key mapping
        add(LimaTechKeyMappings.CATEGORY_LIMATECH, "LimaTech");
        add(LimaTechKeyMappings.RELOAD_KEY_SUBTITLE, "Reload Weapon");
    }

    private void simpleHintItem(DeferredItem<SimpleHintItem> item, String name, String hint)
    {
        addItem(item, name);
        add(item.get().getShiftHint(), hint);
    }

    private String ltxName(String pattern, String name)
    {
        name = "§o" + name + "§r";
        return String.format(pattern, name);
    }

    private String ltxToolName(String name)
    {
        return "LTX ε-Series " + name;
    }

    private void noItemCausingEntityOnlyDamageMessage(ResourceKey<DamageType> damageTypeKey, String translation, String unownedTranslation)
    {
        damageTypeAndVariants(damageTypeKey, translation, collector -> collector.accept("unowned", unownedTranslation));
    }

    private void upgrade(ResourceKey<? extends UpgradeBase<?, ?>> key, String title, String description)
    {
        add(ModResources.registryPrefixedIdLangKey(key), title);
        add(ModResources.registryPrefixVariantIdLangKey(key, UpgradeBaseBuilder.DEFAULT_DESCRIPTION_SUFFIX), description);
    }

    private void upgradeDescOnly(ResourceKey<? extends UpgradeBase<?, ?>> key, String description)
    {
        add(ModResources.registryPrefixVariantIdLangKey(key, UpgradeBaseBuilder.DEFAULT_DESCRIPTION_SUFFIX), description);
    }

    private void namedDamageTag(TagKey<DamageType> tagKey, String value)
    {
        add(LimaTechLang.namedDamageTagKey(tagKey), value);
    }
}