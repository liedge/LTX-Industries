package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public record AddDamageTags(List<TagKey<DamageType>> tags) implements EntityUpgradeEffect
{
    public static final MapCodec<AddDamageTags> CODEC = TagKey.codec(Registries.DAMAGE_TYPE).listOf().fieldOf("tags").xmap(AddDamageTags::new, AddDamageTags::tags);

    public static AddDamageTags addTags(TagKey<DamageType> key)
    {
        return new AddDamageTags(List.of(key));
    }

    @SafeVarargs
    public static AddDamageTags addTags(TagKey<DamageType>... keys)
    {
        return new AddDamageTags(Arrays.asList(keys));
    }

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        DamageSource damageSource = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (damageSource != null) tags.forEach(damageSource::limaCore$addDynamicTag);
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.MODIFY_DAMAGE_TAGS.get();
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return Set.of(LootContextParams.DAMAGE_SOURCE);
    }
}