package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

import java.util.List;

public record CompoundEntityEffect(List<EntityUpgradeEffect> effects) implements EntityUpgradeEffect
{
    public static final MapCodec<CompoundEntityEffect> CODEC = EntityUpgradeEffect.DIRECT_CODEC.listOf().xmap(CompoundEntityEffect::new, CompoundEntityEffect::effects).fieldOf("effects");

    public static CompoundEntityEffect allOf(EntityUpgradeEffect... effects)
    {
        return new CompoundEntityEffect(List.of(effects));
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        for (EntityUpgradeEffect sub : effects)
        {
            sub.apply(level, context, upgradeRank, affectedEntity, equipmentInUse);
        }
    }

    @Override
    public MapCodec<? extends EntityUpgradeEffect> codec()
    {
        return CODEC;
    }

    @Override
    public void validate(ValidationContext context)
    {
        EntityUpgradeEffect.super.validate(context);
        Validatable.validate(context, "effects", effects);
    }
}