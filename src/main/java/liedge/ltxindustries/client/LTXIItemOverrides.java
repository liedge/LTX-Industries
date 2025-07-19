package liedge.ltxindustries.client;

import liedge.ltxindustries.item.tool.EnergyFishingRodItem;
import liedge.ltxindustries.registry.game.LTXIItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIItemOverrides
{
    private LTXIItemOverrides() {}

    public static final ResourceLocation FISHING_ROD_CAST = RESOURCES.location("cast");
    public static final ResourceLocation BRUSH_BRUSHING = RESOURCES.location("brushing");

    static void registerOverrides()
    {
        ItemProperties.register(LTXIItems.LTX_FISHING_ROD.get(), FISHING_ROD_CAST, (stack, level, entity, seed) -> {
            if(entity == null) return 0f;
            else
            {
                boolean holdingMainHand = entity.getMainHandItem() == stack;
                boolean holdingOffHand = entity.getOffhandItem() == stack;
                if (entity.getMainHandItem().getItem() instanceof EnergyFishingRodItem) holdingOffHand = false;

                return (holdingMainHand || holdingOffHand) && entity instanceof Player player && player.fishing != null ? 1f : 0f;
            }
        });

        ItemProperties.register(LTXIItems.LTX_BRUSH.get(), BRUSH_BRUSHING, (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack ? (entity.getUseItemRemainingTicks() % 10) / 10f : 0f);
    }
}