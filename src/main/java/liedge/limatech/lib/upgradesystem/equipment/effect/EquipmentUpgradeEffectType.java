package liedge.limatech.lib.upgradesystem.equipment.effect;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public record EquipmentUpgradeEffectType<T extends EquipmentUpgradeEffect>(ResourceLocation id, MapCodec<T> codec)
{
    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof EquipmentUpgradeEffectType<?> other)) return false;
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