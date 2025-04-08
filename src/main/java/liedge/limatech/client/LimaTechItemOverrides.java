package liedge.limatech.client;

import liedge.limatech.item.tool.EnergyFishingRodItem;
import liedge.limatech.registry.game.LimaTechItems;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static liedge.limatech.LimaTech.RESOURCES;

public final class LimaTechItemOverrides
{
    private LimaTechItemOverrides() {}

    public static final ResourceLocation FISHING_ROD_CAST = RESOURCES.location("cast");
    public static final ResourceLocation BRUSH_BRUSHING = RESOURCES.location("brushing");

    static void registerOverrides()
    {
        ItemProperties.register(LimaTechItems.LTX_FISHING_ROD.get(), FISHING_ROD_CAST, (stack, level, entity, seed) -> {
            if(entity == null) return 0f;
            else
            {
                boolean holdingMainHand = entity.getMainHandItem() == stack;
                boolean holdingOffHand = entity.getOffhandItem() == stack;
                if (entity.getMainHandItem().getItem() instanceof EnergyFishingRodItem) holdingOffHand = false;

                return (holdingMainHand || holdingOffHand) && entity instanceof Player player && player.fishing != null ? 1f : 0f;
            }
        });

        ItemProperties.register(LimaTechItems.LTX_BRUSH.get(), BRUSH_BRUSHING, (stack, level, entity, seed) -> entity != null && entity.getUseItem() == stack ? (entity.getUseItemRemainingTicks() % 10) / 10f : 0f);
    }
}