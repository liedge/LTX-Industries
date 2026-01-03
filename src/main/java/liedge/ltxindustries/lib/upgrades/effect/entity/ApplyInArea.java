package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.Set;

public record ApplyInArea(EntityUpgradeEffect child, ContextlessValue horizontalRadius, ContextlessValue verticalRadius) implements EntityUpgradeEffect
{
    public static final MapCodec<ApplyInArea> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntityUpgradeEffect.DIRECT_CODEC.fieldOf("child").forGetter(ApplyInArea::child),
            ContextlessValue.CODEC.fieldOf("horizontal_radius").forGetter(ApplyInArea::horizontalRadius),
            ContextlessValue.CODEC.fieldOf("vertical_radius").forGetter(ApplyInArea::verticalRadius))
            .apply(instance, ApplyInArea::new));

    public static ApplyInArea aoeEffect(EntityUpgradeEffect child, ContextlessValue horizontalRadius, ContextlessValue verticalRadius)
    {
        return new ApplyInArea(child, horizontalRadius, verticalRadius);
    }

    public static ApplyInArea aoeEffect(EntityUpgradeEffect child, ContextlessValue radius)
    {
        return new ApplyInArea(child, radius, radius);
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        double horizontal = horizontalRadius.calculate(upgradeRank);
        double vertical = verticalRadius.calculate(upgradeRank);
        if (horizontal <= 0 || vertical <= 0) return;

        AABB area = AABB.ofSize(affectedEntity.getBoundingBox().getCenter(), horizontal, vertical, horizontal);
        List<Entity> targets = level.getEntities(affectedEntity, area);

        for (Entity target : targets)
        {
            child.apply(level, context, upgradeRank, target, equipmentInUse);
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
        return LimaLootUtil.joinReferencedParams(child, horizontalRadius, verticalRadius);
    }
}