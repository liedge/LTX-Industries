package liedge.ltxindustries.data.generation;

import liedge.limacore.data.generation.LimaTagsProvider;
import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.bootstrap.LTXIVillagerTrades;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.VillagerTradeTags;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.concurrent.CompletableFuture;

class TradeTagsGen extends LimaTagsProvider<VillagerTrade>
{
    TradeTagsGen(PackOutput output, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(output, Registries.VILLAGER_TRADE, LTXIndustries.MODID, registries);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries)
    {
        buildTag(VillagerTradeTags.FARMER_LEVEL_1).add(LTXIVillagerTrades.FARMER_EMERALD_SPARK_FRUIT);
        buildTag(VillagerTradeTags.WANDERING_TRADER_COMMON).add(LTXIVillagerTrades.TRADER_EMERALD_SPARK_FRUIT);
    }
}