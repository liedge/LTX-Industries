package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.function.Function;

public interface UpgradeValueProvider extends LootContextUser
{
    Codec<UpgradeValueProvider> DIRECT_CODEC = Codec.lazyInitialized(() -> LimaCoreCodecs.dispatchWithInline(UpgradeValueTypes.MASTER_REGISTRY_CODEC, ConstantDouble.class, ConstantDouble.INLINE_CODEC, UpgradeValueProvider::codec, Function.identity()));

    static UpgradeValueProvider wrap(NumberProvider provider)
    {
        return new NumberProviderWrapper(provider);
    }

    double get(LootContext context, int upgradeRank);

    MapCodec<? extends UpgradeValueProvider> codec();
}