package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.io.MachineInputType;
import liedge.limatech.client.LimaTechKeyMappings;
import liedge.limatech.item.SimpleHintItem;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.GrenadeType;
import liedge.limatech.lib.weapons.WeaponAmmoSource;
import liedge.limatech.registry.*;
import liedge.limatech.upgradesystem.EquipmentUpgrade;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.function.Supplier;

import static liedge.limatech.client.LimaTechLang.*;
import static liedge.limatech.registry.LimaTechBlocks.*;
import static liedge.limatech.registry.LimaTechCreativeTabs.MAIN_TAB;
import static liedge.limatech.registry.LimaTechCreativeTabs.WEAPON_MODS_TAB;
import static liedge.limatech.registry.LimaTechItems.*;
import static liedge.limatech.registry.LimaTechRecipeTypes.*;
import static liedge.limatech.registry.LimaTechSounds.*;

class LanguageGen extends LimaLanguageProvider
{
    LanguageGen(PackOutput output)
    {
        super(output, LimaTech.RESOURCES);
    }

    @Override
    protected void addTranslations()
    {
        // Attributes
        add(LimaTechAttributes.UNIVERSAL_STRENGTH.get().getDescriptionId(), "Universal Attack Strength");

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

        addBlock(TIERED_ENERGY_STORAGE_ARRAY, "Energy Storage Array");
        addBlock(INFINITE_ENERGY_STORAGE_ARRAY, "Energy Storage Array");
        addBlock(DIGITAL_FURNACE, "Digital Furnace");
        addBlock(GRINDER, "Grinder");
        addBlock(RECOMPOSER, "Recomposer");
        addBlock(MATERIAL_FUSING_CHAMBER, "Material Fusing Chamber");
        addBlock(FABRICATOR, "Fabricator");
        addBlock(EQUIPMENT_MOD_TABLE, "Equipment Mod Table");

        addBlock(ROCKET_TURRET, "Anti-Air Rocket Turret");
        //#endregion

        //#region Items
        addItem(RAW_TITANIUM, "Raw Titanium");
        addItem(TITANIUM_INGOT, "Titanium Ingot");
        addItem(TITANIUM_NUGGET, "Titanium Nugget");
        addItem(RAW_NIOBIUM, "Raw Niobium");
        addItem(NIOBIUM_INGOT, "Niobium Ingot");
        addItem(NIOBIUM_NUGGET, "Niobium Nugget");

        addItem(WHITE_PIGMENT, "Titanium White Pigment");
        addItem(LIME_PIGMENT, "Lime Pigment");

        addItem(TITANIUM_SWORD, "Titanium Sword");
        addItem(TITANIUM_SHOVEL, "Titanium Shovel");
        addItem(TITANIUM_PICKAXE, "Titanium Pickaxe");
        addItem(TITANIUM_AXE, "Titanium Axe");
        addItem(TITANIUM_HOE, "Titanium Hoe");
        addItem(TITANIUM_SHEARS, "Titanium Shears");

        addItem(DEEPSLATE_POWDER, "Deepslate Powder");
        addItem(SLATE_ALLOY_INGOT, "Slate Alloy Ingot");
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

        addItem(MACHINE_WRENCH, "Machine Wrench");

        simpleHintItem(EXPLOSIVES_WEAPON_TECH_SALVAGE, "Salvaged Tech: Explosive Weapon Systems", "Broken components from an explosives handling device. Might be useful in reconstructing explosive weaponry.");
        simpleHintItem(TARGETING_TECH_SALVAGE, "Salvaged Tech: Auto-Targeting Systems", "Broken electronics from a targeting computer. Might be useful in reconstructing guidance systems for weaponry.");

        addWeaponItem(SUBMACHINE_GUN, "Serenity", "LTX-07/SD %s");
        addWeaponItem(SHOTGUN, "Aurora", "LTX-19/SG %s");
        addWeaponItem(GRENADE_LAUNCHER, "Hanabi", "LTX-33/GL %s");
        addWeaponItem(ROCKET_LAUNCHER, "Daybreak", "LTX-42/RL %s");
        addWeaponItem(MAGNUM, "Nova", "LTX-77/HX %s");

        addItem(EQUIPMENT_UPGRADE_ITEM, "Equipment Upgrade Module");
        add(EQUIPMENT_UPGRADE_ITEM_CUSTOM_NAME, "Equipment Upgrade: %s");
        add(INVALID_EQUIPMENT_UPGRADE_ITEM, "Corrupted Equipment Upgrade");

        simpleHintItem(AUTO_AMMO_CANISTER, "Automatics Ammo Canister", "Stabilized energy suitable for use in low-power high frequency projectile synthesis.");
        simpleHintItem(SPECIALIST_AMMO_CANISTER, "Specialist Ammo Canister", "Concentrated energy suitable for use in medium-power projectile synthesis.");
        simpleHintItem(EXPLOSIVES_AMMO_CANISTER, "Explosives Ammo Canister", "Volatile energy suitable for use in explosive weaponry.");
        simpleHintItem(MAGNUM_AMMO_CANISTER, "Magnum Ammo Canister", "Highly concentrated, compact energy capable of handling the energy spikes of the Nova magnum.");
        //#endregion

        // Weapon upgrade types
        equipmentUpgrade(LimaTechEquipmentUpgrades.SMG_BUILT_IN, "Serenity Intrinsics", "Serenity's small light-frags zip right through targets without a trace.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.SHOTGUN_BUILT_IN, "Aurora Intrinsics", "When in main hand, move 25% faster and step 1 block higher. Additionally, light-frags pierce 15% of armor.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.HIGH_IMPACT_ROUNDS, "High Impact Rounds", "Light-frags with a punch! Send targets flying back regardless of their knockback resistances.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.MAGNUM_SCALING_ROUNDS, "Stellar Reality Disruptor", "Adds 20% of the target's max health as bonus damage to the Nova's lightfrags. Also ignores armor.");

        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_ANTI_VIBRATION, "Acoustic Attenuation", "Eliminates Sculk sound vibrations from weapons and their projectiles.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_STEALTH_DAMAGE, "Biometric Obfuscation", "Targets hit by weapons and their projectiles will (generally) not become aggressive towards the user.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_ENERGY_AMMO, "Energy Ammo Synthesis", "Modifies the weapon's magazine system to reload from an internal Common Energy reserve instead of ammo canister items.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_INFINITE_AMMO, "Infinite Ammo", "Weapon ignores ammo and can fire infinitely.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_ARMOR_PIERCE, "Armor-Piercing Rounds", "Weapon ignores 10% of armor per upgrade rank.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.UNIVERSAL_SHIELD_REGEN, "Shield Siphon", "Restores 4 points of bubble shield per kill, for a maximum of the upgrade rank times 10.");

        equipmentUpgrade(LimaTechEquipmentUpgrades.LOOTING_ENCHANTMENT, "Loot Drop Booster", "Provides 'Looting' enchantment levels equal to the rank of the upgrade.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.AMMO_SCAVENGER_ENCHANTMENT, "Ammo Scavenger", "Increases the chance to find rarer weapon ammo types based on the upgrade rank.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.RAZOR_ENCHANTMENT, "Razor Lightfrags", "Increases mob head drops proportional to the upgrade rank.");

        equipmentUpgrade(LimaTechEquipmentUpgrades.GRENADE_LAUNCHER_PROJECTILE_SPEED, "Hanabi Launch Boost", "Increases the velocity of the Hanabi grenades.");

        equipmentUpgrade(LimaTechEquipmentUpgrades.FLAME_GRENADE_CORE, "Hanabi Core/Flame", "Grenades are loaded with a concentrated fuel that creates powerful flames.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.FREEZE_GRENADE_CORE, "Hanabi Core/Freeze", "Grenades contain a cryogenic compound that freezes a large area.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.ELECTRIC_GRENADE_CORE, "Hanabi Core/Electric", "Grenades create a burst of electrical energy. Recommended for use in humid/aquatic environments.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.ACID_GRENADE_CORE, "Hanabi Core/Acid", "Grenades contain a highly corrosive acid that reduces target armor strength.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.NEURO_GRENADE_CORE, "Hanabi Core/Neuro", "Grenades contain a powerful neuro-suppressant agent that highly reduces target attack strength.");
        equipmentUpgrade(LimaTechEquipmentUpgrades.OMNI_GRENADE_CORE, "Hanabi Core/ARCOIRIS", "Full spectrum adaptable core for the Hanabi. Allows the use of any of grenade types.");

        // Creative tabs
        creativeTab(MAIN_TAB, "LimaTech");
        creativeTab(WEAPON_MODS_TAB, "LimaTech: Weapon Mods");

        //#region Menu titles
        add(LimaTechMenus.ENERGY_STORAGE_ARRAY, "Energy Storage Array");
        add(LimaTechMenus.DIGITAL_FURNACE, "Digital Furnace");
        add(LimaTechMenus.GRINDER, "Grinder");
        add(LimaTechMenus.RECOMPOSER, "Recomposer");
        add(LimaTechMenus.MATERIAL_FUSING_CHAMBER, "Material Fusing Chamber");
        add(LimaTechMenus.FABRICATOR, "Fabricator");
        add(LimaTechMenus.EQUIPMENT_MOD_TABLE, "Equipment Mod Table");
        //#endregion

        // Machine input types
        add(MachineInputType.ITEMS, "Items IO Control");
        add(MachineInputType.ENERGY, "Energy IO Control");
        add(MachineInputType.FLUIDS, "Fluids IO Control");

        // Recipe types
        add(GRINDING, "Grinding");
        add(RECOMPOSING, "Recomposing");
        add(MATERIAL_FUSING, "Material Fusing");
        add(FABRICATING, "Fabricating");

        // Entity type names
        addEntityType(LimaTechEntities.ORB_GRENADE, "Orb Grenade");
        addEntityType(LimaTechEntities.ROCKET_LAUNCHER_MISSILE, "Rocket Launcher Missile");
        addEntityType(LimaTechEntities.TURRET_MISSILE, "Turret Missile");
        addEntityType(LimaTechEntities.STICKY_FLAME, "Sticky Flame");

        // Mob effects
        addEffect(LimaTechMobEffects.FREEZING, "Freezing");
        addEffect(LimaTechMobEffects.CORROSIVE, "Corroding");
        addEffect(LimaTechMobEffects.NEURO, "Neuro-Suppressed");

        // Enchantments
        enchantment(LimaTechEnchantments.RAZOR, "Razor");
        enchantment(LimaTechEnchantments.AMMO_SCAVENGER, "Ammo Scavenger");

        //#region Tooltips
        add(MACHINE_TIER_TOOLTIP, "Tier %s");
        add(INLINE_ENERGY_STORED, "Energy: %s");
        add(INLINE_ENERGY_CAPACITY, "Capacity: %s");
        add(INLINE_ENERGY_TRANSFER_RATE, "I/O: %s/t");

        add(TOP_IO_LABEL, "Top (%s)");
        add(BOTTOM_IO_LABEL, "Bottom (%s)");
        add(FRONT_IO_LABEL, "Front (%s)");
        add(REAR_IO_LABEL, "Rear (%s)");
        add(LEFT_IO_LABEL, "Left (%s)");
        add(RIGHT_IO_LABEL, "Right (%s)");
        add(BACK_BUTTON_LABEL, "Back");
        add(AUTO_OUTPUT_OFF_TOOLTIP, "Auto Output Disabled");
        add(AUTO_OUTPUT_ON_TOOLTIP, "Auto Output Enabled");
        add(AUTO_INPUT_OFF_TOOLTIP, "Auto Input Disabled");
        add(AUTO_INPUT_ON_TOOLTIP, "Auto Input Enabled");

        add(EMPTY_ITEM_INVENTORY_TOOLTIP, "No items stored");
        add(ITEM_INVENTORY_TOOLTIP, "Stored Items");
        add(FABRICATOR_CLICK_TO_CRAFT_TOOLTIP, "Click again to craft");
        add(FABRICATOR_ENERGY_REQUIRED_TOOLTIP, "Energy required: %s");
        add(CRAFTING_PROGRESS_TOOLTIP, "Crafting: %s%%");
        add(EQUIPMENT_UPGRADE_RANK, "Rank %s");
        add(UPGRADE_REMOVE_HINT, "Shift + left click to remove upgrade. Output slot must be empty.");
        add(UPGRADE_COMPATIBLE_ALL_WEAPONS, "Compatible with all LTX weapons");
        add(UPGRADE_COMPATIBLE_SPECIFIC_WEAPONS, "Compatible with:");

        add(TooltipShiftHintItem.HINT_HOVER_TOOLTIP, "Hold SHIFT for extra info");
        add(WeaponAmmoSource.NORMAL.getItemTooltip(), "Reloads with %s");
        add(WeaponAmmoSource.COMMON_ENERGY_UNIT.getItemTooltip(), "Synthesizes ammo from Common Energy Units");
        add(WeaponAmmoSource.INFINITE.getItemTooltip(), "This weapon has infinite ammo!");

        add(WeaponItem.AMMO_LOADED_TOOLTIP, "Ammo: %s/%s");
        add(WeaponItem.ENERGY_AMMO_COST_TOOLTIP, "Ammo Synth Cost: %s");
        add(GrenadeLauncherWeaponItem.GRENADE_TYPE_TOOLTIP, "%s rounds equipped");
        //#endregion

        // Sound subtitles
        soundEvent(WEAPON_MODE_SWITCH, "Weapon mode switched");
        soundEvent(SUBMACHINE_GUN_LOOP, "Submachine gun firing");
        soundEvent(SHOTGUN_FIRE, "Shotgun fires");
        soundEvent(GRENADE_LAUNCHER_FIRE, "Grenade launched");
        soundEvent(ROCKET_LAUNCHER_FIRE, "Rocket launched");
        soundEvent(MAGNUM_FIRE, "Magnum fires");
        soundEvent(MISSILE_EXPLODE, "Missile explodes");
        GRENADE_SOUNDS.forEach((element, holder) -> soundEvent(holder, localizeSimpleName(element) + " grenade explodes"));

        // Orb grenade elements
        for (GrenadeType element : GrenadeType.values())
        {
            add(element, localizeSimpleName(element));
        }

        // Damage types
        add(INVALID_WEAPON_DEATH_MESSAGE, "%s was killed by an invalid LTX weapon");
        add(STRAY_PROJECTILE_DEATH_MESSAGE, "%s was killed by a stray %s");
        damageType(LimaTechDamageTypes.LIGHTFRAG, "%2$s shot %1$s with %3$s");
        damageType(LimaTechDamageTypes.MAGNUM_LIGHTFRAG, "%s was erased by %s's %s");
        damageType(LimaTechDamageTypes.EXPLOSIVE_GRENADE, "%s was blown away by %s's %s");
        damageType(LimaTechDamageTypes.FLAME_GRENADE, "%s was incinerated by %s's %s");
        damageType(LimaTechDamageTypes.FREEZE_GRENADE, "%s was frozen solid by %s's %s");
        damageType(LimaTechDamageTypes.ELECTRIC_GRENADE, "%s was electrocuted by %s's %s");
        damageType(LimaTechDamageTypes.ACID_GRENADE, "%s was dissolved by %s's %s");
        damageType(LimaTechDamageTypes.NEURO_GRENADE, "%s was decayed by %s's %s");
        damageType(LimaTechDamageTypes.ROCKET_LAUNCHER, "%s was blown up by %s's %s");

        traceableDamageSourceMessage(LimaTechDamageTypes.STICKY_FLAME, "%s was cooked well-done", "%s was cooked well-done by %s");
        traceableDamageSourceMessage(LimaTechDamageTypes.TURRET_ROCKET, "%s was shot out of the sky by a rocket turret", "s was shot out of the sky by %s's rocket turret");

        //#region Advancements
        //#endregion

        // Patchouli compatibility
        add("item.limatech.guidebook", "LimaTech Guidebook");
        add("limatech.guidebook.landing_text", "Welcome to LimaTech! This guidebook contains hints, info, and instructions on the basic mechanics of the mod.");

        // Key mapping
        add(LimaTechKeyMappings.CATEGORY_LIMATECH, "LimaTech");
        add(LimaTechKeyMappings.RELOAD_KEY_SUBTITLE, "Reload Weapon");
    }

    private void simpleHintItem(DeferredItem<SimpleHintItem> item, String name, String hint)
    {
        addItem(item, name);
        add(item.get().getShiftHint(), hint);
    }

    private void addWeaponItem(Supplier<? extends WeaponItem> supplier, String name, String designation)
    {
        addItem(supplier, name);
        add(supplier.get().getDesignationId(), designation);
    }

    private void traceableDamageSourceMessage(ResourceKey<DamageType> damageTypeKey, String translation, String entityTranslation)
    {
        damageTypeAndVariants(damageTypeKey, translation, collector -> collector.accept("entity", entityTranslation));
    }

    private void equipmentUpgrade(ResourceKey<EquipmentUpgrade> key, String title, String description)
    {
        add(EquipmentUpgrade.defaultTitleTranslationKey(key), title);
        add(EquipmentUpgrade.defaultDescriptionTranslationKey(key), description);
    }
}