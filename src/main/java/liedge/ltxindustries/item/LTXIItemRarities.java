package liedge.ltxindustries.item;

import liedge.ltxindustries.LTXIConstants;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public final class LTXIItemRarities
{
    private LTXIItemRarities() {}

    public static final EnumProxy<Rarity> LTX_RARITY = new EnumProxy<>(Rarity.class, -1, "ltxi:ltx", (UnaryOperator<Style>) LTXIConstants.LIME_GREEN::applyChatStyle);

    public static Rarity ltxGearRarity()
    {
        return LTX_RARITY.getValue();
    }
}