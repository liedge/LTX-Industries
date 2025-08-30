package liedge.ltxindustries.lib.weapons;

import liedge.limacore.data.LimaEnumCodec;
import liedge.limacore.lib.Translatable;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.effect.UpgradeTooltipsProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.function.Consumer;

public enum WeaponAmmoSource implements StringRepresentable, UpgradeTooltipsProvider
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
        this.itemTooltip = LTXIndustries.RESOURCES.translationHolder("tooltip", "{}", "ammo_source", name);
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
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        switch (this)
        {
            case COMMON_ENERGY_UNIT -> lines.accept(LTXILangKeys.ENERGY_AMMO_EFFECT.translate().withStyle(LTXIConstants.REM_BLUE.chatStyle()));
            case INFINITE -> lines.accept(LTXILangKeys.INFINITE_AMMO_EFFECT.translate().withStyle(LTXIConstants.CREATIVE_PINK.chatStyle()));
        }
    }
}