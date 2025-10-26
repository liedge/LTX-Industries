package liedge.ltxindustries.registry.game;

import liedge.limacore.menu.BlockEntityMenuType;
import liedge.ltxindustries.LTXICommonIds;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.blockentity.*;
import liedge.ltxindustries.blockentity.base.RecipeModeHolderBlockEntity;
import liedge.ltxindustries.blockentity.template.BaseRecipeMachineBlockEntity;
import liedge.ltxindustries.blockentity.template.LTXIMachineBlockEntity;
import liedge.ltxindustries.menu.*;
import liedge.ltxindustries.menu.layout.RecipeLayout;
import liedge.ltxindustries.menu.layout.RecipeLayouts;
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

    public static final DeferredHolder<MenuType<?>, BlockIOConfigurationMenu.MenuType> BLOCK_IO_CONFIGURATION = TYPES.register("block_io_configuration", BlockIOConfigurationMenu.MenuType::new);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<LTXIMachineBlockEntity, MachineUpgradeMenu>> MACHINE_UPGRADES = TYPES.register("machine_upgrades", id -> BlockEntityMenuType.create(id, LTXIMachineBlockEntity.class, MachineUpgradeMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<RecipeModeHolderBlockEntity, RecipeModeMenu>> RECIPE_MODE_SELECT = TYPES.register("recipe_modes", () -> BlockEntityMenuType.create(RecipeModeHolderBlockEntity.class, RecipeModeMenu::new));

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<EquipmentUpgradeStationBlockEntity, EquipmentUpgradeStationMenu>> EQUIPMENT_UPGRADE_STATION = TYPES.register(LTXICommonIds.ID_EQUIPMENT_UPGRADE_STATION, () -> BlockEntityMenuType.create(EquipmentUpgradeStationBlockEntity.class, EquipmentUpgradeStationMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<BaseECABlockEntity, EnergyCellArrayMenu>> ENERGY_CELL_ARRAY = TYPES.register(LTXICommonIds.ID_ENERGY_CELL_ARRAY, () -> BlockEntityMenuType.create(BaseECABlockEntity.class, EnergyCellArrayMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalFurnaceBlockEntity, RecipeLayoutMenu<DigitalFurnaceBlockEntity>>> DIGITAL_FURNACE = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_FURNACE, DigitalFurnaceBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalSmokerBlockEntity, RecipeLayoutMenu<DigitalSmokerBlockEntity>>> DIGITAL_SMOKER = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_SMOKER, DigitalSmokerBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalBlastFurnaceBlockEntity, RecipeLayoutMenu<DigitalBlastFurnaceBlockEntity>>> DIGITAL_BLAST_FURNACE = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_BLAST_FURNACE, DigitalBlastFurnaceBlockEntity.class, RecipeLayouts.COOKING_LAYOUT);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<GrinderBlockEntity, RecipeLayoutMenu<GrinderBlockEntity>>> GRINDER = registerLayoutRecipeMenu(LTXICommonIds.ID_GRINDER, GrinderBlockEntity.class, RecipeLayouts.GRINDING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MaterialFusingChamberBlockEntity, RecipeLayoutMenu<MaterialFusingChamberBlockEntity>>> MATERIAL_FUSING_CHAMBER = registerLayoutRecipeMenu(LTXICommonIds.ID_MATERIAL_FUSING_CHAMBER, MaterialFusingChamberBlockEntity.class, RecipeLayouts.FUSING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<ElectroCentrifugeBlockEntity, RecipeLayoutMenu<ElectroCentrifugeBlockEntity>>> ELECTROCENTRIFUGE = registerLayoutRecipeMenu(LTXICommonIds.ID_ELECTROCENTRIFUGE, ElectroCentrifugeBlockEntity.class, RecipeLayouts.ELECTRO_CENTRIFUGING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MixerBlockEntity, RecipeLayoutMenu<MixerBlockEntity>>> MIXER = registerLayoutRecipeMenu(LTXICommonIds.ID_MIXER, MixerBlockEntity.class, RecipeLayouts.MIXING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<VoltaicInjectorBlockEntity, RecipeLayoutMenu<VoltaicInjectorBlockEntity>>> VOLTAIC_INJECTOR = registerLayoutRecipeMenu(LTXICommonIds.ID_VOLTAIC_INJECTOR, VoltaicInjectorBlockEntity.class, RecipeLayouts.ENERGIZING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<ChemLabBlockEntity, RecipeLayoutMenu<ChemLabBlockEntity>>> CHEM_LAB = registerLayoutRecipeMenu(LTXICommonIds.ID_CHEM_LAB, ChemLabBlockEntity.class, RecipeLayouts.CHEMICAL_REACTING);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register(LTXICommonIds.ID_FABRICATOR, () -> BlockEntityMenuType.create(FabricatorBlockEntity.class, FabricatorMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<AutoFabricatorBlockEntity, AutoFabricatorMenu>> AUTO_FABRICATOR = TYPES.register(LTXICommonIds.ID_AUTO_FABRICATOR, () -> BlockEntityMenuType.create(AutoFabricatorBlockEntity.class, AutoFabricatorMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<MolecularReconstructorBlockEntity, MolecularReconstructorMenu>> MOLECULAR_RECONSTRUCTOR = TYPES.register(LTXICommonIds.ID_MOLECULAR_RECONSTRUCTOR, () -> BlockEntityMenuType.create(MolecularReconstructorBlockEntity.class, MolecularReconstructorMenu::new));
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<DigitalGardenBlockEntity, RecipeLayoutMenu<DigitalGardenBlockEntity>>> DIGITAL_GARDEN = registerLayoutRecipeMenu(LTXICommonIds.ID_DIGITAL_GARDEN, DigitalGardenBlockEntity.class, RecipeLayouts.GARDEN_SIMULATING);

    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<RocketTurretBlockEntity, TurretMenu<RocketTurretBlockEntity>>> ROCKET_TURRET = registerTurret(LTXICommonIds.ID_ROCKET_TURRET, RocketTurretBlockEntity.class);
    public static final DeferredHolder<MenuType<?>, BlockEntityMenuType<RailgunTurretBlockEntity, TurretMenu<RailgunTurretBlockEntity>>> RAILGUN_TURRET = registerTurret(LTXICommonIds.ID_RAILGUN_TURRET, RailgunTurretBlockEntity.class);

    private static <BE extends BaseRecipeMachineBlockEntity<?, ?>> DeferredHolder<MenuType<?>, BlockEntityMenuType<BE, RecipeLayoutMenu<BE>>> registerLayoutRecipeMenu(String name, Class<BE> beClass, RecipeLayout layout)
    {
        //noinspection RedundantTypeArguments
        return TYPES.register(name, () -> BlockEntityMenuType.<BE, RecipeLayoutMenu<BE>>create(beClass, (type, containerId, inventory, menuContext) -> new RecipeLayoutMenu<>(type, containerId, inventory, menuContext, layout)));
    }

    private static <BE extends BaseTurretBlockEntity> DeferredHolder<MenuType<?>, BlockEntityMenuType<BE, TurretMenu<BE>>> registerTurret(String name, Class<BE> beClass)
    {
        return TYPES.register(name, id -> BlockEntityMenuType.<BE, TurretMenu<BE>>create(id, beClass, TurretMenu::new));
    }
}