package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import liedge.limacore.lib.math.MathOperation;
import liedge.limacore.util.LimaLootUtil;
import liedge.ltxindustries.lib.upgrades.value.UpgradeValueProvider;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.util.Set;

public record ValueOperation(UpgradeValueProvider value, MathOperation operation) implements LootContextUser
{
    public static final Codec<ValueOperation> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UpgradeValueProvider.DIRECT_CODEC.fieldOf("value").forGetter(ValueOperation::value),
            MathOperation.COMPOUND_OP_CODEC.fieldOf("op").forGetter(ValueOperation::operation))
            .apply(instance, ValueOperation::new));

    public static final Codec<ValueOperation> CONTEXTLESS_CODEC = LimaLootUtil.contextUserCodec(DIRECT_CODEC, LootContextParamSets.EMPTY, "contextless value operation");

    public static ValueOperation of(UpgradeValueProvider value, MathOperation operation)
    {
        return new ValueOperation(value, operation);
    }

    public static ValueOperation of(NumberProvider provider, MathOperation operation)
    {
        return new ValueOperation(UpgradeValueProvider.wrap(provider), operation);
    }

    public double apply(LootContext context, int upgradeRank, double base, double total)
    {
        return operation.applyCompoundingDouble(total, base, value.get(context, upgradeRank));
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams()
    {
        return value.getReferencedContextParams();
    }
}