package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import liedge.ltxindustries.lib.upgrades.UpgradeContextKeys;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record ContextKeyValue(Target target) implements UpgradeValueProvider
{
    public static final MapCodec<ContextKeyValue> CODEC = Target.CODEC.fieldOf("target").xmap(ContextKeyValue::new, ContextKeyValue::target);

    public static ContextKeyValue of(Target target)
    {
        return new ContextKeyValue(target);
    }

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        return context.getParam(target.key).doubleValue();
    }

    @Override
    public MapCodec<? extends UpgradeValueProvider> codec()
    {
        return CODEC;
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