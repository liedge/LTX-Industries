package liedge.ltxindustries.world.loot;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.ltxindustries.lib.upgrades.UpgradeContextKeys;
import liedge.ltxindustries.registry.game.LTXILootRegistries;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public record ContextKeyProvider(Target target) implements NumberProvider
{
    public static final MapCodec<ContextKeyProvider> CODEC = Target.CODEC.fieldOf("target").xmap(ContextKeyProvider::new, ContextKeyProvider::target);

    @Override
    public float getFloat(LootContext context)
    {
        return context.getParam(target.key).floatValue();
    }

    @Override
    public int getInt(LootContext context)
    {
        return context.getParam(target.key).intValue();
    }

    @Override
    public LootNumberProviderType getType()
    {
        return LTXILootRegistries.CONTEXT_VALUE_PROVIDER.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(target.key);
    }

    public enum Target implements StringRepresentable
    {
        UPGRADE_RANK("rank", UpgradeContextKeys.UPGRADE_RANK),
        DAMAGE_AMOUNT("damage", UpgradeContextKeys.DAMAGE);

        private static final Codec<Target> CODEC = LimaEnumCodec.create(Target.class);

        private final String name;
        private final LootContextParam<? extends Number> key;

        Target(String name, LootContextParam<? extends Number> key)
        {
            this.name = name;
            this.key = key;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }

        public LootContextParam<? extends Number> getKey()
        {
            return key;
        }
    }
}