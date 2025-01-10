package liedge.limatech.lib.upgradesystem.machine.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.blockentity.UpgradableMachineBlockEntity;
import liedge.limatech.lib.upgradesystem.UpgradeEffectBase;
import liedge.limatech.registry.LimaTechRegistries;

public interface MachineUpgradeEffect extends UpgradeEffectBase
{
    Codec<MachineUpgradeEffect> CODEC = LimaTechRegistries.MACHINE_UPGRADE_EFFECT_TYPE.byNameCodec().dispatch(MachineUpgradeEffect::getType, MachineUpgradeEffectType::codec);

    MachineUpgradeEffectType<?> getType();

    /**
     * Called whenever the machine's upgrades are reloaded. This happens whenever the upgrades are modified (add/removed), replaced, and when the block entity is loaded. Only called on the server!
     * @param blockEntity The block entity this upgrade is installed on.
     * @param upgradeRank The rank of the upgrade.
     */
    default void onUpgradeReload(UpgradableMachineBlockEntity blockEntity, int upgradeRank) {}
}