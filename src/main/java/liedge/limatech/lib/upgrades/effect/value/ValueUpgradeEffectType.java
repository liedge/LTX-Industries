package liedge.limatech.lib.upgrades.effect.value;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum ValueUpgradeEffectType implements StringRepresentable
{
    SIMPLE_OPERATION("simple_op", SimpleValueOperationEffect.CODEC),
    TARGET_ATTRIBUTE("target_attribute", TargetAttributeValueEffect.CODEC),
    PLAYER_ATTRIBUTE("player_attribute", PlayerAttributeValueEffect.CODEC);

    public static final Codec<ValueUpgradeEffectType> CODEC = LimaEnumCodec.create(ValueUpgradeEffectType.class);

    private final String name;
    private final MapCodec<? extends ValueUpgradeEffect> codec;

    ValueUpgradeEffectType(String name, MapCodec<? extends ValueUpgradeEffect> codec)
    {
        this.name = name;
        this.codec = codec;
    }

    public MapCodec<? extends ValueUpgradeEffect> getCodec()
    {
        return codec;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}