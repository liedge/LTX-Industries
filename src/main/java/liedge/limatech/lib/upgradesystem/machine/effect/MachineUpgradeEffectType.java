package liedge.limatech.lib.upgradesystem.machine.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record MachineUpgradeEffectType<T extends MachineUpgradeEffect>(ResourceLocation id, MapCodec<T> codec)
{
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof MachineUpgradeEffectType<?> other)) return false;
        return this.id.equals(other.id);
    }

    @Override
    public int hashCode()
    {
        return id.hashCode();
    }

    @Override
    public String toString()
    {
        return id.toString();
    }
}