package liedge.ltxindustries.lib.upgrades.value;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import liedge.limacore.data.LimaCoreCodecs;
import liedge.limacore.util.LimaCollectionsUtil;
import liedge.ltxindustries.LTXIndustries;

public final class UpgradeValueTypes
{
    private UpgradeValueTypes() {}

    private static final BiMap<String, MapCodec<? extends UpgradeValueProvider>> MASTER_REGISTRY = HashBiMap.create();
    private static final BiMap<String, MapCodec<? extends ContextlessValue>> CONTEXTLESS_REGISTRY = HashBiMap.create();

    private static <T extends UpgradeValueProvider> Codec<MapCodec<? extends T>> registryCodec(String type, BiMap<String, MapCodec<? extends T>> registry)
    {
        return Codec.STRING.flatXmap(
                name -> LimaCoreCodecs.nullableDataResult(registry.get(name), () -> String.format("Unregistered %s type %s", type, name)),
                codec -> LimaCoreCodecs.nullableDataResult(registry.inverse().get(codec), () -> String.format("Unregistered %s codec %s", type, codec.toString())));
    }

    static final Codec<MapCodec<? extends UpgradeValueProvider>> MASTER_REGISTRY_CODEC = registryCodec("value", MASTER_REGISTRY);
    static final Codec<MapCodec<? extends ContextlessValue>> CONTEXTLESS_REGISTRY_CODEC = registryCodec("contextless value", CONTEXTLESS_REGISTRY);

    private static void register(String name, MapCodec<? extends UpgradeValueProvider> codec)
    {
        LimaCollectionsUtil.putNoDuplicates(MASTER_REGISTRY, name, codec);
    }

    private static void registerContextless(String name, MapCodec<? extends ContextlessValue> codec)
    {
        register(name, codec);
        LimaCollectionsUtil.putNoDuplicates(CONTEXTLESS_REGISTRY, name, codec);
    }

    public static void register()
    {
        registerContextless("constant", ConstantDouble.CODEC);
        registerContextless("linear", LinearDouble.CODEC);
        registerContextless("exponential", ExponentialDouble.CODEC);
        registerContextless("contextless_math", MathOperationDouble.CODEC);
        registerContextless("levels_wrapper", LBVWrapper.CODEC);
        registerContextless("lookup", DoubleLookup.CODEC);
        register("math", MathOperationValue.CODEC);
        register("number_wrapper", NumberProviderWrapper.CODEC);
        register("context_key", ContextKeyValue.CODEC);

        LTXIndustries.LOGGER.info("Registered upgrade value type codecs.");
    }
}