package liedge.limatech.lib.upgradesystem.calculation;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.util.LimaCoreUtil;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.LevelBasedDoubleValue;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.Nullable;

public final class LivingAttributeCalculation extends CompoundCalculation
{
    public static final MapCodec<LivingAttributeCalculation> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Attribute.CODEC.fieldOf("attribute").forGetter(o -> o.attribute),
            LevelBasedDoubleValue.CODEC.fieldOf("amount").forGetter(CompoundCalculation::getValue))
            .apply(instance, LivingAttributeCalculation::new));

    private final Holder<Attribute> attribute;

    public LivingAttributeCalculation(Holder<Attribute> attribute, LevelBasedDoubleValue value)
    {
        super(value);
        this.attribute = attribute;
    }

    @Override
    public double calculate(double baseValue, int level, @Nullable Object optionalContext)
    {
        double attributeValue = LimaEntityUtil.getAttributeValueSafe(LimaCoreUtil.castOrNull(LivingEntity.class, optionalContext), attribute);
        return attributeValue * getValue().calculate(level);
    }

    @Override
    public Component getTooltip(int level)
    {
        return LimaTechLang.ADD_MULTIPLIED_ATTRIBUTE_CALCULATION.translateArgs(formatPercentageWithSign(getValue().calculate(level)), Component.translatable(attribute.value().getDescriptionId())).withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public CalculationType getType()
    {
        return CalculationType.ADD_MULTIPLIED_ATTRIBUTE;
    }
}