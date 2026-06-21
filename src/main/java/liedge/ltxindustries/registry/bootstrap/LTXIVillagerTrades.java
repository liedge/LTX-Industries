package liedge.ltxindustries.registry.bootstrap;

import liedge.ltxindustries.LTXIndustries;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.VillagerTrade;

import java.util.List;
import java.util.Optional;

public final class LTXIVillagerTrades
{
    private LTXIVillagerTrades() { }

    public static final ResourceKey<VillagerTrade> FARMER_EMERALD_SPARK_FRUIT = key("farmer/emerald_spark_fruit");
    public static final ResourceKey<VillagerTrade> TRADER_EMERALD_SPARK_FRUIT = key("wandering_trader/emerald_spark_fruit");

    private static ResourceKey<VillagerTrade> key(String name)
    {
        return LTXIndustries.RESOURCES.resourceKey(Registries.VILLAGER_TRADE, name);
    }

    public static void bootstrap(BootstrapContext<VillagerTrade> context)
    {
        context.register(FARMER_EMERALD_SPARK_FRUIT,
                simpleTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(LTXIItems.SPARK_FRUIT, 2), 12, 1, 0.05f));
        context.register(TRADER_EMERALD_SPARK_FRUIT,
                simpleTrade(new TradeCost(Items.EMERALD, 1), new ItemStackTemplate(LTXIItems.SPARK_FRUIT, 1), 6, 1, 0.05f));
    }

    private static VillagerTrade simpleTrade(TradeCost wants, ItemStackTemplate gives, int maxUses, int xp, float reputationDiscount)
    {
        return new VillagerTrade(wants, gives, maxUses, xp, reputationDiscount, Optional.empty(), List.of());
    }
}