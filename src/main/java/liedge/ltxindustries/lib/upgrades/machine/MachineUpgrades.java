package liedge.ltxindustries.lib.upgrades.machine;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import liedge.ltxindustries.blockentity.base.UpgradesHolderBlockEntity;
import liedge.ltxindustries.lib.upgrades.MutableUpgradesContainer;
import liedge.ltxindustries.lib.upgrades.UpgradesContainerBase;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Objects;

public final class MachineUpgrades extends UpgradesContainerBase<BlockEntityType<?>, MachineUpgrade>
{
    public static final Codec<MachineUpgrades> CODEC = createCodec(MachineUpgrade.CODEC, MachineUpgrades::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, MachineUpgrades> STREAM_CODEC = createStreamCodec(MachineUpgrade.STREAM_CODEC, MachineUpgrades::new);
    public static final MachineUpgrades EMPTY = new MachineUpgrades(new Object2IntOpenHashMap<>());

    public static MutableUpgradesContainer<MachineUpgrade, MachineUpgrades> builder()
    {
        return new MutableUpgradesContainer<>(MachineUpgrades::new);
    }

    private MachineUpgrades(Object2IntMap<Holder<MachineUpgrade>> internalMap)
    {
        super(internalMap);
    }

    public boolean canInstallUpgrade(UpgradesHolderBlockEntity blockEntity, MachineUpgradeEntry entry)
    {
        Holder<BlockEntityType<?>> contextHolder = Objects.requireNonNull(blockEntity.getAsLimaBlockEntity().getType().builtInRegistryHolder());
        return canInstallUpgrade(contextHolder, entry);
    }

    @Override
    public MutableUpgradesContainer<MachineUpgrade, MachineUpgrades> toMutableContainer()
    {
        return new MutableUpgradesContainer<>(this, MachineUpgrades::new);
    }
}