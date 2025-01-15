package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.LimaTechEntityUpgradeEffects;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record DynamicDamageTagUpgradeEffect(TagKey<DamageType> tag) implements EquipmentUpgradeEffect.DamageModification
{
    public static final MapCodec<DynamicDamageTagUpgradeEffect> CODEC = TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("tag").xmap(DynamicDamageTagUpgradeEffect::new, DynamicDamageTagUpgradeEffect::tag);

    @Override
    public void modifyDynamicAttack(ServerLevel level, int upgradeRank, Player player, LivingEntity livingTarget, LimaDynamicDamageSource damageSource)
    {
        damageSource.addDynamicTag(tag);
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEntityUpgradeEffects.DYNAMIC_DAMAGE_TAG_ENTITY_EFFECT.get();
    }

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.DYNAMIC_DAMAGE_TAG_EFFECT.translateArgs(Component.translatable(LimaTechLang.namedDamageTagKey(tag)).withStyle(LimaTechConstants.LIME_GREEN.chatStyle()));
    }
}