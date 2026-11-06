package liedge.ltxindustries.util;

import liedge.ltxindustries.blockentity.base.BlockIOConfiguration;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandlerUtil;
import net.neoforged.neoforge.transfer.resource.Resource;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public final class SidedResourceUtil
{
    private SidedResourceUtil() { }

    public static <T extends Resource> void pullResources(@Nullable BlockIOConfiguration configuration,
                                                          Direction facing,
                                                          @Nullable ResourceHandler<T> blockInventory,
                                                          int amount, Predicate<T> filter,
                                                          Function<Direction, @Nullable ResourceHandler<T>> neighborAccessor)
    {
        if (configuration != null && configuration.autoInput())
        {
            for (var entry : configuration)
            {
                if (entry.getValue().allowsInput())
                {
                    ResourceHandler<T> neighborInventory = neighborAccessor.apply(entry.getKey().resolveAbsoluteSide(facing));
                    ResourceHandlerUtil.moveStacking(neighborInventory, blockInventory, filter, amount, null);
                }
            }
        }
    }

    public static <T extends Resource> void pushResources(@Nullable BlockIOConfiguration configuration,
                                                          Direction facing,
                                                          @Nullable ResourceHandler<T> blockInventory,
                                                          int amount,
                                                          Predicate<T> filter,
                                                          Function<Direction, @Nullable ResourceHandler<T>> neighborAccessor)
    {
        if (configuration != null && configuration.autoOutput())
        {
            for (var entry : configuration)
            {
                if (entry.getValue().allowsOutput())
                {
                    ResourceHandler<T> neighborInventory = neighborAccessor.apply(entry.getKey().resolveAbsoluteSide(facing));
                    ResourceHandlerUtil.moveStacking(blockInventory, neighborInventory, filter, amount, null);
                }
            }
        }
    }

    public static void pushEnergy(@Nullable BlockIOConfiguration configuration,
                                  Direction facing,
                                  @Nullable EnergyHandler blockEnergy,
                                  int amount,
                                  Function<Direction, @Nullable EnergyHandler> neighborAccessor)
    {
        if (configuration != null && configuration.autoOutput())
        {
            for (var entry : configuration)
            {
                if (entry.getValue().allowsOutput())
                {
                    EnergyHandler neighborEnergy = neighborAccessor.apply(entry.getKey().resolveAbsoluteSide(facing));
                    EnergyHandlerUtil.move(blockEnergy, neighborEnergy, amount, null);
                }
            }
        }
    }
}