package liedge.ltxindustries.lib.upgrades.value;

import com.mojang.serialization.MapCodec;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.util.ExtraCodecs;

public final class UpgradeValueTypes
{
    private UpgradeValueTypes() {}

    static final ExtraCodecs.LateBoundIdMapper<String, MapCodec<? extends UpgradeValueProvider>> ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();
    static final ExtraCodecs.LateBoundIdMapper<String, MapCodec<? extends ContextlessValue>> CONTEXTLESS_ID_MAPPER = new ExtraCodecs.LateBoundIdMapper<>();

    private static void register(String name, MapCodec<? extends UpgradeValueProvider> codec)
    {
        ID_MAPPER.put(name, codec);
    }

    private static void registerContextless(String name, MapCodec<? extends ContextlessValue> codec)
    {
        register(name, codec);
        CONTEXTLESS_ID_MAPPER.put(name, codec);
    }

    public static void bootstrap()
    {
        registerContextless("constant", ConstantDouble.CODEC);
        registerContextless("linear", LinearDouble.CODEC);
        registerContextless("exponential", ExponentialDouble.CODEC);
        registerContextless("contextless_math", MathOperationDouble.CODEC);
        registerContextless("levels_wrapper", LBVWrapper.CODEC);
        registerContextless("lookup", DoubleLookup.CODEC);
        register("math", MathOperationValue.CODEC);
        register("number_wrapper", NumberProviderWrapper.CODEC);
        register("distance_curve", TargetDistanceCurve.CODEC);

        LTXIndustries.LOGGER.info("Registered upgrade value type codecs.");
    }
}