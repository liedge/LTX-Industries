package liedge.ltxindustries.util.config;

import liedge.limacore.LimaCommonConstants;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jspecify.annotations.Nullable;

final class ConfigUtil
{
    private ConfigUtil() { }

    static ModConfigSpec.IntValue positiveInt(ModConfigSpec.Builder builder, String field, String comment, int defaultValue)
    {
        return builder.comment(comment).defineInRange(field, defaultValue, 1, Integer.MAX_VALUE);
    }

    static ModConfigSpec.IntValue energyCapacity(ModConfigSpec.Builder builder, int defaultValue)
    {
        return positiveInt(builder, LimaCommonConstants.KEY_ENERGY_CAPACITY, "Base energy capacity", defaultValue);
    }

    static ModConfigSpec.IntValue energyTransfer(ModConfigSpec.Builder builder, int defaultValue)
    {
        return positiveInt(builder, LimaCommonConstants.KEY_ENERGY_TRANSFER_RATE, "Base energy transfer rate per tick", defaultValue);
    }

    static ModConfigSpec.IntValue customEnergyUsage(ModConfigSpec.Builder builder, String comment, int defaultValue)
    {
        return positiveInt(builder, LimaCommonConstants.KEY_ENERGY_USAGE, comment, defaultValue);
    }

    static ModConfigSpec.IntValue energyUsagePerTick(ModConfigSpec.Builder builder, int defaultValue)
    {
        return customEnergyUsage(builder, "Base energy usage per tick", defaultValue);
    }

    static ModConfigSpec.DoubleValue damageSpec(ModConfigSpec.Builder builder, String field, @Nullable String comment, double defaultValue)
    {
        if (comment != null) builder.comment(comment);
        return builder.defineInRange(field, defaultValue, 0.5d, 4096d);
    }

    static ModConfigSpec.DoubleValue baseDamage(ModConfigSpec.Builder builder, @Nullable String comment, double defaultValue)
    {
        return damageSpec(builder, "base_damage", comment, defaultValue);
    }

    static int getIntValueOrZero(ModConfigSpec spec, ModConfigSpec.IntValue value)
    {
        return spec.isLoaded() ? value.getAsInt() : 0;
    }
}