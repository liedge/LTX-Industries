package liedge.limatech.registry;

import liedge.limacore.inventory.menu.BlockEntityMenuType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.blockentity.GrinderBlockEntity;
import liedge.limatech.blockentity.MaterialFusingChamberBlockEntity;
import liedge.limatech.menu.FabricatorMenu;
import liedge.limatech.menu.GrinderMenu;
import liedge.limatech.menu.MaterialFusingChamberMenu;
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

    public static final DeferredHolder<MenuType<?>, LimaMenuType<GrinderBlockEntity, GrinderMenu>> GRINDER = TYPES.register("grinder", id -> new BlockEntityMenuType<>(id, GrinderBlockEntity.class, GrinderMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<MaterialFusingChamberBlockEntity, MaterialFusingChamberMenu>> MATERIAL_FUSING_CHAMBER = TYPES.register("material_fusing_chamber", id -> new BlockEntityMenuType<>(id, MaterialFusingChamberBlockEntity.class, MaterialFusingChamberMenu::new));
    public static final DeferredHolder<MenuType<?>, LimaMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register("fabricator", id -> new BlockEntityMenuType<>(id, FabricatorBlockEntity.class, FabricatorMenu::new));
}