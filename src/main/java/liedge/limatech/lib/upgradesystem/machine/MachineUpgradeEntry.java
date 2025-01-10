package liedge.limatech.lib.upgradesystem.machine;

import com.mojang.serialization.Codec;
import liedge.limacore.util.LimaRegistryUtil;
import liedge.limatech.lib.upgradesystem.UpgradeBaseEntry;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MachineUpgradeEntry(Holder<MachineUpgrade> upgrade, int upgradeRank) implements UpgradeBaseEntry<MachineUpgrade>
{
    public static final Codec<MachineUpgradeEntry> CODEC = UpgradeBaseEntry.createCodec(MachineUpgrade.CODEC, MachineUpgradeEntry::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, MachineUpgradeEntry> STREAM_CODEC = UpgradeBaseEntry.createStreamCodec(MachineUpgrade.STREAM_CODEC, MachineUpgradeEntry::new);

    @Override
    public String toString()
    {
        return LimaRegistryUtil.getNonNullRegistryId(upgrade) + "(" + upgradeRank + ")";
    }
}