package liedge.limatech.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import static liedge.limatech.LimaTechConstants.*;

public enum GrenadeType implements StringRepresentable, Translatable, OrderedEnum<GrenadeType>
{
    EXPLOSIVE("explosive", EXPLOSIVE_GRAY),
    FLAME("flame", FLAME_ORANGE),
    FREEZE("freeze", FREEZE_LIGHT_BLUE),
    ELECTRIC("electric", ELECTRIC_GREEN),
    ACID("acid", ACID_GREEN),
    NEURO("neuro", NEURO_BLUE);

    public static final LimaEnumCodec<GrenadeType> CODEC = LimaEnumCodec.create(GrenadeType.class);
    public static final StreamCodec<FriendlyByteBuf, GrenadeType> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(GrenadeType.class);

    private final String name;
    private final LimaColor color;
    private final String descriptionId;

    GrenadeType(String name, LimaColor color)
    {
        this.name = name;
        this.color = color;
        this.descriptionId = LimaTech.RESOURCES.translationKey("grenade_type", "{}", name);
    }

    public LimaColor getColor()
    {
        return color;
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    @Override
    public MutableComponent translate()
    {
        return Translatable.super.translate().withStyle(color.chatStyle());
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}