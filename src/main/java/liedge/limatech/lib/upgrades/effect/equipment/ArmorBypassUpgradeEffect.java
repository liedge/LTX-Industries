package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.LimaDynamicDamageSource;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.registry.LimaTechEntityUpgradeEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.LevelBasedValue;

public record ArmorBypassUpgradeEffect(LevelBasedValue amount, CompoundOperation operation) implements EquipmentUpgradeEffect.DamageModification
{
    public static final MapCodec<ArmorBypassUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ArmorBypassUpgradeEffect::amount),
            CompoundOperation.CODEC.fieldOf("operation").forGetter(ArmorBypassUpgradeEffect::operation))
            .apply(instance, ArmorBypassUpgradeEffect::new));

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEntityUpgradeEffects.ARMOR_BYPASS_ENTITY_EFFECT.get();
    }

    @Override
    public Component defaultEffectTooltip(int upgradeRank)
    {
        Component baseComponent = operation.toComponent(amount.calculate(upgradeRank), true);
        return LimaTechLang.ARMOR_BYPASS_EFFECT.translateArgs(baseComponent);
    }

    @Override
    public void modifyDynamicAttack(ServerLevel level, int upgradeRank, Player player, LivingEntity livingTarget, LimaDynamicDamageSource damageSource)
    {
        double baseArmor = livingTarget.getAttributeBaseValue(Attributes.ARMOR);
        double currentArmor = livingTarget.getAttributeValue(Attributes.ARMOR);
        double reduction = Math.min(currentArmor, operation.apply(baseArmor, currentArmor, amount.calculate(upgradeRank)));

        damageSource.setArmorModifier((float) reduction);
    }
}