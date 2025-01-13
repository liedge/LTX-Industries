package liedge.limatech.lib.upgradesystem.effect;

import com.mojang.serialization.Codec;
import liedge.limatech.registry.LimaTechRegistries;
import net.minecraft.resources.ResourceLocation;

public record UpgradeEffectType<E>(ResourceLocation id, Codec<E> codec)
{
    public static final Codec<UpgradeEffectType<?>> CODEC = LimaTechRegistries.UPGRADE_EFFECT_TYPE.byNameCodec();

    @Override
    public String toString()
    {
        return id.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        else if (obj instanceof UpgradeEffectType<?> other)
        {
            return this.id.equals(other.id);
        }
        else
        {
            return false;
        }
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }
}