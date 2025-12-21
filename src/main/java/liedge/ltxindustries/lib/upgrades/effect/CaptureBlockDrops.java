package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import liedge.ltxindustries.LTXIConstants;
import liedge.ltxindustries.client.LTXILangKeys;
import liedge.ltxindustries.lib.upgrades.tooltip.UpgradeTooltipsProvider;
import liedge.ltxindustries.util.LTXITooltipUtil;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.util.function.Consumer;

public record CaptureBlockDrops(HolderSet<Item> items) implements UpgradeTooltipsProvider
{
    public static final Codec<CaptureBlockDrops> CODEC = RegistryCodecs.homogeneousList(Registries.ITEM).xmap(CaptureBlockDrops::new, CaptureBlockDrops::items);

    public static CaptureBlockDrops captureItems(HolderSet<Item> items)
    {
        return new CaptureBlockDrops(items);
    }

    @Override
    public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
    {
        lines.accept(LTXILangKeys.CAPTURE_BLOCK_DROPS_EFFECT.translateArgs(LTXITooltipUtil.translateHolderSet(items)).withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
    }
}