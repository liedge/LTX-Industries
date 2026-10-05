package liedge.ltxindustries.client.model.entity;

import liedge.ltxindustries.LTXIndustries;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorModelSet;

public final class LTXIModelLayers
{
    private LTXIModelLayers() {}

    public static final ModelLayerLocation GLOWSTICK_PROJECTILE = base("glowstick_projectile");
    public static final ModelLayerLocation SHELL_GRENADE_BASE = base("shell_grenade");
    public static final ModelLayerLocation SHELL_GRENADE_EMISSIVE = emissive("shell_grenade");
    public static final ModelLayerLocation SMALL_ROCKET_BASE = base("small_rocket");
    public static final ModelLayerLocation SMALL_ROCKET_EMISSIVE = emissive("small_rocket");
    public static final ModelLayerLocation CLUSTER_MUNITION = base("cluster_munition");
    public static final ModelLayerLocation SEEKER_MINE_BASE = base("seeker_mine");
    public static final ModelLayerLocation SEEKER_MINE_EMISSIVE = emissive("seeker_mine");

    private static final String WONDERLAND_ID = "wonderland_armor";
    public static final ArmorModelSet<ModelLayerLocation> WONDERLAND_BASE = wonderlandSet("head", "chest", "legs", "feet");
    public static final ArmorModelSet<ModelLayerLocation> WONDERLAND_EMISSIVE = wonderlandSet("head_emissive", "chest_emissive", "legs_emissive", "feet_emissive");
    public static final ModelLayerLocation WONDERLAND_VISOR = layer(WONDERLAND_ID, "visor");

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

    private static ArmorModelSet<ModelLayerLocation> wonderlandSet(String head, String chest, String legs, String feet)
    {
        return new ArmorModelSet<>(layer(WONDERLAND_ID, head), layer(WONDERLAND_ID, chest), layer(WONDERLAND_ID, legs), layer(WONDERLAND_ID, feet));
    }
}