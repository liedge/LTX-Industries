package liedge.limatech.lib.upgradesystem.equipment;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.limatech.lib.upgradesystem.UpgradesContainerBase;
import liedge.limatech.lib.upgradesystem.UpgradesContainerBuilder;
import liedge.limatech.lib.upgradesystem.equipment.effect.EquipmentUpgradeEffect;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class EquipmentUpgrades extends UpgradesContainerBase<Item, EquipmentUpgradeEffect, EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgrades> CODEC = createCodec(EquipmentUpgrade.CODEC, EquipmentUpgrades::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentUpgrades> STREAM_CODEC = createStreamCodec(EquipmentUpgrade.STREAM_CODEC, EquipmentUpgrades::new);
    public static final EquipmentUpgrades EMPTY = new EquipmentUpgrades(new Object2IntOpenHashMap<>());

    public static UpgradesContainerBuilder<EquipmentUpgrade, EquipmentUpgrades> builder()
    {
        return new UpgradesContainerBuilder<>(EquipmentUpgrades::new);
    }

    private EquipmentUpgrades(Object2IntMap<Holder<EquipmentUpgrade>> map)
    {
        super(map);
    }

    public boolean canInstallUpgrade(ItemStack equipmentItem, Holder<EquipmentUpgrade> upgradeHolder)
    {
        return canInstallUpgrade(equipmentItem.getItemHolder(), upgradeHolder);
    }

    public UpgradesContainerBuilder<EquipmentUpgrade, EquipmentUpgrades> asBuilder()
    {
        return new UpgradesContainerBuilder<>(this, EquipmentUpgrades::new);
    }
}