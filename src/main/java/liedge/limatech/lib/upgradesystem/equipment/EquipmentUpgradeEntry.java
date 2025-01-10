package liedge.limatech.lib.upgradesystem.equipment;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgradesystem.UpgradeBaseEntry;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record EquipmentUpgradeEntry(Holder<EquipmentUpgrade> upgrade, int upgradeRank) implements UpgradeBaseEntry<EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgradeEntry> CODEC = UpgradeBaseEntry.createCodec(EquipmentUpgrade.CODEC, EquipmentUpgradeEntry::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentUpgradeEntry> STREAM_CODEC = UpgradeBaseEntry.createStreamCodec(EquipmentUpgrade.STREAM_CODEC, EquipmentUpgradeEntry::new);

    public static @Nullable EquipmentUpgradeEntry getFromItem(ItemStack stack)
    {
        return stack.get(LimaTechDataComponents.EQUIPMENT_UPGRADE_ENTRY);
    }

    @Override
    public String toString()
    {
        return LimaRegistryUtil.getNonNullRegistryId(upgrade) + "(" + upgradeRank + ")";
    }
}