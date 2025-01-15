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

public record PlayerAttributeValueEffect(Holder<Attribute> attribute, LevelBasedDoubleValue amount) implements ValueUpgradeEffect
{
    public static final MapCodec<PlayerAttributeValueEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(PlayerAttributeValueEffect::attribute),
            LevelBasedDoubleValue.CODEC.fieldOf("amount").forGetter(PlayerAttributeValueEffect::amount))
            .apply(instance, PlayerAttributeValueEffect::new));

    @Override
    public double calculate(@Nullable Player player, @Nullable Entity targetEntity, int upgradeRank)
    {
        return amount.calculate(upgradeRank) * LimaEntityUtil.getAttributeValueSafe(player, attribute);
    }

    @Override
    public Component getValueTooltip(int upgradeRank, boolean beneficial)
    {
        return LimaTechLang.PLAYER_ATTRIBUTE_VALUE_EFFECT.translateArgs(LimaTechTooltipUtil.percentageWithSign(amount.calculate(upgradeRank), !beneficial), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.AQUA);
    }

    @Override
    public CompoundOperation operation()
    {
        return CompoundOperation.FLAT_ADDITION;
    }

    @Override
    public ValueUpgradeEffectType type()
    {
        return ValueUpgradeEffectType.PLAYER_ATTRIBUTE;
    }
}