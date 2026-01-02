package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Set;

record NumberProviderWrapper(NumberProvider value) implements UpgradeValueProvider
{
    static final MapCodec<NumberProviderWrapper> CODEC = NumberProviders.CODEC.fieldOf("value").xmap(NumberProviderWrapper::new, NumberProviderWrapper::value);

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        return value.getFloat(context);
    }

    @Override
    public MapCodec<? extends UpgradeValueProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return value.getReferencedContextParams();
    }
}