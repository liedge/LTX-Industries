package liedge.limatech.registry;

import liedge.limacore.inventory.menu.BlockEntityAccessMenuType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.*;
import liedge.limatech.menu.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LimaTechMenus
{
    private LimaTechMenus() {}

    private static final DeferredRegister<MenuType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.MENU);

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<MenuType<?>, IOControllerMenu.MenuType> MACHINE_IO_CONTROL = TYPES.register("machine_io_control", IOControllerMenu.MenuType::new);
    public static final DeferredHolder<MenuType<?>, LimaMenuType<UpgradableMachineBlockEntity, MachineUpgradeMenu>> MACHINE_UPGRADES = TYPES.register("machine_upgrades", id -> BlockEntityAccessMenuType.create(id, UpgradableMachineBlockEntity.class, MachineUpgradeMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<BaseESABlockEntity, EnergyStorageArrayMenu>> ENERGY_STORAGE_ARRAY = TYPES.register("energy_storage_array", id -> BlockEntityAccessMenuType.create(id, BaseESABlockEntity.class, EnergyStorageArrayMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<DigitalFurnaceBlockEntity, DigitalFurnaceMenu>> DIGITAL_FURNACE = TYPES.register("digital_furnace", id -> BlockEntityAccessMenuType.create(id, DigitalFurnaceBlockEntity.class, DigitalFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<GrinderBlockEntity, GrinderMenu>> GRINDER = TYPES.register("grinder", id -> BlockEntityAccessMenuType.create(id, GrinderBlockEntity.class, GrinderMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<RecomposerBlockEntity, RecomposerMenu>> RECOMPOSER = TYPES.register("recomposer", id -> BlockEntityAccessMenuType.create(id, RecomposerBlockEntity.class, RecomposerMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<MaterialFusingChamberBlockEntity, MaterialFusingChamberMenu>> MATERIAL_FUSING_CHAMBER = TYPES.register("material_fusing_chamber", id -> BlockEntityAccessMenuType.create(id, MaterialFusingChamberBlockEntity.class, MaterialFusingChamberMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register("fabricator", id -> BlockEntityAccessMenuType.create(id, FabricatorBlockEntity.class, FabricatorMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<EquipmentUpgradeStationBlockEntity, EquipmentUpgradeStationMenu>> EQUIPMENT_UPGRADE_STATION = TYPES.register("equipment_upgrade_station", id -> BlockEntityAccessMenuType.create(id, EquipmentUpgradeStationBlockEntity.class, EquipmentUpgradeStationMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<RocketTurretBlockEntity, RocketTurretMenu>> ROCKET_TURRET = TYPES.register("rocket_turret", id -> BlockEntityAccessMenuType.create(id, RocketTurretBlockEntity.class, RocketTurretMenu::new));
}