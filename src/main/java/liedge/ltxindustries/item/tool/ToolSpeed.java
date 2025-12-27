package liedge.ltxindustries.item.tool;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.OrderedEnum;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

public enum ToolSpeed implements StringRepresentable, Translatable, OrderedEnum<ToolSpeed>
{
    OFF("off", 0f),
    SLOW("slow", 3f),
    NORMAL("normal", 9f),
    FAST("fast", 20f),
    TURBO("turbo", 75f);

    public static final LimaEnumCodec<ToolSpeed> CODEC = LimaEnumCodec.create(ToolSpeed.class);
    public static final StreamCodec<FriendlyByteBuf, ToolSpeed> STREAM_CODEC = NeoForgeStreamCodecs.enumCodec(ToolSpeed.class);

    private final String name;
    private final String descriptionId;
    private final float speed;

    ToolSpeed(String name, float speed)
    {
        this.name = name;
        this.descriptionId = LTXIndustries.RESOURCES.translationKey("tool_speed.{}", name);
        this.speed = speed;
    }

    public float getSpeed()
    {
        return speed;
    }

    @Override
    public String descriptionId()
    {
        return descriptionId;
    }

    @Override
    public String getSerializedName()
    {
        return name;
    }
}