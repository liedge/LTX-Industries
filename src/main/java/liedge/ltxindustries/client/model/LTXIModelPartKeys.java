package liedge.ltxindustries.client.model;

import liedge.limacore.client.model.StaticQuads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import static liedge.ltxindustries.LTXIndustries.RESOURCES;

public final class LTXIModelPartKeys
{
    private LTXIModelPartKeys() {}

    public static final StandaloneModelKey<StaticQuads> GRINDER_FRONT_CRUSHER = key("grinder_front_crusher");
    public static final StandaloneModelKey<StaticQuads> GRINDER_REAR_CRUSHER = key("grinder_rear_crusher");
    public static final StandaloneModelKey<StaticQuads> ELECTROCENTRIFUGE_TUBES = key("electrocentrifuge_tubes");
    public static final StandaloneModelKey<StaticQuads> MIXER_BLADES = key("mixer_blades");

    public static final StandaloneModelKey<StaticQuads> ARC_TURRET_SWIVEL = key("arc_turret_swivel");
    public static final StandaloneModelKey<StaticQuads> ARC_TURRET_WEAPONS = key("arc_turret_weapons");
    public static final StandaloneModelKey<StaticQuads> ROCKET_TURRET_SWIVEL = key("rocket_turret_swivel");
    public static final StandaloneModelKey<StaticQuads> ROCKET_TURRET_WEAPONS = key("rocket_turret_weapons");
    public static final StandaloneModelKey<StaticQuads> RAILGUN_TURRET_SWIVEL = key("railgun_turret_swivel");
    public static final StandaloneModelKey<StaticQuads> RAILGUN_TURRET_WEAPONS = key("railgun_turret_weapons");

    public static QuadCollection get(StandaloneModelKey<QuadCollection> key)
    {
        QuadCollection collection = Minecraft.getInstance().getModelManager().getStandaloneModel(key);
        return collection != null ? collection : QuadCollection.EMPTY;
    }

    public static void register(ModelEvent.RegisterStandalone event)
    {
        registerBEPart(event, GRINDER_FRONT_CRUSHER);
        registerBEPart(event, GRINDER_REAR_CRUSHER);
        registerBEPart(event, ELECTROCENTRIFUGE_TUBES);
        registerBEPart(event, MIXER_BLADES);
        registerBEPart(event, ARC_TURRET_SWIVEL);
        registerBEPart(event, ARC_TURRET_WEAPONS);
        registerBEPart(event, ROCKET_TURRET_SWIVEL);
        registerBEPart(event, ROCKET_TURRET_WEAPONS);
        registerBEPart(event, RAILGUN_TURRET_SWIVEL);
        registerBEPart(event, RAILGUN_TURRET_WEAPONS);
    }

    private static void registerBEPart(ModelEvent.RegisterStandalone event, StandaloneModelKey<StaticQuads> key)
    {
        event.register(key, StaticQuads.create(RESOURCES.id("block_entity/" + key.getName())));
    }

    private static <T> StandaloneModelKey<T> key(String name)
    {
        return new StandaloneModelKey<>(() -> name);
    }
}