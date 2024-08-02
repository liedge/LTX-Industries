package liedge.limatech.registry;

import liedge.limacore.inventory.menu.BlockEntityMenuType;
import liedge.limacore.inventory.menu.LimaMenuType;
import liedge.limatech.LimaTech;
import liedge.limatech.blockentity.FabricatorBlockEntity;
import liedge.limatech.menu.FabricatorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class LimaTechMenus
{
    private LimaTechMenus() {}

    private static final DeferredRegister<MenuType<?>> TYPES = LimaTech.RESOURCES.deferredRegister(Registries.MENU);

    public static void initRegister(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static final Supplier<LimaMenuType<FabricatorBlockEntity, FabricatorMenu>> FABRICATOR = TYPES.register("fabricator", id -> new BlockEntityMenuType<>(id, FabricatorBlockEntity.class, FabricatorMenu::new));
}