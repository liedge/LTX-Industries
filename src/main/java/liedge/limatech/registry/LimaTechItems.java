package liedge.limatech.registry;

import liedge.limacore.item.LimaCreativeTabGenerator;
import liedge.limacore.lib.energy.InfiniteEnergyStorage;
import liedge.limatech.LimaTech;
import liedge.limatech.item.InfiniteEnergyItem;
import liedge.limatech.item.LimaTechRarities;
import liedge.limatech.item.weapon.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.SimpleTier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

import static liedge.limacore.util.LimaItemUtil.*;

public final class LimaTechItems
{
    private LimaTechItems() {}

    private static final DeferredRegister.Items ITEMS = LimaTech.RESOURCES.deferredItems();
    private static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = LimaTech.RESOURCES.deferredRegister(Registries.CREATIVE_MODE_TAB);

    public static void initRegister(IEventBus bus)
    {
        ITEMS.register(bus);
        CREATIVE_TABS.register(bus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, ctx) -> InfiniteEnergyStorage.INFINITE_ENERGY_STORAGE, INFINITE_ENERGY_CARD);
    }

    // Creative mode tabs
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_TABS.register("main", id -> LimaCreativeTabGenerator.tabBuilder(id, LimaTechBlocks.getRegisteredBlocks(), ITEMS.getEntries())
            .icon(LimaTechItems.SUBMACHINE_GUN::toStack).build());

    // Mod tiers & rarities
    private static final Tier TITANIUM_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 6.25f, 2.1f, 14, () -> Ingredient.of(LimaTechItems.TITANIUM_INGOT));

    // Base mod materials
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> RAW_NIOBIUM = ITEMS.registerSimpleItem("raw_niobium");
    public static final DeferredItem<Item> NIOBIUM_INGOT = ITEMS.registerSimpleItem("niobium_ingot");
    public static final DeferredItem<Item> NIOBIUM_NUGGET = ITEMS.registerSimpleItem("niobium_nugget");
    
    // Titanium tools
    public static final DeferredItem<SwordItem> TITANIUM_SWORD = ITEMS.registerItem("titanium_sword", properties -> createSword(properties, TITANIUM_TIER, 3, -2.2f));
    public static final DeferredItem<ShovelItem> TITANIUM_SHOVEL = ITEMS.registerItem("titanium_shovel", properties -> createShovel(properties, TITANIUM_TIER, 1.5f, -2.8f));
    public static final DeferredItem<PickaxeItem> TITANIUM_PICKAXE = ITEMS.registerItem("titanium_pickaxe", properties -> createPickaxe(properties, TITANIUM_TIER, 1f, -2.6f));
    public static final DeferredItem<AxeItem> TITANIUM_AXE = ITEMS.registerItem("titanium_axe", properties -> createAxe(properties, TITANIUM_TIER, 6f, -2.9f));
    public static final DeferredItem<HoeItem> TITANIUM_HOE = ITEMS.registerItem("titanium_hoe", properties -> createHoe(properties, TITANIUM_TIER, -2f, -1f));
    public static final DeferredItem<ShearsItem> TITANIUM_SHEARS = ITEMS.registerItem("titanium_shears", properties -> createShears(properties, 476));

    // Components
    public static final DeferredItem<Item> COPPER_CIRCUIT = ITEMS.registerSimpleItem("copper_circuit");
    public static final DeferredItem<Item> GOLD_CIRCUIT = ITEMS.registerSimpleItem("gold_circuit");
    public static final DeferredItem<Item> NIOBIUM_CIRCUIT = ITEMS.registerSimpleItem("niobium_circuit");

    // Processed resources
    public static final DeferredItem<Item> DEEPSLATE_POWDER = ITEMS.registerSimpleItem("deepslate_powder");

    // Alloy ingots
    public static final DeferredItem<Item> SLATE_ALLOY_INGOT = ITEMS.registerSimpleItem("slate_alloy_ingot");
    public static final DeferredItem<Item> SLATE_ALLOY_NUGGET = ITEMS.registerSimpleItem("slate_alloy_nugget");

    // Tech salvage modules
    public static final DeferredItem<Item> EXPLOSIVES_WEAPON_TECH_SALVAGE = ITEMS.registerSimpleItem("explosives_weapon_tech_salvage", properties().rarity(Rarity.RARE));
    public static final DeferredItem<Item> TARGETING_TECH_SALVAGE = ITEMS.registerSimpleItem("targeting_tech_salvage", properties().rarity(Rarity.RARE));

    // LTX Weapons
    public static final DeferredItem<WeaponItem> SUBMACHINE_GUN = registerWeapon("submachine_gun", SMGWeaponItem::new);
    public static final DeferredItem<WeaponItem> SHOTGUN = registerWeapon("shotgun", ShotgunWeaponItem::new);
    public static final DeferredItem<WeaponItem> GRENADE_LAUNCHER = registerWeapon("grenade_launcher", GrenadeLauncherWeaponItem::new);
    public static final DeferredItem<WeaponItem> ROCKET_LAUNCHER = registerWeapon("rocket_launcher", RocketLauncherItem::new);
    public static final DeferredItem<WeaponItem> MAGNUM = registerWeapon("magnum", MagnumWeaponItem::new);

    // LTX Weapons Canisters
    public static final DeferredItem<Item> AUTO_AMMO_CANISTER = ITEMS.registerSimpleItem("auto_ammo_canister");
    public static final DeferredItem<Item> SPECIALIST_AMMO_CANISTER = ITEMS.registerSimpleItem("specialist_ammo_canister");
    public static final DeferredItem<Item> EXPLOSIVES_AMMO_CANISTER = ITEMS.registerSimpleItem("explosives_ammo_canister");
    public static final DeferredItem<Item> LEGENDARY_AMMO_CANISTER = ITEMS.registerSimpleItem("legendary_ammo_canister", properties().fireResistant().rarity(Rarity.EPIC));

    // Creative debug items
    public static final DeferredItem<Item> INFINITE_ENERGY_CARD = ITEMS.registerItem("infinite_energy_card", InfiniteEnergyItem::new, properties().rarity(Rarity.EPIC).stacksTo(1));

    private static DeferredItem<WeaponItem> registerWeapon(String name, Function<Item.Properties, ? extends WeaponItem> constructor)
    {
        return ITEMS.registerItem(name, constructor, properties().stacksTo(1).fireResistant().rarity(LimaTechRarities.ltxGearRarity()));
    }

    @Contract(value = " -> new", pure = true)
    private static Item.@NotNull Properties properties()
    {
        return new Item.Properties();
    }
}