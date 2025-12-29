package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

public record RestoreShield(LevelBasedValue amount, LevelBasedValue maxOvercharge) implements EntityUpgradeEffect
{
    public static final MapCodec<RestoreShield> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(RestoreShield::amount),
            LevelBasedValue.CODEC.optionalFieldOf("max_overcharge", LevelBasedValue.constant(0)).forGetter(RestoreShield::maxOvercharge))
            .apply(instance, RestoreShield::new));

    public static RestoreShield restore(LevelBasedValue amount, LevelBasedValue maxOvercharge)
    {
        return new RestoreShield(amount, maxOvercharge);
    }

    public static RestoreShield restore(LevelBasedValue amount)
    {
        return new RestoreShield(amount, LevelBasedValue.constant(0));
    }

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            EntityBubbleShield shield = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shield != null) shield.addShieldHealth(livingEntity, amount.calculate(upgradeRank), maxOvercharge.calculate(upgradeRank));
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.RESTORE_BUBBLE_SHIELD.get();
    }
}