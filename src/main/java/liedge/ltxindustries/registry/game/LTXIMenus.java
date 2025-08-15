package liedge.ltxindustries.registry.game;

import liedge.limacore.menu.BlockEntityMenuType;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.*;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.menu.*;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
import liedge.ltxindustries.menu.layout.RecipeMenuLayout;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class LTXIMenus
{
    private LTXIMenus() {}

    private static final DeferredRegister<MenuType<?>> TYPES = LTXIndustries.RESOURCES.deferredRegister(Registries.MENU);

    public static void register(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final DeferredHolder<MenuType<?>, IOControllerMenu.MenuType> MACHINE_IO_CONTROL = TYPES.register("machine_io_control", IOControllerMenu.MenuType::new);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<LTXIMachineBlockEntity, MachineUpgradeMenu>> MACHINE_UPGRADES = TYPES.register("machine_upgrades", id -> BlockEntityMenuType.create(id, LTXIMachineBlockEntity.class, MachineUpgradeMenu::new));

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<BaseESABlockEntity, EnergyStorageArrayMenu>> ENERGY_STORAGE_ARRAY = TYPES.register(LTXICommonIds.ID_ENERGY_STORAGE_ARRAY, () -> BlockEntityMenuType.create(BaseESABlockEntity.class, EnergyStorageArrayMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalFurnaceBlockEntity, RecipeLayoutMenu<DigitalFurnaceBlockEntity>>> DIGITAL_FURNACE = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalSmokerBlockEntity, RecipeLayoutMenu<DigitalSmokerBlockEntity>>> DIGITAL_SMOKER = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_SMOKER, DigitalSmokerBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalBlastFurnaceBlockEntity, RecipeLayoutMenu<DigitalBlastFurnaceBlockEntity>>> DIGITAL_BLAST_FURNACE = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_BLAST_FURNACE, DigitalBlastFurnaceBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<GrinderBlockEntity, RecipeLayoutMenu<GrinderBlockEntity>>> GRINDER = registerLayoutRecipeMenu(LTXICommonIds.ID_GRINDER, GrinderBlockEntity.class, RecipeLayouts.GRINDER);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MaterialFusingChamberBlockEntity, RecipeLayoutMenu<MaterialFusingChamberBlockEntity>>> MATERIAL_FUSING_CHAMBER = registerLayoutRecipeMenu(LTXICommonIds.ID_MATERIAL_FUSING_CHAMBER, MaterialFusingChamberBlockEntity.class, RecipeLayouts.MATERIAL_FUSING_CHAMBER);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<ElectroCentrifugeBlockEntity, RecipeLayoutMenu<ElectroCentrifugeBlockEntity>>> ELECTROCENTRIFUGE = registerLayoutRecipeMenu(LTXICommonIds.ID_ELECTROCENTRIFUGE, ElectroCentrifugeBlockEntity.class, RecipeLayouts.ELECTROCENTRIFUGE);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MixerBlockEntity, RecipeLayoutMenu<MixerBlockEntity>>> MIXER = registerLayoutRecipeMenu(LTXICommonIds.ID_MIXER, MixerBlockEntity.class, RecipeLayouts.MIXER);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<ChemLabBlockEntity, RecipeLayoutMenu<ChemLabBlockEntity>>> CHEM_LAB = registerLayoutRecipeMenu(LTXICommonIds.ID_CHEM_LAB, ChemLabBlockEntity.class, RecipeLayouts.CHEM_LAB);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register(LTXICommonIds.ID_FABRICATOR, () -> BlockEntityMenuType.create(FabricatorBlockEntity.class, FabricatorMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<AutoFabricatorBlockEntity, AutoFabricatorMenu>> AUTO_FABRICATOR = TYPES.register(LTXICommonIds.ID_AUTO_FABRICATOR, () -> BlockEntityMenuType.create(AutoFabricatorBlockEntity.class, AutoFabricatorMenu::new));

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MolecularReconstructorBlockEntity, MolecularReconstructorMenu>> MOLECULAR_RECONSTRUCTOR = TYPES.register(LTXICommonIds.ID_MOLECULAR_RECONSTRUCTOR, () -> BlockEntityMenuType.create(MolecularReconstructorBlockEntity.class, MolecularReconstructorMenu::new));

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<EquipmentUpgradeStationBlockEntity, EquipmentUpgradeStationMenu>> EQUIPMENT_UPGRADE_STATION = TYPES.register(LTXICommonIds.ID_EQUIPMENT_UPGRADE_STATION, () -> BlockEntityMenuType.create(EquipmentUpgradeStationBlockEntity.class, EquipmentUpgradeStationMenu::new));

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<RocketTurretBlockEntity, TurretMenu<RocketTurretBlockEntity>>> ROCKET_TURRET = registerTurret(LTXICommonIds.ID_ROCKET_TURRET, RocketTurretBlockEntity.class);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<RailgunTurretBlockEntity, TurretMenu<RailgunTurretBlockEntity>>> RAILGUN_TURRET = registerTurret(LTXICommonIds.ID_RAILGUN_TURRET, RailgunTurretBlockEntity.class);

    private static <BE extends StateBlockRecipeMachineBlockEntity<?, ?>> DeferredHolder<MenuType<?>, BlockEntityMenuType<BE, RecipeLayoutMenu<BE>>> registerLayoutRecipeMenu(String name, Class<BE> beClass, RecipeMenuLayout layout)
    {
        //noinspection RedundantTypeArguments
        return TYPES.register(name, () -> BlockEntityMenuType.<BE, RecipeLayoutMenu<BE>>create(beClass, (type, containerId, inventory, menuContext) -> new RecipeLayoutMenu<>(type, containerId, inventory, menuContext, layout)));
    }

    private static <BE extends BaseTurretBlockEntity> DeferredHolder<MenuType<?>, BlockEntityMenuType<BE, TurretMenu<BE>>> registerTurret(String name, Class<BE> beClass)
    {
        return TYPES.register(name, id -> BlockEntityMenuType.<BE, TurretMenu<BE>>create(id, beClass, TurretMenu::new));
    }
}