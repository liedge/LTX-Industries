package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.function.Consumer;

public record IgniteEntity(LevelBasedValue duration) implements EntityUpgradeEffect
{
    public static final MapCodec<IgniteEntity> CODEC = LevelBasedValue.CODEC.fieldOf("duration").xmap(IgniteEntity::new, IgniteEntity::duration);

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        entity.igniteForSeconds(duration.calculate(upgradeRank));
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.IGNITE_ENTITY.get();
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines) { }
}