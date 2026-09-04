package liedge.ltxindustries.item;

import liedge.ltxindustries.util.LTXIChatStyles;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

public final class LTXIItemRarities
{
    private LTXIItemRarities() {}

    public static final EnumProxy<Rarity> LTX_RARITY = new EnumProxy<>(Rarity.class, -1, "ltxi:ltx", LTXIChatStyles.LIME_GREEN);

    public static Rarity ltxGearRarity()
    {
        return LTX_RARITY.getValue();
    }
}