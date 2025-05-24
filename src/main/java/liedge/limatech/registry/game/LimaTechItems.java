package liedge.limatech.registry.game;

import liedge.limatech.item.*;
import liedge.limatech.item.tool.*;
import liedge.limatech.item.weapon.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.function.Function;

import static liedge.limatech.LimaTech.RESOURCES;
import static liedge.limatech.item.LimaTechRarities.ltxGearRarity;

public final class LimaTechItems
{
    private LimaTechItems() {}

    private static final DeferredRegister.Items ITEMS = RESOURCES.deferredItems();

    public static void register(IEventBus bus)
    {
        ITEMS.register(bus);
        ITEMS.addAlias(RESOURCES.location("rocket_launcher_ammo"), RESOURCES.location("explosives_ammo_canister"));
        ITEMS.addAlias(RESOURCES.location("magnum_ammo_canister"), RESOURCES.location("heavy_ammo_canister"));
        bus.addListener(RegisterCapabilitiesEvent.class, LimaTechItems::registerCapabilities);
    }

    private static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerItem(Capabilities.EnergyStorage.ITEM, (stack, $) -> EnergyHolderItem.createEnergyAccess(stack),
                LTX_DRILL, LTX_SWORD, LTX_SHOVEL, LTX_AXE, LTX_HOE, LTX_SHEARS, LTX_BRUSH, LTX_FISHING_ROD, LTX_LIGHTER, LTX_WRENCH,
                SUBMACHINE_GUN, SHOTGUN, LINEAR_FUSION_RIFLE, GRENADE_LAUNCHER, ROCKET_LAUNCHER, MAGNUM);
    }

    static Collection<DeferredHolder<Item, ? extends Item>> getRegisteredItems()
    {
        return ITEMS.getEntries();
    }

    // Base mod materials
    public static final DeferredItem<Item> RAW_TITANIUM = ITEMS.registerSimpleItem("raw_titanium");
    public static final DeferredItem<Item> TITANIUM_INGOT = ITEMS.registerSimpleItem("titanium_ingot");
    public static final DeferredItem<Item> TITANIUM_NUGGET = ITEMS.registerSimpleItem("titanium_nugget");
    public static final DeferredItem<Item> RAW_NIOBIUM = ITEMS.registerSimpleItem("raw_niobium");
    public static final DeferredItem<Item> NIOBIUM_INGOT = ITEMS.registerSimpleItem("niobium_ingot");
    public static final DeferredItem<Item> NIOBIUM_NUGGET = ITEMS.registerSimpleItem("niobium_nugget");

    // Pigments
    public static final DeferredItem<Item> WHITE_PIGMENT = ITEMS.registerSimpleItem("white_pigment");
    public static final DeferredItem<Item> LIGHT_BLUE_PIGMENT = ITEMS.registerSimpleItem("light_blue_pigment");
    public static final DeferredItem<Item> LIME_PIGMENT = ITEMS.registerSimpleItem("lime_pigment");

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

    // LTX basic tools
    public static final DeferredItem<EnergyDrillItem> LTX_DRILL = registerLTXGear("ltx_drill", properties -> new EnergyDrillItem(properties, 5f, -2f));
    public static final DeferredItem<EnergySwordItem> LTX_SWORD = registerLTXGear("ltx_sword", properties -> new EnergySwordItem(properties, 9f, -1.5f));
    public static final DeferredItem<EnergyShovelItem> LTX_SHOVEL = registerLTXGear("ltx_shovel", properties -> new EnergyShovelItem(properties, 7.5f, -2.2f));
    public static final DeferredItem<EnergyAxeItem> LTX_AXE = registerLTXGear("ltx_axe", properties -> new EnergyAxeItem(properties, 12f, -2.8f));
    public static final DeferredItem<EnergyHoeItem> LTX_HOE = registerLTXGear("ltx_hoe", properties -> new EnergyHoeItem(properties, 3f, -1f));
    public static final DeferredItem<EnergyWrenchItem> LTX_WRENCH = registerLTXGear("ltx_wrench", EnergyWrenchItem::new);
    public static final DeferredItem<EnergyShearsItem> LTX_SHEARS = registerLTXGear("ltx_shears", EnergyShearsItem::new);
    public static final DeferredItem<EnergyBrushItem> LTX_BRUSH = registerLTXGear("ltx_brush", EnergyBrushItem::new);
    public static final DeferredItem<EnergyFishingRodItem> LTX_FISHING_ROD = registerLTXGear("ltx_fishing_rod", EnergyFishingRodItem::new);
    public static final DeferredItem<EnergyLighterItem> LTX_LIGHTER = registerLTXGear("ltx_lighter", EnergyLighterItem::new);

    // Processed resources
    public static final DeferredItem<Item> DEEPSLATE_POWDER = ITEMS.registerSimpleItem("deepslate_powder");

    // Alloy ingots
    public static final DeferredItem<Item> SLATE_ALLOY_INGOT = ITEMS.registerSimpleItem("slate_alloy_ingot");
    public static final DeferredItem<Item> BEDROCK_ALLOY_INGOT = ITEMS.registerSimpleItem("bedrock_alloy_ingot");
    public static final DeferredItem<Item> SLATE_ALLOY_NUGGET = ITEMS.registerSimpleItem("slate_alloy_nugget");

    // Tech salvage modules
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_WEAPON_TECH_SALVAGE = ITEMS.registerItem("explosives_weapon_tech_salvage", SimpleHintItem::new, properties().rarity(Rarity.RARE));
    public static final DeferredItem<SimpleHintItem> TARGETING_TECH_SALVAGE = ITEMS.registerItem("targeting_tech_salvage", SimpleHintItem::new, properties().rarity(Rarity.RARE));

    // Upgrade
    public static final DeferredItem<Item> EMPTY_UPGRADE_MODULE = ITEMS.registerSimpleItem("empty_upgrade_module");
    public static final DeferredItem<EquipmentUpgradeModuleItem> EQUIPMENT_UPGRADE_MODULE = ITEMS.registerItem("equipment_upgrade_module", EquipmentUpgradeModuleItem::new, properties().stacksTo(1));
    public static final DeferredItem<MachineUpgradeModuleItem> MACHINE_UPGRADE_MODULE = ITEMS.registerItem("machine_upgrade_module", MachineUpgradeModuleItem::new, properties().stacksTo(1));

    public static final DeferredItem<FabricationBlueprintItem> FABRICATION_BLUEPRINT = ITEMS.registerItem("fabrication_blueprint", FabricationBlueprintItem::new, properties().stacksTo(1));

    // LTX Weapons
    public static final DeferredItem<SMGWeaponItem> SUBMACHINE_GUN = registerLTXGear("submachine_gun", SMGWeaponItem::new);
    public static final DeferredItem<ShotgunWeaponItem> SHOTGUN = registerLTXGear("shotgun", ShotgunWeaponItem::new);
    public static final DeferredItem<GrenadeLauncherWeaponItem> GRENADE_LAUNCHER = registerLTXGear("grenade_launcher", GrenadeLauncherWeaponItem::new);
    public static final DeferredItem<LinearFusionWeaponItem> LINEAR_FUSION_RIFLE = registerLTXGear("linear_fusion_rifle", LinearFusionWeaponItem::new);
    public static final DeferredItem<RocketLauncherWeaponItem> ROCKET_LAUNCHER = registerLTXGear("rocket_launcher", RocketLauncherWeaponItem::new);
    public static final DeferredItem<MagnumWeaponItem> MAGNUM = registerLTXGear("magnum", MagnumWeaponItem::new);

    // LTX weapon ammo items
    public static final DeferredItem<SimpleHintItem> AUTO_AMMO_CANISTER = ITEMS.registerItem("auto_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> SPECIALIST_AMMO_CANISTER = ITEMS.registerItem("specialist_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> EXPLOSIVES_AMMO_CANISTER = ITEMS.registerItem("explosives_ammo_canister", SimpleHintItem::new);
    public static final DeferredItem<SimpleHintItem> HEAVY_AMMO_CANISTER = ITEMS.registerItem("heavy_ammo_canister", SimpleHintItem::new);

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