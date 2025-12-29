package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public record ApplyInArea(EntityUpgradeEffect child, float horizontalRadius, float verticalRadius) implements EntityUpgradeEffect
{
    private static DataResult<ApplyInArea> validate(ApplyInArea value)
    {
        if (value.child instanceof ApplyInArea)
            return DataResult.error(() -> "Recursive area application effect.");
        else
            return DataResult.success(value);
    }

    public static final MapCodec<ApplyInArea> CODEC = RecordCodecBuilder.<ApplyInArea>mapCodec(instance -> instance.group(
            EntityUpgradeEffect.DIRECT_CODEC.fieldOf("child").forGetter(ApplyInArea::child),
            LimaCoreCodecs.floatOpenStartRange(0f, 128f).fieldOf("horizontal_radius").forGetter(ApplyInArea::horizontalRadius),
            LimaCoreCodecs.floatOpenStartRange(0f, 128f).fieldOf("vertical_radius").forGetter(ApplyInArea::verticalRadius))
            .apply(instance, ApplyInArea::new))
            .validate(ApplyInArea::validate);

    public static ApplyInArea aoeEffect(EntityUpgradeEffect child, float horizontalRadius, float verticalRadius)
    {
        return new ApplyInArea(child, horizontalRadius, verticalRadius);
    }

    public static ApplyInArea aoeEffect(EntityUpgradeEffect child, float radius)
    {
        return new ApplyInArea(child, radius, radius);
    }

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        AABB area = AABB.ofSize(entity.getBoundingBox().getCenter(), horizontalRadius, verticalRadius, horizontalRadius);
        List<Entity> targets = level.getEntities(entity, area);

        for (Entity target : targets)
        {
            child.applyEntityEffect(level, target, upgradeRank, context);
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.APPLY_IN_AREA.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return child.getReferencedContextParams();
    }
}