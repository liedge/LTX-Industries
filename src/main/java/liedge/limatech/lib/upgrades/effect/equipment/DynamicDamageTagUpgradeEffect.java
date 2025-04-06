package liedge.limatech.lib.upgrades.effect.equipment;

import com.google.common.cache.LoadingCache;
import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.effect.EffectTooltipCaches;
import liedge.limatech.registry.game.LimaTechEquipmentUpgradeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;

public record DynamicDamageTagUpgradeEffect(TagKey<DamageType> tag) implements EquipmentUpgradeEffect.DamageModification
{
    private static final LoadingCache<TagKey<DamageType>, Component> TOOLTIP_CACHE = EffectTooltipCaches.getInstance().create(25, tag -> LimaTechLang.DYNAMIC_DAMAGE_TAG_EFFECT.translateArgs(Component.translatable(LimaTechLang.namedDamageTagKey(tag)).withStyle(ChatFormatting.AQUA)));

    public static final MapCodec<DynamicDamageTagUpgradeEffect> CODEC = TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("tag").xmap(DynamicDamageTagUpgradeEffect::new, DynamicDamageTagUpgradeEffect::tag);

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEquipmentUpgradeEffects.DYNAMIC_DAMAGE_TAG_EQUIPMENT_EFFECT.get();
    }

    @Override
    public void modifyDynamicDamageSource(ServerLevel level, Entity entity, int upgradeRank, LimaDynamicDamageSource damageSource)
    {
        damageSource.addDynamicTag(tag);
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return TOOLTIP_CACHE.getUnchecked(tag);
    }
}