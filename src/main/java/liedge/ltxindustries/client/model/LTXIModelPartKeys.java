package liedge.ltxindustries.client.model;

import com.google.common.base.Preconditions;
import liedge.ltxindustries.LTXIndustries;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.model.standalone.StandaloneModelKey;

import java.util.Arrays;
import java.util.List;

public final class LTXIModelPartKeys
{
    private LTXIModelPartKeys() {}

    public static final StandaloneModelKey<LayeredModelPart> ELECTROCENTRIFUGE_TUBES = key("electrocentrifuge_tubes");
    public static final StandaloneModelKey<LayeredModelPart> GRINDER_FRONT_CRUSHER = key("grinder_front_crusher");
    public static final StandaloneModelKey<LayeredModelPart> GRINDER_REAR_CRUSHER = key("grinder_rear_crusher");
    public static final StandaloneModelKey<LayeredModelPart> MIXER_BLADES = key("mixer_blades");

    public static final StandaloneModelKey<LayeredModelPart> ARC_TURRET_SWIVEL = key("arc_turret_swivel");
    public static final StandaloneModelKey<LayeredModelPart> ARC_TURRET_WEAPONS = key("arc_turret_weapons");
    public static final StandaloneModelKey<LayeredModelPart> ROCKET_TURRET_SWIVEL = key("rocket_turret_swivel");
    public static final StandaloneModelKey<LayeredModelPart> ROCKET_TURRET_WEAPONS = key("rocket_turret_weapons");
    public static final StandaloneModelKey<LayeredModelPart> RAILGUN_TURRET_SWIVEL = key("railgun_turret_swivel");
    public static final StandaloneModelKey<LayeredModelPart> RAILGUN_TURRET_WEAPONS = key("railgun_turret_weapons");

    public static void register(ModelEvent.RegisterStandalone event)
    {
    }

    private static void registerBlockVariants(ModelEvent.RegisterStandalone event, StandaloneModelKey<LayeredModelPart> key, String... variants)
    {
        registerVariants(event, "block/", key, variants);
    }

    private static void registerItemVariants(ModelEvent.RegisterStandalone event, StandaloneModelKey<LayeredModelPart> key, String... variants)
    {
        registerVariants(event, "item/", key, variants);
    }

    private static void registerVariants(ModelEvent.RegisterStandalone event, String prefix, StandaloneModelKey<LayeredModelPart> key, String... variants)
    {
        String name = key.getName();
        List<Identifier> modelIds = Arrays.stream(variants).map(variant -> LTXIndustries.RESOURCES.id(prefix + name + "_" + variant)).toList();
        Preconditions.checkState(!modelIds.isEmpty(), "Model " + name + " must contain at least 1 valid variant");

        event.register(key, LayeredModelPart.create(modelIds));
    }

    private static StandaloneModelKey<LayeredModelPart> key(String name)
    {
        return new StandaloneModelKey<>(() -> name);
    }
}