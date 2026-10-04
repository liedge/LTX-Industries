package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class LTXIModelLayers
{
    private LTXIModelLayers() {}

    private static ModelLayerLocation layer(String name, String layer)
    {
        return new ModelLayerLocation(LTXIndustries.RESOURCES.id(name), layer);
    }

    private static ModelLayerLocation base(String name)
    {
        return layer(name, "main");
    }

    private static ModelLayerLocation emissive(String name)
    {
        return layer(name, "emissive");
    }

    public static final ModelLayerLocation GLOWSTICK_PROJECTILE = base("glowstick_projectile");
    public static final ModelLayerLocation SHELL_GRENADE_BASE = base("shell_grenade");
    public static final ModelLayerLocation SHELL_GRENADE_EMISSIVE = emissive("shell_grenade");
    public static final ModelLayerLocation SMALL_ROCKET_BASE = base("small_rocket");
    public static final ModelLayerLocation SMALL_ROCKET_EMISSIVE = emissive("small_rocket");
    public static final ModelLayerLocation WONDERLAND_ARMOR_SET = base("wonderland_armor");
}