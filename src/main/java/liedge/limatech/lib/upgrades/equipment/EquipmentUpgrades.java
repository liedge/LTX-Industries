package liedge.limatech.lib.upgrades.equipment;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.limatech.lib.upgrades.UpgradesContainerBase;
import liedge.limatech.lib.upgrades.MutableUpgradesContainer;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class EquipmentUpgrades extends UpgradesContainerBase<Item, EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgrades> CODEC = createCodec(EquipmentUpgrade.CODEC, EquipmentUpgrades::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentUpgrades> STREAM_CODEC = createStreamCodec(EquipmentUpgrade.STREAM_CODEC, EquipmentUpgrades::new);
    public static final EquipmentUpgrades EMPTY = new EquipmentUpgrades(new Object2IntOpenHashMap<>());

    public static MutableUpgradesContainer<EquipmentUpgrade, EquipmentUpgrades> builder()
    {
        return new MutableUpgradesContainer<>(EquipmentUpgrades::new);
    }

    private EquipmentUpgrades(Object2IntMap<Holder<EquipmentUpgrade>> map)
    {
        super(map);
    }

    public boolean canInstallUpgrade(ItemStack equipmentItem, EquipmentUpgradeEntry entry)
    {
        return canInstallUpgrade(equipmentItem.getItemHolder(), entry);
    }

    @Override
    public MutableUpgradesContainer<EquipmentUpgrade, EquipmentUpgrades> toMutableContainer()
    {
        return new MutableUpgradesContainer<>(this, EquipmentUpgrades::new);
    }
}