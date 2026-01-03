package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.lib.upgrades.UpgradedEquipmentInUse;
import liedge.ltxindustries.lib.upgrades.value.ConstantDouble;
import liedge.ltxindustries.lib.upgrades.value.ContextlessValue;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;

import java.util.Set;

public record RestoreShield(UpgradeValueProvider amount, ContextlessValue maxOvercharge) implements EntityUpgradeEffect
{
    public static final MapCodec<RestoreShield> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("amount").forGetter(RestoreShield::amount),
            ContextlessValue.CODEC.optionalFieldOf("max_overcharge", ConstantDouble.of(0)).forGetter(RestoreShield::maxOvercharge))
            .apply(instance, RestoreShield::new));

    public static RestoreShield restore(UpgradeValueProvider amount, ContextlessValue maxOvercharge)
    {
        return new RestoreShield(amount, maxOvercharge);
    }

    public static RestoreShield restore(UpgradeValueProvider amount)
    {
        return new RestoreShield(amount, ConstantDouble.of(0));
    }

    @Override
    public void apply(ServerLevel level, LootContext context, int upgradeRank, Entity affectedEntity, UpgradedEquipmentInUse equipmentInUse)
    {
        if (affectedEntity instanceof LivingEntity livingEntity)
        {
            EntityBubbleShield shield = affectedEntity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shield != null) shield.addShieldHealth(livingEntity, (float) amount.get(context, upgradeRank), (float) maxOvercharge.calculate(upgradeRank));
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.RESTORE_SHIELD.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return amount.getReferencedContextParams();
    }
}