package liedge.ltxindustries.lib.upgrades.effect;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
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

public interface CaptureLoot extends UpgradeTooltipsProvider
{
    static MobDrops mobDrops()
    {
        return MobDrops.INSTANCE;
    }

    static BlockDrops blockDrops(HolderSet<Item> items)
    {
        return new BlockDrops(items);
    }

    static MobExperience mobExperience()
    {
        return MobExperience.INSTANCE;
    }

    final class MobDrops implements CaptureLoot
    {
        public static final MobDrops INSTANCE = new MobDrops();
        public static final Codec<MobDrops> CODEC = MapCodec.unitCodec(INSTANCE);

        private MobDrops() { }

        @Override
        public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
        {
            lines.accept(LTXILangKeys.CAPTURE_MOB_DROPS_EFFECT.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
        }
    }

    record BlockDrops(HolderSet<Item> items) implements CaptureLoot
    {
        public static final Codec<BlockDrops> CODEC = RegistryCodecs.homogeneousList(Registries.ITEM).xmap(BlockDrops::new, BlockDrops::items);

        @Override
        public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
        {
            lines.accept(LTXILangKeys.CAPTURE_BLOCK_DROPS_EFFECT.translateArgs(LTXITooltipUtil.translateHolderSet(items)).withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
        }
    }

    final class MobExperience implements CaptureLoot
    {
        public static final MobExperience INSTANCE = new MobExperience();
        public static final Codec<MobExperience> CODEC = MapCodec.unitCodec(INSTANCE);

        private MobExperience() { }

        @Override
        public void addUpgradeTooltips(int upgradeRank, Consumer<Component> lines)
        {
            lines.accept(LTXILangKeys.CAPTURE_MOB_EXPERIENCE_EFFECT.translate().withStyle(LTXIConstants.LIME_GREEN.chatStyle()));
        }
    }
}