package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limatech.LimaTechCapabilities;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.entity.BubbleShieldUser;
import liedge.limatech.registry.game.LimaTechEquipmentUpgradeEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

import static liedge.limatech.LimaTechConstants.BUBBLE_SHIELD_GREEN;
import static liedge.limatech.util.LimaTechTooltipUtil.flatNumberWithSign;
import static liedge.limatech.util.LimaTechTooltipUtil.flatNumberWithoutSign;

public record BubbleShieldUpgradeEffect(LevelBasedValue amount, LevelBasedValue maxShield) implements EquipmentUpgradeEffect
{
    public static final MapCodec<BubbleShieldUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(BubbleShieldUpgradeEffect::amount),
            LevelBasedValue.CODEC.optionalFieldOf("max_shield", LevelBasedValue.constant(BubbleShieldUser.MAX_SHIELD_HEALTH)).forGetter(BubbleShieldUpgradeEffect::maxShield))
            .apply(instance, BubbleShieldUpgradeEffect::new));

    @Override
    public void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        BubbleShieldUser user = entity.getCapability(LimaTechCapabilities.ENTITY_BUBBLE_SHIELD);
        if (user != null) user.addShieldHealth(amount.calculate(upgradeRank), maxShield.calculate(upgradeRank));
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEquipmentUpgradeEffects.BUBBLE_SHIELD_EQUIPMENT_EFFECT.get();
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        return LimaTechLang.BUBBLE_SHIELD_EFFECT.translateArgs(flatNumberWithSign(amount.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_GREEN.chatStyle()), flatNumberWithoutSign(maxShield.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_GREEN.chatStyle()));
    }
}