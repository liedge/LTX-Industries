package liedge.ltxindustries.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.entity.BubbleShieldUser;
import liedge.ltxindustries.registry.game.LTXIEquipmentUpgradeEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.function.Consumer;

import static liedge.ltxindustries.LTXIConstants.BUBBLE_SHIELD_GREEN;
import static liedge.ltxindustries.util.LTXITooltipUtil.flatNumberWithSign;
import static liedge.ltxindustries.util.LTXITooltipUtil.flatNumberWithoutSign;

public record BubbleShieldUpgradeEffect(LevelBasedValue amount, LevelBasedValue maxShield) implements EquipmentUpgradeEffect
{
    public static final MapCodec<BubbleShieldUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(BubbleShieldUpgradeEffect::amount),
            LevelBasedValue.CODEC.optionalFieldOf("max_shield", LevelBasedValue.constant(BubbleShieldUser.MAX_SHIELD_HEALTH)).forGetter(BubbleShieldUpgradeEffect::maxShield))
            .apply(instance, BubbleShieldUpgradeEffect::new));

    @Override
    public void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            BubbleShieldUser user = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (user != null) user.addShieldHealth(livingEntity, amount.calculate(upgradeRank), maxShield.calculate(upgradeRank));
        }
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LTXIEquipmentUpgradeEffects.BUBBLE_SHIELD_EQUIPMENT_EFFECT.get();
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component tooltip = LTXILangKeys.BUBBLE_SHIELD_EFFECT.translateArgs(flatNumberWithSign(amount.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_GREEN.chatStyle()), flatNumberWithoutSign(maxShield.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_GREEN.chatStyle()));
        lines.accept(tooltip);
    }
}