package liedge.limatech.lib.upgradesystem.machine.effect;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgradesystem.calculation.CompoundCalculation;
import liedge.limatech.lib.upgradesystem.calculation.EmptyCalculation;
import liedge.limatech.registry.LimaTechMachineUpgrades;
import net.minecraft.network.chat.Component;

import java.util.List;

public record ModifyEnergyStorageUpgradeEffect(CompoundCalculation capacityModifier, CompoundCalculation transferRateModifier) implements MachineUpgradeEffect
{
    public static final MapCodec<ModifyEnergyStorageUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CompoundCalculation.CODEC.optionalFieldOf("capacity_modifier", EmptyCalculation.empty()).forGetter(ModifyEnergyStorageUpgradeEffect::capacityModifier),
            CompoundCalculation.CODEC.optionalFieldOf("transfer_rate_modifier", EmptyCalculation.empty()).forGetter(ModifyEnergyStorageUpgradeEffect::transferRateModifier))
            .apply(instance, ModifyEnergyStorageUpgradeEffect::new));

    public static ModifyEnergyStorageUpgradeEffect modifyCapacityOnly(CompoundCalculation capacityModifier)
    {
        return new ModifyEnergyStorageUpgradeEffect(capacityModifier, EmptyCalculation.empty());
    }

    public static ModifyEnergyStorageUpgradeEffect modifyTransferRateOnly(CompoundCalculation transferRateModifier)
    {
        return new ModifyEnergyStorageUpgradeEffect(EmptyCalculation.empty(), transferRateModifier);
    }

    @Override
    public MachineUpgradeEffectType<?> getType()
    {
        return LimaTechMachineUpgrades.MODIFY_ENERGY_STORAGE.get();
    }

    @Override
    public void appendEffectTooltip(int upgradeRank, List<Component> lines)
    {
        if (!capacityModifier.isEmpty()) lines.add(LimaTechLang.ENERGY_CAPACITY_UPGRADE.translateArgs(capacityModifier.getTooltip(upgradeRank)));
        if (!transferRateModifier().isEmpty()) lines.add(LimaTechLang.ENERGY_TRANSFER_RATE_UPGRADE.translateArgs(transferRateModifier.getTooltip(upgradeRank)));
    }
}