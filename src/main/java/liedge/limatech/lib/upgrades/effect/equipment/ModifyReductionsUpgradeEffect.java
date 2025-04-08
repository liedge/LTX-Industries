package liedge.limatech.lib.upgrades.effect.equipment;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.damage.DamageReductionType;
import liedge.limacore.lib.damage.LimaCoreDamageComponents;
import liedge.limacore.lib.damage.ReductionModifier;
import liedge.limacore.lib.math.MathOperation;
import liedge.limatech.registry.game.LimaTechEquipmentUpgradeEffects;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record ModifyReductionsUpgradeEffect(DamageReductionType reductionType, LevelBasedValue amount) implements EquipmentUpgradeEffect
{
    public static final MapCodec<ModifyReductionsUpgradeEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            DamageReductionType.CODEC.fieldOf("reduction_type").forGetter(ModifyReductionsUpgradeEffect::reductionType),
            LevelBasedValue.CODEC.fieldOf("amount").forGetter(ModifyReductionsUpgradeEffect::amount))
            .apply(instance, ModifyReductionsUpgradeEffect::new));

    @Override
    public void applyEquipmentEffect(ServerLevel level, Entity entity, int upgradeRank, LootContext context)
    {
        DamageSource source = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (source != null) source.mergeListElement(LimaCoreDamageComponents.REDUCTION_MODIFIERS, new ReductionModifier(amount.calculate(upgradeRank), MathOperation.MULTIPLY, reductionType));
    }

    @Override
    public MapCodec<? extends EquipmentUpgradeEffect> codec()
    {
        return LimaTechEquipmentUpgradeEffects.MODIFY_DAMAGE_REDUCTIONS.get();
    }

    @Override
    public Component getEffectTooltip(int upgradeRank)
    {
        MutableComponent c = LimaTechTooltipUtil.percentageWithSign(1f - amount.calculate(upgradeRank), false);
        return c.append(CommonComponents.SPACE).append(reductionType.translate()).append(" reduction");
    }
}