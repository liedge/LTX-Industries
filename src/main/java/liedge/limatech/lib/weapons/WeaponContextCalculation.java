package liedge.limatech.lib.weapons;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.util.LimaEntityUtil;
import liedge.limatech.item.weapon.WeaponItem;
import net.minecraft.core.Holder;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import org.jetbrains.annotations.Nullable;

public abstract class WeaponContextCalculation
{
    public static final Codec<WeaponContextCalculation> CODEC = Type.CODEC.dispatch(WeaponContextCalculation::getType, Type::getCodec);

    public static WeaponContextCalculation flatAddition(LevelBasedValue value)
    {
        return new Addition(value);
    }

    public static WeaponContextCalculation flatAddition(float amount)
    {
        return flatAddition(LevelBasedValue.constant(amount));
    }

    public static WeaponContextCalculation multiplyBase(LevelBasedValue value)
    {
        return new MultiplyBase(value);
    }

    public static WeaponContextCalculation multiplyBase(float amount)
    {
        return multiplyBase(LevelBasedValue.constant(amount));
    }

    public static WeaponContextCalculation multiplyByAttribute(LevelBasedValue value, Holder<Attribute> attribute)
    {
        return new MultiplyByTargetAttribute(value, attribute);
    }

    public static WeaponContextCalculation multiplyByAttribute(float amount, Holder<Attribute> attribute)
    {
        return multiplyByAttribute(LevelBasedValue.constant(amount), attribute);
    }

    public static WeaponContextCalculation overrideBase(float amount)
    {
        return new OverrideBase(LevelBasedValue.constant(amount));
    }

    private final LevelBasedValue operand;

    protected WeaponContextCalculation(LevelBasedValue operand)
    {
        this.operand = operand;
    }

    protected LevelBasedValue getOperand()
    {
        return operand;
    }

    public abstract double doCalculation(WeaponItem weaponItem, double traitBaseValue, int upgradeRank, @Nullable Entity targetEntity);

    public abstract Type getType();

    private static class OverrideBase extends WeaponContextCalculation
    {
        private static final MapCodec<WeaponContextCalculation> CODEC = LevelBasedValue.CODEC.fieldOf("amount").xmap(OverrideBase::new, WeaponContextCalculation::getOperand);

        private OverrideBase(LevelBasedValue operand)
        {
            super(operand);
        }

        @Override
        public double doCalculation(WeaponItem weaponItem, double traitBaseValue, int upgradeRank, @Nullable Entity targetEntity)
        {
            return getOperand().calculate(0);
        }

        @Override
        public Type getType()
        {
            return Type.OVERRIDE_BASE;
        }
    }

    private static class Addition extends WeaponContextCalculation
    {
        private static final MapCodec<WeaponContextCalculation> CODEC = LevelBasedValue.CODEC.fieldOf("amount").xmap(Addition::new, WeaponContextCalculation::getOperand);

        private Addition(LevelBasedValue operand)
        {
            super(operand);
        }

        @Override
        public double doCalculation(WeaponItem weaponItem, double traitBaseValue, int upgradeRank, @Nullable Entity targetEntity)
        {
            return getOperand().calculate(upgradeRank);
        }

        @Override
        public Type getType()
        {
            return Type.ADDITION;
        }
    }

    private static class MultiplyBase extends WeaponContextCalculation
    {
        private static final MapCodec<WeaponContextCalculation> CODEC = LevelBasedValue.CODEC.fieldOf("amount").xmap(MultiplyBase::new, WeaponContextCalculation::getOperand);

        private MultiplyBase(LevelBasedValue operand)
        {
            super(operand);
        }

        @Override
        public double doCalculation(WeaponItem weaponItem, double traitBaseValue, int upgradeRank, @Nullable Entity targetEntity)
        {
            return traitBaseValue * getOperand().calculate(upgradeRank);
        }

        @Override
        public Type getType()
        {
            return Type.MULTIPLY_BASE;
        }
    }

    private static class MultiplyByTargetAttribute extends WeaponContextCalculation
    {
        private static final MapCodec<MultiplyByTargetAttribute> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                LevelBasedValue.CODEC.fieldOf("amount").forGetter(WeaponContextCalculation::getOperand),
                Attribute.CODEC.fieldOf("attribute").forGetter(o -> o.attribute))
                .apply(instance, MultiplyByTargetAttribute::new));

        private final Holder<Attribute> attribute;

        private MultiplyByTargetAttribute(LevelBasedValue operand, Holder<Attribute> attribute)
        {
            super(operand);
            this.attribute = attribute;
        }

        @Override
        public double doCalculation(WeaponItem weaponItem, double traitBaseValue, int upgradeRank, @Nullable Entity targetEntity)
        {
            return LimaEntityUtil.getAttributeValueSafe(targetEntity, attribute) * getOperand().calculate(upgradeRank);
        }

        @Override
        public Type getType()
        {
            return Type.MULTIPLY_BY_TARGET_ATTRIBUTE;
        }
    }

    public enum Type implements StringRepresentable
    {
        ADDITION("addition", Addition.CODEC),
        MULTIPLY_BASE("multiply_base", MultiplyBase.CODEC),
        MULTIPLY_BY_TARGET_ATTRIBUTE("multiply_by_target_attribute", MultiplyByTargetAttribute.CODEC),
        OVERRIDE_BASE("override_base", OverrideBase.CODEC);

        public static final LimaEnumCodec<Type> CODEC = LimaEnumCodec.createStrict(Type.class);

        private final String name;
        private final MapCodec<? extends WeaponContextCalculation> codec;

        Type(String name, MapCodec<? extends WeaponContextCalculation> codec)
        {
            this.name = name;
            this.codec = codec;
        }

        public MapCodec<? extends WeaponContextCalculation> getCodec()
        {
            return codec;
        }

        @Override
        public String getSerializedName()
        {
            return name;
        }
    }
}