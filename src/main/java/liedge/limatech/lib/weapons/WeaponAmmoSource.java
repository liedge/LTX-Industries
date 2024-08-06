package liedge.limatech.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum WeaponAmmoSource implements StringRepresentable
{
    NORMAL("normal"),
    COMMON_ENERGY_UNIT("energy"),
    INFINITE("infinite");

    public static final LimaEnumCodec<WeaponAmmoSource> CODEC = LimaEnumCodec.createLenient(WeaponAmmoSource.class, NORMAL);
    public static final StreamCodec<FriendlyByteBuf, WeaponAmmoSource> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(WeaponAmmoSource.class);

    private final String name;
    private final Translatable itemTooltip;

    WeaponAmmoSource(String name)
    {
        this.name = name;
        this.itemTooltip = LimaTech.RESOURCES.translationHolder("tooltip", "{}", "ammo_source", name);
    }

    public Translatable getItemTooltip()
    {
        return itemTooltip;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}