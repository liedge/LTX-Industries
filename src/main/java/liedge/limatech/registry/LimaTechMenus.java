package liedge.limatech.registry;

import liedge.limacore.inventory.menu.BlockEntityMenuType;
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

    public static final DeferredHolder<MenuType<?>, MachineIOControlMenu.MenuType> MACHINE_IO_CONTROL = TYPES.register("machine_io_control", MachineIOControlMenu.MenuType::new);
    public static final DeferredHolder<MenuType<?>, LimaMenuType<EnergyStorageArrayBlockEntity, EnergyStorageArrayMenu>> ENERGY_STORAGE_ARRAY = TYPES.register("energy_storage_array", id -> BlockEntityMenuType.create(id, EnergyStorageArrayBlockEntity.class, EnergyStorageArrayMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<DigitalFurnaceBlockEntity, DigitalFurnaceMenu>> DIGITAL_FURNACE = TYPES.register("digital_furnace", id -> BlockEntityMenuType.create(id, DigitalFurnaceBlockEntity.class, DigitalFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<GrinderBlockEntity, GrinderMenu>> GRINDER = TYPES.register("grinder", id -> BlockEntityMenuType.create(id, GrinderBlockEntity.class, GrinderMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<RecomposerBlockEntity, RecomposerMenu>> RECOMPOSER = TYPES.register("recomposer", id -> BlockEntityMenuType.create(id, RecomposerBlockEntity.class, RecomposerMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<MaterialFusingChamberBlockEntity, MaterialFusingChamberMenu>> MATERIAL_FUSING_CHAMBER = TYPES.register("material_fusing_chamber", id -> BlockEntityMenuType.create(id, MaterialFusingChamberBlockEntity.class, MaterialFusingChamberMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register("fabricator", id -> BlockEntityMenuType.create(id, FabricatorBlockEntity.class, FabricatorMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<EquipmentModTableBlockEntity, EquipmentModTableMenu>> EQUIPMENT_MOD_TABLE = TYPES.register("equipment_mod_table", id -> BlockEntityMenuType.create(id, EquipmentModTableBlockEntity.class, EquipmentModTableMenu::new));
}