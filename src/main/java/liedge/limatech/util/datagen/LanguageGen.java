package liedge.limatech.util.datagen;

import liedge.limacore.data.generation.LimaLanguageProvider;
import liedge.limatech.LimaTech;
import liedge.limatech.client.LimaTechKeyMappings;
import liedge.limatech.item.TooltipShiftHintItem;
import liedge.limatech.item.weapon.GrenadeLauncherWeaponItem;
import liedge.limatech.item.weapon.WeaponItem;
import liedge.limatech.lib.weapons.OrbGrenadeElement;
import liedge.limatech.lib.weapons.WeaponDeathMessageType;
import liedge.limatech.registry.*;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;

import java.util.function.Supplier;

import static liedge.limatech.client.LimaTechLangKeys.*;
import static liedge.limatech.registry.LimaTechBlocks.*;
import static liedge.limatech.registry.LimaTechItems.*;
import static liedge.limatech.registry.LimaTechSounds.*;

class LanguageGen extends LimaLanguageProvider
{
    LanguageGen(PackOutput output)
    {
        super(output, LimaTech.RESOURCES);
    }

    private String localizeDyeColor(DyeColor color)
    {
        return switch (color)
        {
            case WHITE -> "White";
            case ORANGE -> "Orange";
            case MAGENTA -> "Magenta";
            case LIGHT_BLUE -> "Light Blue";
            case YELLOW -> "Yellow";
            case LIME -> "Lime";
            case PINK -> "Pink";
            case GRAY -> "Gray";
            case LIGHT_GRAY -> "Light Gray";
            case CYAN -> "Cyan";
            case PURPLE -> "Purple";
            case BLUE -> "Blue";
            case BROWN -> "Brown";
            case GREEN -> "Green";
            case RED -> "Red";
            case BLACK -> "Black";
        };
    }

    private String localizeGrenadeElement(OrbGrenadeElement element)
    {
        return switch (element)
        {
            case EXPLOSIVE -> "Explosive";
            case FLAME -> "Flame";
            case FREEZE -> "Freeze";
            case ELECTRIC -> "Electric";
            case ACID -> "Acid";
        };
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

        GLOW_BLOCKS.forEach((color, deferredBlock) -> addBlock(deferredBlock, localizeDyeColor(color) + " Glow Block"));

        addBlock(FABRICATOR, "Fabricator");

        addBlock(ROCKET_TURRET, "Anti-Air Rocket Turret");
        //#endregion

        //#region Items
        addItem(RAW_TITANIUM, "Raw Titanium");
        addItem(TITANIUM_INGOT, "Titanium Ingot");
        addItem(TITANIUM_NUGGET, "Titanium Nugget");
        addItem(RAW_NIOBIUM, "Raw Niobium");
        addItem(NIOBIUM_INGOT, "Niobium Ingot");
        addItem(NIOBIUM_NUGGET, "Niobium Nugget");

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

        addItem(EXPLOSIVES_WEAPON_TECH_SALVAGE, "Salvaged Tech: Explosive Weapon Systems");
        addItem(TARGETING_TECH_SALVAGE, "Salvaged Tech: Auto-Targeting Systems");

        addWeaponItem(SUBMACHINE_GUN, "LTX-07/SD ", "Serenity");
        addWeaponItem(SHOTGUN, "LTX-19/SG ", "Aurora");
        addWeaponItem(GRENADE_LAUNCHER, "LTX-33/GL ", "Hanabi");
        addWeaponItem(ROCKET_LAUNCHER, "LTX-42/RL ", "Daybreak");
        addWeaponItem(MAGNUM, "LTX-77/HX ", "Nascent Star");

        addItem(AUTO_AMMO_CANISTER, "Automatics Ammo Canister");
        addItem(SPECIALIST_AMMO_CANISTER, "Specialist Ammo Canister");
        addItem(EXPLOSIVES_AMMO_CANISTER, "Explosives Ammo Canister");
        addItem(LEGENDARY_AMMO_CANISTER, "Legendary Ammo Canister");

        addItem(INFINITE_ENERGY_CARD, "Infinite Energy Card");
        //#endregion

        // Creative tabs
        creativeTab(MAIN_TAB, "LimaTech");

        //#region Menu titles
        add(LimaTechMenus.FABRICATOR, "Fabricator");
        //#endregion

        // Recipe types
        add(LimaTechCrafting.FABRICATING_TYPE, "Fabricating");

        // Entity type names
        addEntityType(LimaTechEntities.ORB_GRENADE, "Orb Grenade");
        addEntityType(LimaTechEntities.MISSILE, "Missile");

        // Mob effects
        addEffect(LimaTechMobEffects.CORROSIVE, "Corroding");

        //#region Tooltips
        add(ENERGY_TOOLTIP, "Energy");
        add(ITEM_ENERGY_TOOLTIP, "Stored Energy: %s");
        add(ITEM_INVENTORY_TOOLTIP, "Stored Items");
        add(FABRICATOR_CLICK_TO_CRAFT_TOOLTIP, "Click again to craft");
        add(FABRICATOR_ENERGY_REQUIRED_TOOLTIP, "Energy required: %s");
        add(CRAFTING_PROGRESS_TOOLTIP, "Crafting: %s%%");
        add(TooltipShiftHintItem.HINT_HOVER_TOOLTIP, "Hold SHIFT for extra info");
        add(WeaponItem.AMMO_ITEM_TOOLTIP, "Reloads with %s");
        add(WeaponItem.AMMO_LOADED_TOOLTIP, "Ammo: %s/%s");
        add(GrenadeLauncherWeaponItem.ORB_ELEMENT_TOOLTIP, "%s rounds equipped");
        //#endregion

        // Sound subtitles
        soundEvent(GRENADE_LAUNCHER_FIRE, "Grenade launcher bloops");
        soundEvent(MISSILE_EXPLODE, "Missile explodes");
        GRENADE_SOUNDS.forEach((element, holder) -> soundEvent(holder, localizeGrenadeElement(element) + " grenade explodes"));

        // Orb grenade elements
        for (OrbGrenadeElement element : OrbGrenadeElement.values())
        {
            add(element, localizeGrenadeElement(element));
        }

        // Damage types
        add(WeaponDeathMessageType.INVALID_SOURCE_MESSAGE, "%s was killed by unknown technology");
        add(WeaponDeathMessageType.UNKNOWN_OWNER_PROJECTILE_MESSAGE, "%s was killed by a stray %s");
        damageType(LimaTechDamageTypes.LIGHTFRAG, "%2$s shot %1$s with %3$s");
        entityAttackDamageType(LimaTechDamageTypes.ROCKET_TURRET, "%s was blown up by %s", "%s was blown up by %s using %s");

        //#region Advancements
        //#endregion

        // Key mapping
        add(LimaTechKeyMappings.CATEGORY_LIMATECH, "LimaTech");
        add(LimaTechKeyMappings.RELOAD_KEY_SUBTITLE, "Reload Weapon");
    }

    private void addWeaponItem(Supplier<? extends WeaponItem> supplier, String prefix, String name)
    {
        add(supplier.get().getNamePrefix(), prefix);
        addItem(supplier, name);
    }
}