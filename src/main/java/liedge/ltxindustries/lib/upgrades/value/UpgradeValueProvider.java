package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public interface UpgradeValueProvider
{
    Codec<UpgradeValueProvider> CODEC = LimaCoreCodecs.xorSubclassCodec(UpgradeDoubleValue.codec("ltxi:type"), NumberProviderWrapper.CODEC, UpgradeDoubleValue.class, NumberProviderWrapper.class);

    static UpgradeValueProvider of(NumberProvider provider)
    {
        return new NumberProviderWrapper(provider);
    }

    double get(LootContext context, int rank);
}