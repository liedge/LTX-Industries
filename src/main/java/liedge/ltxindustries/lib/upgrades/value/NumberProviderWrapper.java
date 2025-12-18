package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
record NumberProviderWrapper(NumberProvider provider) implements UpgradeValueProvider
{
    private static final Codec<NumberProvider> BASE_CODEC = BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE
            .byNameCodec()
            .dispatch("minecraft:type", NumberProvider::getType, LootNumberProviderType::codec);
    static final Codec<NumberProviderWrapper> CODEC = BASE_CODEC.xmap(NumberProviderWrapper::new, NumberProviderWrapper::provider);

    @Override
    public double get(LootContext context, int rank)
    {
        return provider.getFloat(context);
    }
}