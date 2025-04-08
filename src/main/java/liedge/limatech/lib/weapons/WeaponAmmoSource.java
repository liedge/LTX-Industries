package liedge.limatech.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.limatech.LimaTech;
import liedge.limatech.LimaTechConstants;
import liedge.limatech.client.LimaTechLang;
import liedge.limatech.lib.upgrades.effect.EffectTooltipProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.Consumer;

public enum WeaponAmmoSource implements StringRepresentable, EffectTooltipProvider
{
    NORMAL("normal"),
    COMMON_ENERGY_UNIT("energy"),
    INFINITE("infinite");

    public static final LimaEnumCodec<WeaponAmmoSource> CODEC = LimaEnumCodec.create(WeaponAmmoSource.class);
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

    @Override
    public void appendEffectLines(int upgradeRank, Consumer<Component> linesConsumer)
    {
        switch (this)
        {
            case COMMON_ENERGY_UNIT -> linesConsumer.accept(LimaTechLang.ENERGY_AMMO_EFFECT.translate().withStyle(LimaTechConstants.REM_BLUE.chatStyle()));
            case INFINITE -> LimaTechLang.INFINITE_AMMO_EFFECT.translate().withStyle(LimaTechConstants.CREATIVE_PINK.chatStyle());
        }
    }
}