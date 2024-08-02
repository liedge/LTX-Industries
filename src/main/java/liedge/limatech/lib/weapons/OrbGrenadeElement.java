package liedge.limatech.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.LimaColor;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.lib.Translatable;
import liedge.limatech.registry.LimaTechDataComponents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum OrbGrenadeElement implements StringRepresentable, Translatable, OrderedEnum<OrbGrenadeElement>
{
    EXPLOSIVE("explosive", 0x8b8b8b),
    FLAME("flame", 0xff8c19),
    FREEZE("freeze", 0xc0f6fc),
    ELECTRIC("electric", 0xdeff70),
    ACID("acid", 0x39e622);

    // RE Yellow/orange acid - 0xffc933
    // Dark green acid - 0x39e622
    // Pale green acid - 0x5ae869

    public static final LimaEnumCodec<OrbGrenadeElement> CODEC = LimaEnumCodec.createDefaulted(OrbGrenadeElement.class, EXPLOSIVE);
    public static final StreamCodec<FriendlyByteBuf, OrbGrenadeElement> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(OrbGrenadeElement.class);

    public static OrbGrenadeElement getFromItem(ItemStack stack)
    {
        return stack.getOrDefault(LimaTechDataComponents.GRENADE_ELEMENT, EXPLOSIVE);
    }

    private final String name;
    private final LimaColor color;
    private final String descriptionId;

    OrbGrenadeElement(String name, int colorRgb)
    {
        this.name = name;
        this.color = LimaColor.makeColor(colorRgb);
        this.descriptionId = "orb_element." + name;
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
        return Translatable.super.translate().withStyle(color::applyStyle);
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}