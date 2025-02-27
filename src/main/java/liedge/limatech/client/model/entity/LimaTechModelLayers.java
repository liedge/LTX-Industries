package liedge.limatech.client.model.entity;

import liedge.limatech.LimaTech;
import net.minecraft.client.model.geom.ModelLayerLocation;

public final class LimaTechModelLayers
{
    private LimaTechModelLayers() {}

    private static ModelLayerLocation mainLayer(String name)
    {
        return new ModelLayerLocation(LimaTech.RESOURCES.location(name), "main");
    }

    public static final ModelLayerLocation ROCKET = mainLayer("rocket");
    public static final ModelLayerLocation ORB_GRENADE = mainLayer("orb_grenade");
}