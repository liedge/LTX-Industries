package liedge.limatech.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import net.minecraft.util.StringRepresentable;

public enum WeaponAttribute implements StringRepresentable
{
    DAMAGE("damage"),
    RANGE("range"),
    PROJECTILE_SPEED("projectile_speed"),
    RELOAD_SPEED("reload_speed");

    public static final LimaEnumCodec<WeaponAttribute> CODEC = LimaEnumCodec.createStrict(WeaponAttribute.class);

    private final String name;

    WeaponAttribute(String name)
    {
        this.name = name;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}