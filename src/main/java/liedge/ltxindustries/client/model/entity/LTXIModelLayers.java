package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class LTXIModelLayers
{
    private LTXIModelLayers() {}

    private static ModelLayerLocation mainLayer(String name)
    {
        return new ModelLayerLocation(LTXIndustries.RESOURCES.id(name), "main");
    }

    public static final ModelLayerLocation GLOWSTICK_PROJECTILE = mainLayer("glowstick_projectile");
    public static final ModelLayerLocation SHELL_GRENADE = mainLayer("shell_grenade");
    public static final ModelLayerLocation SMALL_ROCKET = mainLayer("small_rocket");
    public static final ModelLayerLocation WONDERLAND_ARMOR_SET = mainLayer("wonderland_armor");
}