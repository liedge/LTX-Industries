package liedge.limatech.item;

import liedge.limatech.LimaTechConstants;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Rarity;
import net.neoforged.fml.common.asm.enumextension.EnumProxy;

import java.util.function.UnaryOperator;

public final class LimaTechRarities
{
    private LimaTechRarities() {}

    public static final EnumProxy<Rarity> LTX_RARITY = new EnumProxy<>(Rarity.class, -1, "limatech:ltx", (UnaryOperator<Style>) LimaTechConstants.LIME_GREEN::applyChatStyle);

    public static Rarity ltxGearRarity()
    {
        return LTX_RARITY.getValue();
    }
}