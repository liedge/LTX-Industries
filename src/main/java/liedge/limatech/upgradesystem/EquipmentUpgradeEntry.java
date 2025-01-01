package liedge.limatech.upgradesystem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record EquipmentUpgradeEntry(Holder<EquipmentUpgrade> upgrade, int upgradeRank)
{
    public static final Codec<EquipmentUpgradeEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            EquipmentUpgrade.CODEC.fieldOf("upgrade").forGetter(EquipmentUpgradeEntry::upgrade),
            EquipmentUpgrade.UPGRADE_RANK_CODEC.optionalFieldOf("rank", 1).forGetter(EquipmentUpgradeEntry::upgradeRank))
            .apply(instance, EquipmentUpgradeEntry::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentUpgradeEntry> STREAM_CODEC = StreamCodec.composite(
            EquipmentUpgrade.STREAM_CODEC, EquipmentUpgradeEntry::upgrade,
            EquipmentUpgrade.UPGRADE_RANK_STREAM_CODEC, EquipmentUpgradeEntry::upgradeRank,
            EquipmentUpgradeEntry::new);

    public static @Nullable EquipmentUpgradeEntry getFromItem(ItemStack stack)
    {
        return stack.get(LimaTechDataComponents.ITEM_UPGRADE_ENTRY);
    }

    @Override
    public String toString()
    {
        return LimaRegistryUtil.getNonNullRegistryId(upgrade) + "(" + upgradeRank + ")";
    }
}