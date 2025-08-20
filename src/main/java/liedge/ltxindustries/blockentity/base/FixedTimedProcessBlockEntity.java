package liedge.ltxindustries.blockentity.base;

import liedge.ltxindustries.lib.upgrades.machine.MachineUpgrades;
import liedge.ltxindustries.registry.game.LTXIUpgradeEffectComponents;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.LootContext;

public interface FixedTimedProcessBlockEntity extends VariableTimedProcessBlockEntity
{
    static void applyUpgrades(FixedTimedProcessBlockEntity blockEntity, LootContext context, MachineUpgrades upgrades)
    {
        double newProcessingTime = upgrades.applyValue(LTXIUpgradeEffectComponents.TICKS_PER_OPERATION, context, blockEntity.getBaseTicksPerOperation());
        blockEntity.setTicksPerOperation(Math.max(0, Mth.floor(newProcessingTime)));
    }

    int getBaseTicksPerOperation();
}