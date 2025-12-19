package liedge.ltxindustries.lib.upgrades.effect.entity;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.ltxindustries.LTXICapabilities;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.shield.EntityBubbleShield;
import liedge.ltxindustries.registry.game.LTXIEntityUpgradeEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.function.Consumer;

import static liedge.ltxindustries.LTXIConstants.BUBBLE_SHIELD_BLUE;
import static liedge.ltxindustries.util.LTXITooltipUtil.flatNumberWithSign;
import static liedge.ltxindustries.util.LTXITooltipUtil.flatNumberWithoutSign;

public record RestoreShieldUpgradeEffect(LevelBasedValue amount, LevelBasedValue maxOvercharge) implements EntityUpgradeEffect
{
    public static final MapCodec<RestoreShieldUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(RestoreShieldUpgradeEffect::amount),
            LevelBasedValue.CODEC.optionalFieldOf("max_overcharge", LevelBasedValue.constant(0)).forGetter(RestoreShieldUpgradeEffect::maxOvercharge))
            .apply(instance, RestoreShieldUpgradeEffect::new));

    @Override
    public void applyEntityEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        if (entity instanceof LivingEntity livingEntity)
        {
            EntityBubbleShield shield = entity.getCapability(LTXICapabilities.ENTITY_BUBBLE_SHIELD);
            if (shield != null) shield.addShieldHealth(livingEntity, amount.calculate(upgradeRank), maxOvercharge.calculate(upgradeRank));
        }
    }

    @Override
    public EntityUpgradeEffectType<?> getType()
    {
        return LTXIEntityUpgradeEffects.RESTORE_BUBBLE_SHIELD.get();
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        Component tooltip = LTXILangKeys.BUBBLE_SHIELD_EFFECT.translateArgs(
                flatNumberWithSign(amount.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_BLUE.chatStyle()),
                flatNumberWithoutSign(maxOvercharge.calculate(upgradeRank)).withStyle(BUBBLE_SHIELD_BLUE.chatStyle()));
        lines.accept(tooltip);
    }
}