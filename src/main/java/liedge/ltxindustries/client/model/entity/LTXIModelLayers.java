package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class LTXIModelLayers
{
    private LTXIModelLayers() {}

    private static ModelLayerLocation mainLayer(String name)
    {
        return new ModelLayerLocation(LTXIndustries.RESOURCES.location(name), "main");
    }

    public static final ModelLayerLocation GLOWSTICK_PROJECTILE = mainLayer("glowstick_projectile");
    public static final ModelLayerLocation ROCKET = mainLayer("rocket");
    public static final ModelLayerLocation ORB_GRENADE = mainLayer("orb_grenade");
    public static final ModelLayerLocation WONDERLAND_ARMOR_SET = mainLayer("wonderland_armor");
}