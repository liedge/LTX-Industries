package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.math.CompoundOperation;
import liedge.limatech.lib.math.LevelBasedDoubleValue;
import liedge.limatech.util.LimaTechTooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public record TargetAttributeValueEffect(Holder<Attribute> attribute, LevelBasedDoubleValue amount) implements ValueUpgradeEffect
{
    public static final MapCodec<TargetAttributeValueEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(TargetAttributeValueEffect::attribute),
            LevelBasedDoubleValue.CODEC.fieldOf("amount").forGetter(TargetAttributeValueEffect::amount))
            .apply(instance, TargetAttributeValueEffect::new));

    @Override
    public double calculate(@Nullable Player player, @Nullable Entity targetEntity, int upgradeRank)
    {
        return amount.calculate(upgradeRank) * LimaEntityUtil.getAttributeValueSafe(targetEntity, attribute);
    }

    @Override
    public Component getValueTooltip(int upgradeRank, boolean beneficial)
    {
        return LimaTechLang.TARGET_ATTRIBUTE_VALUE_EFFECT.translateArgs(LimaTechTooltipUtil.percentageWithSign(amount.calculate(upgradeRank), !beneficial), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public CompoundOperation operation()
    {
        return CompoundOperation.FLAT_ADDITION;
    }

    @Override
    public ValueUpgradeEffectType type()
    {
        return ValueUpgradeEffectType.TARGET_ATTRIBUTE;
    }
}