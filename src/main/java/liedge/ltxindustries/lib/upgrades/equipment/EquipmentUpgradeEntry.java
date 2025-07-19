package liedge.ltxindustries.lib.upgrades.equipment;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.ltxindustries.lib.upgrades.UpgradeBaseEntry;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record EquipmentUpgradeEntry(Holder<EquipmentUpgrade> upgrade, int upgradeRank) implements UpgradeBaseEntry<EquipmentUpgrade>
{
    public static final Codec<EquipmentUpgradeEntry> CODEC = UpgradeBaseEntry.createCodec(EquipmentUpgrade.CODEC, EquipmentUpgradeEntry::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, EquipmentUpgradeEntry> STREAM_CODEC = UpgradeBaseEntry.createStreamCodec(EquipmentUpgrade.STREAM_CODEC, EquipmentUpgradeEntry::new);

    @Override
    public String toString()
    {
        return LimaRegistryUtil.getNonNullRegistryId(upgrade) + "(" + upgradeRank + ")";
    }
}