package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public record ModifyDamageTagsUpgradeEffect(List<TagKey<DamageType>> tags) implements EntityUpgradeEffect
{
    public static final MapCodec<ModifyDamageTagsUpgradeEffect> CODEC = TagKey.codec(Registries.DAMAGE_TYPE).listOf().fieldOf("tags").xmap(ModifyDamageTagsUpgradeEffect::new, ModifyDamageTagsUpgradeEffect::tags);

    public static ModifyDamageTagsUpgradeEffect of(TagKey<DamageType> key)
    {
        return new ModifyDamageTagsUpgradeEffect(List.of(key));
    }

    @SafeVarargs
    public static ModifyDamageTagsUpgradeEffect of(TagKey<DamageType>... keys)
    {
        return new ModifyDamageTagsUpgradeEffect(Arrays.asList(keys));
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
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        for (TagKey<DamageType> tag : tags)
        {
            Component tagComponent = Component.translatable(LTXILangKeys.namedDamageTagKey(tag));
            lines.accept(LTXILangKeys.DYNAMIC_DAMAGE_TAG_EFFECT.translateArgs(tagComponent).withStyle(ChatFormatting.AQUA));
        }
    }
}