package liedge.limatech.registry;

import liedge.limatech.LimaTech;
import liedge.limatech.item.*;
import liedge.limatech.item.weapon.*;
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

import java.util.Collection;
import java.util.function.Function;

import static liedge.limacore.util.LimaItemUtil.*;
import static liedge.limatech.item.LimaTechRarities.ltxGearRarity;

public final class LimaTechItems
{
    private LimaTechItems() {}

    private static final DeferredRegister.Items ITEMS = LimaTech.RESOURCES.deferredItems();

    public static void initRegister(IEventBus bus)
    {
        ITEMS.register(bus);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, $) -> EnergyHolderItem.createEnergyAccess(stack), SUBMACHINE_GUN, SHOTGUN, GRENADE_LAUNCHER, ROCKET_LAUNCHER, MAGNUM);
    }

    static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems()
    {
        return ITEMS.getEntries();
    }

    // Mod tiers & rarities
    private static final Tier TITANIUM_TIER = new SimpleTier(BlockTags.INCORRECT_FOR_IRON_TOOL, 500, 6.25f, 2.1f, 14, () -> Ingredient.of(LimaTechItems.TITANIUM_INGOT));

    // Base mod materials
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> RAW_NIOBIUM = ITEMS.registerSimpleItem("raw_niobium");
    public static final DeferredItem<Item> NIOBIUM_INGOT = ITEMS.registerSimpleItem("niobium_ingot");
    public static final DeferredItem<Item> NIOBIUM_NUGGET = ITEMS.registerSimpleItem("niobium_nugget");

    // Pigments
    public static final DeferredItem<Item> WHITE_PIGMENT = ITEMS.registerSimpleItem("white_pigment");
    public static final DeferredItem<Item> LIME_PIGMENT = ITEMS.registerSimpleItem("lime_pigment");
    
    // Titanium tools
    public static final DeferredItem<SwordItem> TITANIUM_SWORD = ITEMS.registerItem("titanium_sword", properties -> createSword(properties, TITANIUM_TIER, 3, -2.2f));
    public static final DeferredItem<ShovelItem> TITANIUM_SHOVEL = ITEMS.registerItem("titanium_shovel", properties -> createShovel(properties, TITANIUM_TIER, 1.5f, -2.8f));
    public static final DeferredItem<PickaxeItem> TITANIUM_PICKAXE = ITEMS.registerItem("titanium_pickaxe", properties -> createPickaxe(properties, TITANIUM_TIER, 1f, -2.6f));
    public static final DeferredItem<AxeItem> TITANIUM_AXE = ITEMS.registerItem("titanium_axe", properties -> createAxe(properties, TITANIUM_TIER, 6f, -2.9f));
    public static final DeferredItem<HoeItem> TITANIUM_HOE = ITEMS.registerItem("titanium_hoe", properties -> createHoe(properties, TITANIUM_TIER, -2f, -1f));
    public static final DeferredItem<ShearsItem> TITANIUM_SHEARS = ITEMS.registerItem("titanium_shears", properties -> createShears(properties, 476));

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

    // Components
    public static final DeferredItem<Item> COPPER_CIRCUIT = ITEMS.registerSimpleItem("copper_circuit");
    public static final DeferredItem<Item> GOLD_CIRCUIT = ITEMS.registerSimpleItem("gold_circuit");
    public static final DeferredItem<Item> NIOBIUM_CIRCUIT = ITEMS.registerSimpleItem("niobium_circuit");

    // General tools
    public static final DeferredItem<MachineWrenchItem> MACHINE_WRENCH = ITEMS.registerItem("machine_wrench", MachineWrenchItem::new, properties().stacksTo(1).rarity(ltxGearRarity()));

    // Processed resources
    public static final DeferredItem<Item> DEEPSLATE_POWDER = ITEMS.registerSimpleItem("deepslate_powder");

    // Alloy ingots
    public static final DeferredItem<Item> SLATE_ALLOY_INGOT = ITEMS.registerSimpleItem("slate_alloy_ingot");
    public static final DeferredItem<Item> SLATE_ALLOY_NUGGET = ITEMS.registerSimpleItem("slate_alloy_nugget");

    // Tech salvage modules
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_WEAPON_TECH_SALVAGE = ITEMS.registerItem("explosives_weapon_tech_salvage", SimpleHintItem::new, properties().rarity(Rarity.RARE));
    public static final DeferredItem<SimpleHintItem> TARGETING_TECH_SALVAGE = ITEMS.registerItem("targeting_tech_salvage", SimpleHintItem::new, properties().rarity(Rarity.RARE));

    // Upgrade
    public static final DeferredItem<EquipmentUpgradeItem> EQUIPMENT_UPGRADE_ITEM = ITEMS.registerItem("equipment_upgrade", EquipmentUpgradeItem::new, properties().rarity(ltxGearRarity()).stacksTo(1));

    // LTX Weapons
    public static final DeferredItem<WeaponItem> SUBMACHINE_GUN = registerWeapon("submachine_gun", SMGWeaponItem::new);
    public static final DeferredItem<WeaponItem> SHOTGUN = registerWeapon("shotgun", ShotgunWeaponItem::new);
    public static final DeferredItem<WeaponItem> GRENADE_LAUNCHER = registerWeapon("grenade_launcher", GrenadeLauncherWeaponItem::new);
    public static final DeferredItem<WeaponItem> ROCKET_LAUNCHER = registerWeapon("rocket_launcher", RocketLauncherItem::new);
    public static final DeferredItem<WeaponItem> MAGNUM = registerWeapon("magnum", MagnumWeaponItem::new);

    // LTX Weapons Canisters
    public static final DeferredItem<SimpleHintItem> AUTO_AMMO_CANISTER = ITEMS.registerItem("auto_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> SPECIALIST_AMMO_CANISTER = ITEMS.registerItem("specialist_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_AMMO_CANISTER = ITEMS.registerItem("explosives_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> MAGNUM_AMMO_CANISTER = ITEMS.registerItem("magnum_ammo_canister", SimpleHintItem::new);

    private static DeferredItem<WeaponItem> registerWeapon(String name, Function<Item.Properties, ? extends WeaponItem> constructor)
    {
        return ITEMS.registerItem(name, constructor, properties().stacksTo(1).fireResistant().rarity(ltxGearRarity()));
    }

    @Contract(value = " -> new", pure = true)
    private static Item.@NotNull Properties properties()
    {
        return new Item.Properties();
    }
}