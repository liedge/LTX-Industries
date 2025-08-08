package liedge.ltxindustries.integration.jei;

import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public final class ModuleSubtypeInterpreter<UE extends UpgradeBaseEntry<?>> implements ISubtypeInterpreter<ItemStack>
{
    private final DataComponentType<UE> componentType;

    public ModuleSubtypeInterpreter(Supplier<? extends DataComponentType<UE>> typeSupplier)
    {
        this.componentType = typeSupplier.get();
    }

    @Override
    public @Nullable Object getSubtypeData(ItemStack ingredient, UidContext context)
    {
        // Data components override hashCode and equals.
        return ingredient.get(componentType);
    }

    @Override
    public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context)
    {
        return "";
    }
}