package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.registry.game.LimaTechEquipmentUpgradeEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public record DynamicDamageTagUpgradeEffect(TagKey<DamageType> tag) implements EquipmentUpgradeEffect.DamageModification
{
    public static final MapCodec<DynamicDamageTagUpgradeEffect> CODEC = TagKey.codec(Registries.DAMAGE_TYPE).fieldOf("tag").xmap(DynamicDamageTagUpgradeEffect::new, DynamicDamageTagUpgradeEffect::tag);

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEquipmentUpgradeEffects.DYNAMIC_DAMAGE_TAG_EQUIPMENT_EFFECT.get();
    }

    @Override
    public void modifyDynamicAttack(Player player, int upgradeRank, LivingEntity target, LimaDynamicDamageSource damageSource)
    {
        damageSource.addDynamicTag(tag);
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.DYNAMIC_DAMAGE_TAG_EFFECT.translateArgs(Component.translatable(LimaTechLang.namedDamageTagKey(tag)).withStyle(ChatFormatting.AQUA));
    }
}