package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaLootUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Set;

public record TargetDistanceCurve(UpgradeValueProvider start, UpgradeValueProvider range, UpgradeValueProvider factor, boolean invert) implements UpgradeValueProvider
{
    public static final MapCodec<TargetDistanceCurve> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("start").forGetter(TargetDistanceCurve::start),
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("range").forGetter(TargetDistanceCurve::range),
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("factor").forGetter(TargetDistanceCurve::factor),
            Codec.BOOL.optionalFieldOf("invert", false).forGetter(TargetDistanceCurve::invert))
            .apply(instance, TargetDistanceCurve::new));

    public static TargetDistanceCurve of(UpgradeValueProvider start, UpgradeValueProvider range, UpgradeValueProvider factor, boolean invert)
    {
        return new TargetDistanceCurve(start, range, factor, invert);
    }

    public static TargetDistanceCurve of(UpgradeValueProvider start, UpgradeValueProvider range, UpgradeValueProvider factor)
    {
        return of(start, range, factor, false);
    }

    @Override
    public double get(LootContext context, int upgradeRank)
    {
        Entity thisEntity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity attacker = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);

        if (thisEntity != null && attacker != null)
        {
            double minDistance = start.get(context, upgradeRank);
            double span = range.get(context, upgradeRank);

            if (minDistance >= 0 && span > 0)
            {
                double distance = Math.sqrt(thisEntity.distanceToSqr(attacker));
                double t = Math.clamp((distance - minDistance) / span, 0d, 1d);
                if (invert) t = 1d - t;

                return t * factor.get(context, upgradeRank);
            }
        }

        return 0;
    }

    @Override
    public MapCodec<? extends UpgradeValueProvider> codec()
    {
        return CODEC;
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return LimaLootUtil.joinReferencedParams(start, range, factor);
    }
}